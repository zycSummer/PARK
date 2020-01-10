package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.VResult;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.QueryBody;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.SampleDataResponse;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.ApiService;
import com.jet.cloud.deepmind.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zhuyicheng
 * @create 2019/11/25 10:11
 * @desc api服务实现类
 */
@Service
public class ApiServiceImpl implements ApiService {
    @Autowired
    private CommonRepo commonRepo;
    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private SiteRepo siteRepo;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MeterRepo meterRepo;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${huawei.api.url}")
    private String apiUrl;

    @Override
    public VResult queryMeter(String parkId, String meterId) {
        /**
         * 如果有传仪表标识，则根据以下SQL查询结果
         * RTHGCC.W1-8
         * select CONCAT(obj_id,'.',meter_id) meter_id, meter_name, energy_type_id, memo from `tb_obj_meter` where obj_type = 'SITE' AND CONCAT(obj_id,'.',meter_id) = ?;
         */
        try {
            if (meterId != null && !Objects.equals("", meterId)) {
                Meter meter = null;
                try {
                    meter = commonRepo.queryMeterByMeterId(meterId);
                } catch (Exception e) {
                    ;
                }
                MeterVO meterVO = new MeterVO();
                if (meter != null) {
                    meterVO.setMeterId(meter.getMeterId());
                    meterVO.setMeterName(meter.getMeterName());
                    meterVO.setEnergyTypeId(meter.getEnergyTypeId());
                    meterVO.setMemo(meter.getMemo());
                }
                return new VResult<>("0", null, meterVO);
            } else {
                /**
                 *如果没有传仪表标识，则根据以下SQL查询结果
                 * select CONCAT(obj_id, '.', meter_id) meter_id, meter_name, energy_type_id, memo from `tb_obj_meter`
                 where obj_type = 'SITE';
                 *
                 *
                 *正常返回结果中返回码为0，返回消息为NULL
                 */
                List<Meter> meters = null;
                try {
                    meters = commonRepo.queryMeter();
                } catch (Exception e) {
                    ;
                }
                List<MeterVO> meterVOS = new ArrayList<>();
                if (StringUtils.isNotNullAndEmpty(meters)) {
                    for (Meter meter : meters) {
                        MeterVO meterVO = new MeterVO();
                        meterVO.setMeterId(meter.getMeterId());
                        meterVO.setMeterName(meter.getMeterName());
                        meterVO.setEnergyTypeId(meter.getEnergyTypeId());
                        meterVO.setMemo(meter.getMemo());
                        meterVOS.add(meterVO);
                    }
                }
                return new VResult<>("0", null, meterVOS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new VResult<>("500", null, e);
        }
    }

    @Override
    public VResult queryMeterLastValue(String parkId, String meterId) {
        try {
            MeterLastValueVO meterLastValueVO = new MeterLastValueVO();
            Long timeOutValue = commonService.getTimeOutValue();
            /**
             * 园区标识 暂时忽略
             * 1. SELECT rtdb_tenant_id FROM tb_park LIMIT 1;
             */
            Park park = parkRepo.findFirstPark();
            String rtdbTenantParkId = park.getRtdbTenantId();
            /**
             * 2. 将传入的仪表标识按点号分割，取前半部分作为site_id, 然后按如下SQL查询
             * SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;
             *
             * RTHGCC.W1-8
             */
            String siteId = meterId.split("\\.")[0];
            Site site = siteRepo.findBySiteId(siteId);
            if (site != null) {
                // LYGSHCYJD.DGWSCL.E1-1-2.P
                String rtdbProjectSiteId = site.getRtdbProjectId();
                /**
                 * 3. 根据以下SQL并结合传来的仪表标识查询仪表信息
                 * SELECT energy_type_id FROM `tb_obj_meter` WHERE obj_type = 'SITE' AND CONCAT(obj_id,'.',meter_id) = ?;
                 */
                Meter meter = null;
                try {
                    meter = commonRepo.queryMeterTypeByMeterId(meterId);
                } catch (Exception e) {
                    ;
                }
                if (meter != null) {
                    String energyTypeId = meter.getEnergyTypeId();
                    String metId = meter.getMeterId();
                    String meterName = meter.getMeterName();
                    meterLastValueVO.setMeterId(meterId);
                    meterLastValueVO.setMeterName(meterName);
                    meterLastValueVO.setEnergyTypeId(energyTypeId);
                    /**
                     * 4. 根据查询结果中的仪表所属的能源种类标识到系统能源参数表表中查询对应的参数列表
                     * SELECT energy_para_id FROM `tb_sys_energy_para` WHERE energy_type_id = ?;
                     */
                    List<SysEnergyPara> sysEnergyParas = sysEnergyParaRepo.findByEnergyTypeIdOrderBySortId(energyTypeId);
                    if (StringUtils.isNotNullAndEmpty(sysEnergyParas)) {
                        Map<String, String> pointsMap = new HashMap<>();
                        for (SysEnergyPara sysEnergyPara : sysEnergyParas) {
                            StringJoiner joiner = new StringJoiner(".");
                            String energyParaId = sysEnergyPara.getEnergyParaId();
                            String pointId = joiner.add(rtdbTenantParkId).add(rtdbProjectSiteId).add(metId).add(energyParaId).toString();
                            pointsMap.put(energyParaId, pointId);
                        }
                        /**
                         * 构成需要去实时库查询最新值的多个测点去实时库查询，然后按照返回结果格式要求进行返回。
                         * 返回结果中仪表最新采集数据时间 取当前仪表所有参数的最新值时间戳最大的那个
                         */
                        List<String> pointIds = new ArrayList<String>(pointsMap.values());
                        List<SampleDataResponse> sampleDataResponses = kairosdbClient.queryLast(pointIds, timeOutValue);
                        List<Long> timestamps = new ArrayList<>();
                        Map<String, Double> meterLastValueObj = new HashMap<>();
                        for (SampleDataResponse sampleDataRespons : sampleDataResponses) {
                            Double value = sampleDataRespons.getValue();
                            String point = sampleDataRespons.getPoint();
                            for (String energyParaId : pointsMap.keySet()) {
                                String pointId = pointsMap.get(energyParaId);
                                if (Objects.equals(pointId, point)) {
                                    meterLastValueObj.put(energyParaId, value);
                                    break;
                                }
                            }
                            Long timestamp = sampleDataRespons.getTimestamp();
                            timestamps.add(timestamp);
                        }
                        if (StringUtils.isNotNullAndEmpty(timestamps)) {
                            Long max = querMax(timestamps);
                            meterLastValueVO.setMeterLastTime(max);
                        } else {
                            meterLastValueVO.setMeterLastTime(null);
                        }
                        meterLastValueVO = queryMeterData(energyTypeId, meterLastValueObj, meterLastValueVO);
                        return new VResult<>("0", null, meterLastValueVO);
                    } else {
                        return new VResult<>("105", "此仪表无参数", meterLastValueVO);
                    }
                } else {
                    return new VResult<>("104", "无此仪表", meterLastValueVO);
                }
            } else {
                return new VResult<>("104", "无此仪表", meterLastValueVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new VResult<>("500", null, e);
        }
    }

    @Override
    public VResult queryMeterHisValue(String parkId, String meterId, String paraId, Long startTime, Long endTime, Integer interval) {
        try {
            MeterHisValueVO meterHisValueVO = new MeterHisValueVO();
            // 秒
            if (interval == null) interval = 300;
            /**
             * 园区标识 暂时忽略
             * 1. SELECT rtdb_tenant_id FROM tb_park LIMIT 1;
             */
            Park park = parkRepo.findFirstPark();
            String rtdbTenantParkId = park.getRtdbTenantId();
            /**
             * 2. 将传入的仪表标识按点号分割，取前半部分作为site_id, 然后按如下SQL查询
             * SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;
             *
             * RTHGCC.W1-8
             */
            String siteId = meterId.split("\\.")[0];
            String metId = meterId.split("\\.")[1];// 数据库里面的仪表标识
            Site site = siteRepo.findBySiteId(siteId);
            if (site != null) {
                // LYGSHCYJD.DGWSCL.E1-1-2.P
                String rtdbProjectSiteId = site.getRtdbProjectId();
                /**
                 * 3. 取传入的仪表标识按点号分割的后半部分
                 */
                Meter meter = meterRepo.findByObjTypeAndObjIdAndMeterIdOrderBySortId("SITE", siteId, metId);
                if (meter != null) {
                    String energyTypeId = meter.getEnergyTypeId();
                    String meterName = meter.getMeterName();

                    meterHisValueVO.setMeterId(meterId);
                    meterHisValueVO.setMeterName(meterName);
                    meterHisValueVO.setEnergyTypeId(energyTypeId);
                    meterHisValueVO.setParaId(paraId);
                } else {
                    meterHisValueVO.setMeterId(meterId);
                    meterHisValueVO.setMeterName(null);
                    meterHisValueVO.setEnergyTypeId(null);
                    meterHisValueVO.setParaId(paraId);
                }
                /**
                 * 4. 取传入的参数标识
                 */
                StringJoiner joiner = new StringJoiner(".");
                String pointId = joiner.add(rtdbTenantParkId).add(rtdbProjectSiteId).add(metId).add(paraId).toString();

                /**
                 * 以上4步骤结果用点号连接，构成需要去实时库查询的测点，并按照传入的开始结束时间以及时间间隔去实时库查询，
                 * 然后按照返回结果格式要求进行返回。
                 */
                SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryHis(pointId, startTime, endTime, interval, TimeUnit.SECONDS);
                meterHisValueVO.setHisTime(sampleData4KairosResp.getTimestamps());
                meterHisValueVO.setHisData(sampleData4KairosResp.getValues());
                return new VResult<>("0", null, meterHisValueVO);
            } else {
                return new VResult<>("104", "无此仪表", meterHisValueVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new VResult<>("500", null, e);
        }
    }

    @Override
    public VResult queryMeterUsageValue(String parkId, String meterId, String date) {
        try {
            MeterUsageValueVO meterUsageValueVO = new MeterUsageValueVO();
            /**
             * 园区标识 暂时忽略
             * 1. SELECT rtdb_tenant_id FROM tb_park LIMIT 1;
             */
            Park park = parkRepo.findFirstPark();
            String rtdbTenantParkId = park.getRtdbTenantId();
            /**
             * 2. 将传入的仪表标识按点号分割，取前半部分作为site_id, 然后按如下SQL查询
             * SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;
             *
             * RTHGCC.W1-8
             */
            String siteId = meterId.split("\\.")[0];
            String metId = meterId.split("\\.")[1];// 数据库里面的仪表标识
            Site site = siteRepo.findBySiteId(siteId);
            if (site != null) {
                // LYGSHCYJD.DGWSCL.E1-1-2.P
                String rtdbProjectSiteId = site.getRtdbProjectId();
                /**
                 * 3. 取传入的仪表标识按点号分割的后半部分
                 */
                Meter meter = meterRepo.findByObjTypeAndObjIdAndMeterIdOrderBySortId("SITE", siteId, metId);
                meterUsageValueVO.setMeterId(meterId);
                if (meter != null) {
                    String energyTypeId = meter.getEnergyTypeId();
                    String meterName = meter.getMeterName();
                    meterUsageValueVO.setMeterName(meterName);
                    meterUsageValueVO.setEnergyTypeId(energyTypeId);
                } else {
                    meterUsageValueVO.setMeterName(null);
                    meterUsageValueVO.setEnergyTypeId(null);
                }

                /**
                 * 4. 根据以下SQL并结合传入的仪表参数 获取参数标识
                 * SELECT energy_usage_para_id FROM tb_sys_energy_type WHERE energy_type_id = (SELECT energy_type_id FROM tb_obj_meter WHERE obj_type = 'SITE' AND CONCAT(obj_id,'.',meter_id) = ? )
                 */
                String energyUsageParaId = null;
                try {
                    energyUsageParaId = commonRepo.queryEnergyUsageParaIdByMeterId(meterId);
                } catch (Exception e) {
                    ;
                }

                /**
                 * 以上4步骤结果 用点号连接，构成需要去实时库查询的测点，并按照传入的日期去实时库查询此日期每1小时的差值数据，然后按照返回结果格式要求进行返回。
                 */
                StringJoiner joiner = new StringJoiner(".");
                if (energyUsageParaId != null) {
                    String pointId = joiner.add(rtdbTenantParkId).add(rtdbProjectSiteId).add(metId).add(energyUsageParaId).toString();
                    LocalDateTime startTime = DateUtil.longToLocalTime(DateUtil.stringToLong(date + " 00:00:00"));
                    LocalDateTime endTime = startTime.plusDays(1);
                    SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryHis(pointId, startTime, endTime, 1, TimeUnit.HOURS);
                    List<Double> values = sampleData4KairosResp.getValues();
                    String msg = sampleData4KairosResp.getMsg();
                    if (Objects.equals(msg, "此测点不存在") && !StringUtils.isNotNullAndEmpty(values)) {
                        return new VResult<>("102", "此仪表无用量参数!", meterUsageValueVO);
                    }
                    meterUsageValueVO.setUsageHourly(sampleData4KairosResp.getValues());
                } else {
                    meterUsageValueVO.setUsageHourly(null);
                }
                return new VResult<>("0", null, meterUsageValueVO);
            } else {
                return new VResult<>("104", "无此仪表", meterUsageValueVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new VResult<>("500", null, e);
        }
    }

    @Override
    public VResult queryMeterNextMaxLoadValue(String parkId, String meterId) {
        try {
            MeterNextMaxLoadValueVO meterNextMaxLoadValueVO = new MeterNextMaxLoadValueVO();
            /**
             * 园区标识 暂时忽略
             * 1. SELECT rtdb_tenant_id FROM tb_park LIMIT 1;
             */
            Park park = parkRepo.findFirstPark();
            String rtdbTenantParkId = park.getRtdbTenantId();
            /**
             * 2. 将传入的仪表标识按点号分割，取前半部分作为site_id, 然后按如下SQL查询
             * SELECT rtdb_project_id FROM `tb_site` WHERE site_id = ?;
             *
             * RTHGCC.W1-8
             */
            String siteId = meterId.split("\\.")[0];
            // 3. 取传入的仪表标识按点号分割的后半部分
            String metId = meterId.split("\\.")[1];// 数据库里面的仪表标识
            Site site = siteRepo.findBySiteId(siteId);
            if (site != null) {
                // LYGSHCYJD.DGWSCL.E1-1-2.P
                String rtdbProjectSiteId = site.getRtdbProjectId();

                Meter meter = meterRepo.findByObjTypeAndObjIdAndMeterIdOrderBySortId("SITE", siteId, metId);
                if (meter != null) {
                    String energyTypeId = meter.getEnergyTypeId();
                    String meterName = meter.getMeterName();
                    meterNextMaxLoadValueVO.setMeterId(meterId);
                    meterNextMaxLoadValueVO.setMeterName(meterName);
                    meterNextMaxLoadValueVO.setEnergyTypeId(energyTypeId);
                } else {
                    meterNextMaxLoadValueVO.setMeterId(meterId);
                    meterNextMaxLoadValueVO.setMeterName(null);
                    meterNextMaxLoadValueVO.setEnergyTypeId(null);
                }
                /**
                 * 4. 根据以下SQL并结合传入的仪表参数 获取参数标识
                 * select energy_load_para_id from tb_sys_energy_type WHERE energy_type_id =
                 * (SELECT energy_type_id FROM tb_obj_meter WHERE obj_type = 'SITE' AND CONCAT(obj_id,'.',meter_id) = ? )
                 */
                String energyLoadParaId = null;
                try {
                    energyLoadParaId = commonRepo.queryMeterNextMaxLoadValueByMeterId(meterId);
                } catch (Exception e) {
                    ;
                }
                /**
                 * 以上4步骤结果 用点号连接，去实时库查询此测点往前1年的历史数据，5分钟
                 */
                StringJoiner joiner = new StringJoiner(".");
                if (energyLoadParaId != null) {
                    String pointId = joiner.add(rtdbTenantParkId).add(rtdbProjectSiteId).add(metId).add(energyLoadParaId).toString();
                    LocalDateTime endTime = LocalDateTime.now();
                    LocalDateTime startTime = endTime.minusYears(1);
                    SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryHis(pointId, startTime, endTime, 5, TimeUnit.MINUTES);
                    List<Double> values = sampleData4KairosResp.getValues();
                    List<Long> timestamps = sampleData4KairosResp.getTimestamps();
                    /**
                     * 传入刚查询的历史数据，接口返回明天最大负荷预测值。
                     */
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamps", timestamps);
                    map.put("values", values);
                    HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map);
                    JSONObject jsonObject = restTemplate.postForEntity(apiUrl, httpEntity, JSONObject.class).getBody();
                    Double maxValue = jsonObject.getDouble("max_value");
                    meterNextMaxLoadValueVO.setMaxLoadValue(maxValue);
                } else {
                    meterNextMaxLoadValueVO.setMaxLoadValue(null);
                }
                return new VResult<>("0", null, meterNextMaxLoadValueVO);
            } else {
                return new VResult<>("104", "无此仪表", meterNextMaxLoadValueVO);
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return new VResult<>("500", null, e);
        }
    }

    private MeterLastValueVO queryMeterData(String energyTypeId, Map<String, Double> meterLastValueObj, MeterLastValueVO meterLastValueVO) {
        if (Objects.equals(energyTypeId, "electricity")) {
            Double ua = meterLastValueObj.get("Ua");
            Double ub = meterLastValueObj.get("Ub");
            Double uc = meterLastValueObj.get("Uc");
            Double uab = meterLastValueObj.get("Uab");
            Double ubc = meterLastValueObj.get("Ubc");
            Double uca = meterLastValueObj.get("Uca");
            Double ia = meterLastValueObj.get("Ia");
            Double ib = meterLastValueObj.get("Ib");
            Double ic = meterLastValueObj.get("Ic");
            Double pa = meterLastValueObj.get("Pa");
            Double pb = meterLastValueObj.get("Pb");
            Double pc = meterLastValueObj.get("Pc");
            Double p = meterLastValueObj.get("P");
            Double qa = meterLastValueObj.get("Qa");
            Double qb = meterLastValueObj.get("Qb");
            Double qc = meterLastValueObj.get("Qc");
            Double q = meterLastValueObj.get("Q");
            Double sa = meterLastValueObj.get("Sa");
            Double sb = meterLastValueObj.get("Sb");
            Double sc = meterLastValueObj.get("Sc");
            Double s = meterLastValueObj.get("S");
            Double pFa = meterLastValueObj.get("PFa");
            Double pFb = meterLastValueObj.get("PFb");
            Double pFc = meterLastValueObj.get("PFc");
            Double pf = meterLastValueObj.get("PF");
            Double epImp = meterLastValueObj.get("Ep_imp");
            Double epExp = meterLastValueObj.get("Ep_exp");
            Double eqImp = meterLastValueObj.get("Eq_imp");
            Double eqExp = meterLastValueObj.get("Eq_exp");

            ElectricityVO electricityVO = new ElectricityVO(ua, ub, uc, uab, ubc, uca, ia, ib, ic, pa, pb, pc, p, qa, qb, qc, q, sa, sb, sc, s, pFa, pFb, pFc, pf, epImp, epExp, eqImp, eqExp);
            meterLastValueVO.setMeterLastValue(electricityVO);
        } else if (Objects.equals(energyTypeId, "water")) {
            Double totalflow = meterLastValueObj.get("Totalflow");
            Double flowrate = meterLastValueObj.get("Flowrate");
            Double temp = meterLastValueObj.get("Temp");

            WaterVO waterVO = new WaterVO(totalflow, flowrate, temp);
            meterLastValueVO.setMeterLastValue(waterVO);
        } else if (Objects.equals(energyTypeId, "steam")) {
            Double temp = meterLastValueObj.get("Temp");
            Double press = meterLastValueObj.get("Press");
            Double totalflowStd = meterLastValueObj.get("Totalflow_std");
            Double flowrateStd = meterLastValueObj.get("Flowrate_std");
            Double totalflowWork = meterLastValueObj.get("Totalflow_work");
            Double flowrateWork = meterLastValueObj.get("Flowrate_work");

            StreamVO streamVO = new StreamVO(temp, press, totalflowStd, flowrateStd, totalflowWork, flowrateWork);
            meterLastValueVO.setMeterLastValue(streamVO);
        }
        return meterLastValueVO;
    }

    private static Long querMax(List<Long> timestamps) {
        List<Long> listTemp = new ArrayList();
        for (int i = 0; i < timestamps.size(); i++) {
            // 保存不为空的元素
            if (timestamps.get(i) != null) {
                listTemp.add(timestamps.get(i));
            }
        }
        Long max = Collections.max(listTemp);
        return max;
    }
}
