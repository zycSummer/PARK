package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.CalcPointsVO;
import com.jet.cloud.deepmind.model.CombinePointVO;
import com.jet.cloud.deepmind.model.RealTimeLoadVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.*;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.ProjectEnergyConsumeService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.jet.cloud.deepmind.common.Constants.*;

/**
 * @author yhy
 * @create 2019-11-06 10:06
 */
@Service
public class ProjectEnergyConsumeServiceImpl implements ProjectEnergyConsumeService {


    @Autowired
    private CommonService commonService;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private DataSourceRepo dataSourceRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private SysEnergyGradeRepo sysEnergyGradeRepo;
    @Autowired
    private GdpMonthlyRepo gdpMonthlyRepo;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;

    @Override
    public Response realTimeLoadResp(String objType, String objId, String energyTypeId) {
        try {
            Response ok = Response.ok(realTimeLoad(objType, objId, energyTypeId));
            ok.setQueryPara(objId, objType, energyTypeId);
            return ok;
        } catch (NotFoundException e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objId, objType, energyTypeId);
            return error;
        }
    }

    /**
     * 能耗日历
     */
    @Override
    public Response calendar(String objType, String objId, Long timestamp) {

        try {
            JSONObject object = new JSONObject();
            List<SysEnergyGrade> gradeList = sysEnergyGradeRepo.findAll(Sort.by("lower"));
            object.put("gradeList", gradeList);
            LocalDateTime start = DateUtil.longToLocalTime(timestamp);
            SampleData4KairosResp resp = queryHis(objType, objId, ENERGY_TYPE_STD_COAL, start, start.plusYears(1), 1, TimeUnit.MONTHS);

            List<GdpMonthly> monthlyList = gdpMonthlyRepo.findByObjTypeAndObjIdAndYear(objType, objId, start.getYear());

            Map<Month, Double> gdpMap = new HashMap<>();
            for (GdpMonthly gdp : monthlyList) {
                gdpMap.put(Month.of(gdp.getMonth()), gdp.getGdp());
            }

            List<String> res = new ArrayList<>();
            if (resp != null) {
                List<Double> values = resp.getValues();
                if (values != null) {
                    List<Long> timestamps = resp.getTimestamps();
                    int index = 0;
                    for (Double value : values) {
                        Double radio = gdpMap.get(DateUtil.longToLocalTime(timestamps.get(index)).getMonth());
                        if (radio != null && value != null) {
                            res.add(MathUtil.double2String(value / radio));
                        } else {
                            res.add(null);
                        }
                        index++;
                    }
                }
            }
            if (res.size() == 0) {
                for (int i = 0; i < 12; i++) {
                    res.add(null);
                }
            }

            object.put("dataList", res);
            Response ok = Response.ok(object);
            ok.setQueryPara(objType, objId, timestamp);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, timestamp);
            return error;
        }
    }

    @Override
    public Response rank(String objType, String objId, String energyTypeId, Long timestamp) {

        try {

            JSONObject object = new JSONObject();
            Long timeOutValue = commonService.getTimeOutValue();
            List<JSONObject> dataList = new ArrayList<>();
            SysEnergyType energyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
            if (Objects.equals(OBJ_TYPE_PARK, objType)) {
                //选择的是“园区”
                List<String> siteIdList = commonService.getCurrentUserSiteIdList();
                List<Site> siteList = commonService.getCurrentUserSiteList();
                Map<String, String> siteIdMapSiteName = new HashMap<>();
                for (Site site : siteList) {
                    siteIdMapSiteName.put(site.getSiteId(), site.getSiteName());
                }

                if (energyType == null || energyType.getEnergyLoadParaId() == null) {
                    throw new NotFoundException("[" + energyTypeId + "]energyType能源类型配置错误");
                }

                List<DataSource> dataSourceList = dataSourceRepo.findByObjTypeAndObjIdInAndEnergyTypeIdAndEnergyParaId(OBJ_TYPE_SITE, siteIdList
                        , energyTypeId, energyType.getEnergyLoadParaId());

                Map<String, String> map = new HashMap<>();
                List<String> pointIdList = new ArrayList<>();
                for (DataSource dataSource : dataSourceList) {
                    map.put(dataSource.getDataSource(), dataSource.getObjId());
                    pointIdList.add(dataSource.getDataSource());
                }


                List<SampleDataResponse> queryLastList = kairosdbClient.queryLast(pointIdList, timeOutValue);


                for (SampleDataResponse response : queryLastList) {
                    JSONObject t = new JSONObject();
                    t.put("name", siteIdMapSiteName.get(map.get(response.getPoint())));
                    t.put("val", response.getValue());
                    siteIdList.remove(map.get(response.getPoint()));
                    dataList.add(t);
                }

                for (String op : siteIdList) {
                    JSONObject t = new JSONObject();
                    t.put("name", siteIdMapSiteName.get(op));
                    t.put("val", null);
                    dataList.add(t);
                }


            } else if (Objects.equals(OBJ_TYPE_SITE, objType)) {

                List<CombinePointVO> combinePointVOList = commonService.getSiteCombinePointList(objId, energyTypeId);

                List<String> queryList = new ArrayList<>();
                for (CombinePointVO vo : combinePointVOList) {
                    queryList.add(vo.getPointId());
                }

                List<SampleDataResponse> queryLastList = kairosdbClient.queryLast(queryList, timeOutValue);

                int index = 0;
                for (SampleDataResponse response : queryLastList) {
                    JSONObject t = new JSONObject();
                    t.put("name", combinePointVOList.get(index).getMeterName());
                    t.put("val", response.getValue());
                    index++;
                    dataList.add(t);
                }

            }
            //从大到小排序
            dataList.sort(Comparator.comparing(key -> key.getDouble("val")
                    , Comparator.nullsFirst(Double::compareTo).reversed()));
           /* List<JSONObject> list = dataList.stream().sorted((o1, o2) -> {
                Double val1 = o1.getDouble("val");
                Double val2 = o2.getDouble("val");
                if (val1 == null || val2 == null) return 1;
                return -val1.compareTo(val2);
            }).collect(Collectors.toList());*/
            object.put("list", dataList);
            SysEnergyPara energyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyType.getEnergyLoadParaId());
            object.put("name", energyPara.getEnergyParaName());
            object.put("unit", energyPara.getUnit());
            Response ok = Response.ok(object);
            ok.setQueryPara(objType, objId, energyTypeId, timestamp);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, energyTypeId, timestamp);
            return error;
        }
    }

    @Override
    public Response usageInfoPie(String objType, String objId, String timeUnit) {

        try {

            List<SysEnergyType> energyTypeList = sysEnergyTypeRepo.findAllByEnergyTypeIdNot(ENERGY_TYPE_STD_COAL);
            Map<String, String> energyTypeIdMapEnergyTypeName = new HashMap<>();
            Map<String, String> pointIdMapEnergyTypeId = new HashMap<>();
            List<String> queryList = new ArrayList<>();
            for (SysEnergyType energyType : energyTypeList) {
                energyTypeIdMapEnergyTypeName.put(energyType.getEnergyTypeId(), energyType.getEnergyTypeName());
                DataSource d = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId
                        , energyType.getEnergyTypeId(), energyType.getEnergyUsageParaId());
                if (d != null) {
                    queryList.add(d.getDataSource());
                    pointIdMapEnergyTypeId.put(d.getDataSource(), energyType.getEnergyTypeId());
                }


            }
            if (energyTypeIdMapEnergyTypeName.size() == 0) {
                throw new NotFoundException("除标煤外（std_coal）的所有能源种类的负荷参数均未配置");
            }
            LocalDateTime start;
            LocalDateTime end;
            TimeUnit unit;
            switch (timeUnit) {
                case "year":
                    start = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    end = start.plusYears(1);
                    unit = TimeUnit.YEARS;
                    break;
                case "month":
                default:
                    start = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    end = start.plusMonths(1);
                    unit = TimeUnit.MONTHS;
                    break;
            }

            AggregatorDataResponse diffResp = kairosdbClient.queryDiff(queryList, start, end, 1, unit);

            List<JSONObject> res = new ArrayList<>();
            if (diffResp != null && diffResp.getValues() != null) {
                //有数据
                Double sum = null;

                for (DataPointResult result : diffResp.getValues()) {
                    JSONObject data = new JSONObject();
                    String energyTypeId = pointIdMapEnergyTypeId.get(result.getMetricName());
                    data.put("name", energyTypeIdMapEnergyTypeName.get(energyTypeId));
                    energyTypeIdMapEnergyTypeName.remove(energyTypeId);
                    Double v = null;
                    List<Double> values = result.getValues();
                    if (StringUtils.isNotNullAndEmpty(values)) {
                        v = values.get(0);
                        if (v != null) {
                            if (sum == null) sum = 0d;
                            sum += v;
                        }
                    }
                    data.put("val", v);
                    res.add(data);
                }

                for (JSONObject object : res) {
                    Double val = object.getDouble("val");
                    if (val != null && sum != null) {
                        object.put("compare", val / sum);
                    } else {
                        object.put("compare", null);
                    }
                }

            } else {
                ;
            }
            //填充未配测点的数据
            for (Map.Entry<String, String> entry : energyTypeIdMapEnergyTypeName.entrySet()) {
                JSONObject data = new JSONObject();
                data.put("name", entry.getValue());
                data.put("val", null);
                data.put("compare", null);
                res.add(data);
            }
            Response ok = Response.ok(res);
            ok.setQueryPara(objType, objId, timeUnit);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, timeUnit);
            return error;
        }

    }

    @Override
    public Response usageInfoCompare(String objType, String objId, String energyTypeId, Long timestamp) {

        try {
            SysEnergyType energyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
            if (energyType == null || energyType.getEnergyUsageParaId() == null) {
                throw new NotFoundException("[" + energyTypeId + "]energyType能源类型配置错误");
            }

            DataSource d = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId
                    , energyType.getEnergyTypeId(), energyType.getEnergyUsageParaId());

            if (d == null || d.getDataSource() == null) {
                throw new NotFoundException("[" + objType + "-" + objId + "-" + energyTypeId + "]dataSource 未配置");
            }

            LocalDateTime nowStart = DateUtil.longToLocalTime(timestamp);
            LocalDateTime nowEnd = nowStart.plusMonths(1);

            //去年这个月
            LocalDateTime lastYearStart = nowStart.minusYears(1);
            LocalDateTime lastYearEnd = lastYearStart.plusMonths(1);

            //去年这个月同期
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastYearTermEnd = now.plusYears(1);
            LocalDateTime lastYearTermStart = lastYearTermEnd.withDayOfMonth(1);

            //上月
            LocalDateTime lastMonthStart = now.withDayOfMonth(1).minusMonths(1);
            LocalDateTime lastMonthEnd = lastMonthStart.plusMonths(1);
            //上月同期
            LocalDateTime lastMonthTermEnd = now.minusMonths(1);
            LocalDateTime lastMonthTermStart = lastMonthTermEnd.withDayOfMonth(1);

            String dataSource = d.getDataSource();

            SampleData4KairosResp nowResp = kairosdbClient.queryDiff(dataSource, nowStart, nowEnd, 1, TimeUnit.MONTHS);
            SampleData4KairosResp lastYearResp = kairosdbClient.queryDiff(dataSource, lastYearStart, lastYearEnd, 1, TimeUnit.MONTHS);
            SampleData4KairosResp lastYearTermResp = kairosdbClient.queryDiff(dataSource, lastYearTermStart, lastYearTermEnd, 1, TimeUnit.MONTHS);
            SampleData4KairosResp lastMonthResp = kairosdbClient.queryDiff(dataSource, lastMonthStart, lastMonthEnd, 1, TimeUnit.MONTHS);
            SampleData4KairosResp lastMonthTermResp = kairosdbClient.queryDiff(dataSource, lastMonthTermStart, lastMonthTermEnd, 1, TimeUnit.MONTHS);

            JSONObject o = new JSONObject();

            Double nowVal = null;
            Double lastYearTermVal = null;
            Double lastMonthTermVal = null;

            if (nowResp != null && nowResp.getValues() != null && nowResp.getValues().size() > 0) {
                nowVal = nowResp.getValues().get(0);
                o.put("nowResp", nowVal);
            } else {
                o.put("nowResp", null);
            }
            if (lastYearResp != null && lastYearResp.getValues() != null && lastYearResp.getValues().size() > 0) {
                o.put("lastYearResp", lastYearResp.getValues().get(0));
            } else {
                o.put("lastYearResp", null);
            }
            if (lastYearTermResp != null && lastYearTermResp.getValues() != null && lastYearTermResp.getValues().size() > 0) {
                lastYearTermVal = lastYearTermResp.getValues().get(0);
                o.put("lastYearTermResp", lastYearTermVal);
            } else {
                o.put("lastYearTermResp", null);
            }
            if (lastMonthResp != null && lastMonthResp.getValues() != null && lastMonthResp.getValues().size() > 0) {
                o.put("lastMonthResp", lastMonthResp.getValues().get(0));
            } else {
                o.put("lastMonthResp", null);
            }
            if (lastMonthTermResp != null && lastMonthTermResp.getValues() != null && lastMonthTermResp.getValues().size() > 0) {
                lastMonthTermVal = lastMonthTermResp.getValues().get(0);
                o.put("lastMonthTermResp", lastMonthTermVal);
            } else {
                o.put("lastMonthTermResp", null);
            }

            if (nowVal != null) {
                if (lastYearTermVal != null) {
                    o.put("lastYearCompare", (nowVal - lastYearTermVal) / lastMonthTermVal);
                } else {
                    o.put("lastYearCompare", null);
                }
                if (lastMonthTermVal != null) {
                    o.put("lastMonthCompare", (nowVal - lastMonthTermVal) / lastMonthTermVal);
                } else {
                    o.put("lastMonthCompare", null);
                }
            } else {
                o.put("lastYearCompare", null);
                o.put("lastMonthCompare", null);
            }

            SysEnergyPara energyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyType.getEnergyUsageParaId());
            o.put("unit", energyPara.getUnit());
            o.put("name", energyPara.getEnergyParaName());
            Response ok = Response.ok(o);
            ok.setQueryPara(objType, objId, energyTypeId, timestamp);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, energyTypeId, timestamp);
            return error;
        }


    }

    private SampleData4KairosResp queryHis(String objType, String objId, String energyTypeId, LocalDateTime start, LocalDateTime end, Integer interval, TimeUnit unit) throws NotFoundException {
        DataSource dataSource = getDataSource(objType, objId, energyTypeId);
        return kairosdbClient.queryHis(dataSource.getDataSource(), start, end, interval, unit);
    }


    private DataSource getDataSource(String objType, String objId, String energyTypeId) throws NotFoundException {
        SysEnergyType energyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
        if (energyType == null || energyType.getEnergyLoadParaId() == null) {
            throw new NotFoundException("[" + energyTypeId + "]energyType能源类型配置错误");
        }

        DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId
                , energyTypeId, energyType.getEnergyLoadParaId());

        if (dataSource == null || StringUtils.isNullOrEmpty(dataSource.getDataSource())) {
            throw new NotFoundException("[" + objType + "-" + objId + "-" + energyTypeId + energyType.getEnergyLoadParaId() + "]dataSource数据源配置错误");
        }
        return dataSource;
    }


    public RealTimeLoadVO realTimeLoad(String objType, String objId, String energyTypeId) throws NotFoundException {

        RealTimeLoadVO res = new RealTimeLoadVO();
        String name = commonService.getObjNameByObjTypeAndObjId(objType, objId);
        res.setName(name + " - 实时负荷");
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        SampleData4KairosResp resp = queryHis(objType, objId, energyTypeId, start, start.plusDays(1), 5, TimeUnit.MINUTES);
        if (resp != null) {

            List<Long> timestamps = resp.getTimestamps();
            List<Double> values = resp.getValues();
            res.setTimestamps(timestamps);
            res.setValues(values);

            CalcPointsVO temp = commonService.getMathHandlePoints(timestamps, values);
            res.setMinVal(temp.getMinVal());
            res.setMaxVal(temp.getMaxVal());
            res.setMaxTime(temp.getMaxTime() == null ? null : temp.getMaxTime().toLocalTime());
            res.setMinTime(temp.getMinTime() == null ? null : temp.getMinTime().toLocalTime());
            res.setAvg(temp.getAvg());
        }
        return res;
    }


    public static void main(String[] args) {
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            JSONObject t = new JSONObject();
            t.put("name", i + "");
            t.put("val", MathUtil.double2String(Math.random()));
            dataList.add(t);
        }

        System.out.println(dataList);


        //dataList.stream().sorted(Comparator.comparing(a -> a.get("val"))).collect(Collectors.toList());

        System.out.println(dataList);
    }

}
