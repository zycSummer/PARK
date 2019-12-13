package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.*;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.ComprehensiveShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zhuyicheng
 * @create 2019/10/23 16:07
 * @desc 综合展示
 */
@Service
public class ComprehensiveShowServiceImpl implements ComprehensiveShowService {
    private static final Logger log = LoggerFactory.getLogger(ComprehensiveShowServiceImpl.class);

    @Autowired
    private BigScreenRepo bigScreenRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private CommonRepo commonRepo;
    @Autowired
    private SiteRepo siteRepo;
    @Autowired
    private EnergyMonthlyUsagePlanRepo energyMonthlyUsagePlanRepo;
    @Autowired
    private DataSourceRepo dataSourceRepo;

    @Value("${ht_img_file_prefix}")
    private String filePrefix;

    private final String OBJTYPE_ISNULL = "对象类型不能为空";
    private final String OBJID_ISNULL = "对象标识不能为空";
    private final String HTIMGID_ISNULL = "综合展示画面标识不能为空";
    private final String ISMAINPAGE_ISNULL = "是否为主页不能为空";
    private final String SAME_MONTH = "当月";
    private final String LAST_MONTH = "上月";
    private final String LAST_SAME_MONTH = "上月同期";
    private final String ICON = "/public/images/treeIcon/isMainPage.png";

    @Override
    public ServiceData insertSiteImg(BigScreen bigScreen) {
        try {
            String cfgPic = bigScreen.getCfgPic();
            String objType = bigScreen.getObjType();
            String objId = bigScreen.getObjId();
            String htImgId = bigScreen.getHtImgId();
            String isMainPage = bigScreen.getIsMainPage();
            if (objType == null || "".equals(objType)) {
                return ServiceData.error(OBJTYPE_ISNULL, currentUser);
            }
            if (objId == null || "".equals(objId)) {
                return ServiceData.error(OBJID_ISNULL, currentUser);
            }
            if (htImgId == null || "".equals(htImgId)) {
                return ServiceData.error(HTIMGID_ISNULL, currentUser);
            }
            if (isMainPage == null || "".equals(isMainPage)) {
                return ServiceData.error(ISMAINPAGE_ISNULL, currentUser);
            }
            String path = StringUtils.sendFromFile(filePrefix, objType, objId, htImgId, cfgPic, null, null, null);
            String fileName = objType + "_" + objId + "_" + htImgId + ".json";
            bigScreen.setFilePath(fileName);
            bigScreen.setCreateUserId(currentUser.userId());
            bigScreen.setCreateNow();
            bigScreenRepo.save(bigScreen);
            return ServiceData.success("右侧组态画面新增成功", currentUser);
        } catch (Exception e) {
            log.error("新增组态编辑失败,e={}", e.getMessage());
            return ServiceData.error("右侧组态画面新增失败", e, currentUser);
        }
    }

    @Override
    public Response querySiteImg(String objType, String objId, String isMainpage) {
        BigScreen bigScreen = bigScreenRepo.findByObjTypeAndObjIdAndIsMainPage(objType, objId, "Y");
        if (bigScreen != null) {
            bigScreen.setIcon(ICON);
            String filePath = filePrefix + bigScreen.getFilePath();
            if (!"".equals(filePath)) {
                String result = StringUtils.readToString(filePath);
                bigScreen.setCfgPic(result);
                Response ok = Response.ok(bigScreen);
                ok.setQueryPara(objType, objId, isMainpage);
                return ok;
            }
        }

        return Response.ok();
    }

    @Override
    public Response queryAllSiteImg(String objType, String objId) {
        List<BigScreen> bigScreens = bigScreenRepo.findByObjTypeAndObjId(objType, objId);
        if (bigScreens != null && !bigScreens.isEmpty()) {
            for (BigScreen bigScreen : bigScreens) {
                if (bigScreen != null) {
                    if ("Y".equals(bigScreen.getIsMainPage())) {
                        bigScreen.setIcon(ICON);
                    }
                    String filePath = filePrefix + bigScreen.getFilePath();
                    if (filePath != null && !"".equals(filePath)) {
                        String result = StringUtils.readToString(filePath);
                        bigScreen.setCfgPic(result);
                    }
                }
            }
        }
        Response ok = Response.ok(bigScreens);
        ok.setQueryPara(objId, objType);
        return ok;
    }

    @Override
    public Response queryAllSiteImgByHtImgId(String objType, String objId, String htImgId) {
        BigScreen bigScreen = bigScreenRepo.findByObjTypeAndObjIdAndHtImgId(objType, objId, htImgId);
        if (bigScreen != null) {
            if ("Y".equals(bigScreen.getIsMainPage())) {
                bigScreen.setIsMainPage(ICON);
            }
            String filePath = filePrefix + bigScreen.getFilePath();
            if (filePath != null && !"".equals(filePath)) {
                String result = StringUtils.readToString(filePath);
                bigScreen.setCfgPic(result);
            }
        }
        Response ok = Response.ok(bigScreen);
        ok.setQueryPara(objId, objType, htImgId);
        return ok;
    }

    @Override
    public ServiceData updateSiteImg(BigScreen bigScreen) {
        try {
            Integer id = bigScreen.getId();
            BigScreen screen = bigScreenRepo.findById(id).get();
            String cfgPic = bigScreen.getCfgPic();
            String objType = bigScreen.getObjType();
            String objId = bigScreen.getObjId();
            String htImgId = bigScreen.getHtImgId();
            String isMainPage = bigScreen.getIsMainPage();

            if (objType == null || "".equals(objType)) {
                return ServiceData.error(OBJTYPE_ISNULL, currentUser);
            }
            if (objId == null || "".equals(objId)) {
                return ServiceData.error(OBJID_ISNULL, currentUser);
            }
            if (htImgId == null || "".equals(htImgId)) {
                return ServiceData.error(HTIMGID_ISNULL, currentUser);
            }
            if (isMainPage == null || "".equals(isMainPage)) {
                return ServiceData.error(ISMAINPAGE_ISNULL, currentUser);
            }

            String memo = bigScreen.getMemo();
            String path = StringUtils.sendFromFile(filePrefix, objType, objId, htImgId, cfgPic, screen.getObjType(), screen.getObjId(), screen.getHtImgId());
            String fileName = objType + "_" + objId + "_" + htImgId + ".json";
            screen.setObjType(objType);
            screen.setObjId(objId);
            screen.setHtImgId(htImgId);
            if (Objects.equals(isMainPage, "Y")) {
                bigScreenRepo.updateSiteImg("N");
            }
            screen.setIsMainPage(isMainPage);
            screen.setFilePath(fileName);
            screen.setMemo(memo);
            screen.setUpdateUserId(currentUser.userId());
            screen.setUpdateNow();
            bigScreenRepo.save(screen);
            return ServiceData.success("更新组态编辑成功", currentUser);
        } catch (Exception e) {
            log.error("更新组态编辑失败,e={}", e.getMessage());
            return ServiceData.error("更新组态编辑失败", e, currentUser);
        }
    }

    @Override
    public ServiceData deleteSiteImg(String objType, String objId, String htImgId) {
        try {
            bigScreenRepo.deleteSiteImg(objType, objId, htImgId);
            StringUtils.deleteFile(filePrefix, objType, objId, htImgId);
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            log.error("删除组态编辑失败,e={}", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("删除失败", e, currentUser);
        }
    }

    @Override
    public List<Map<String, Object>> energyRealTimeValue(JSONObject datasources) {
        List<Map<String, Object>> list = new ArrayList<>();
        Long timeOutValue = commonService.getTimeOutValue();
        try {
            JSONArray jsonArray = datasources.getJSONArray("datasources");
            List<String> points = jsonArray.toJavaList(String.class);
            List<SampleDataResponse> sampleDataResponses = kairosdbClient.queryLast(points, timeOutValue);

            if (StringUtils.isNotNullAndEmpty(sampleDataResponses)) {
                for (SampleDataResponse sampleDataRespons : sampleDataResponses) {
                    String metricName = sampleDataRespons.getPoint();
                    Double value = sampleDataRespons.getValue();
                    Map<String, Object> map = new HashMap<>();
                    map.put("datasource", metricName);
                    map.put("lastValue", value);
                    list.add(map);
                }
            }
        } catch (Exception e) {
            log.error("综合展示最新值查询查询失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> energyTodayDiffValue(JSONObject datasources) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            JSONArray jsonArray = datasources.getJSONArray("datasources");
            List<String> points = jsonArray.toJavaList(String.class);
            LocalDateTime startTime = DateUtil.dateToLocalDateTime(DateUtil.initDateByDay());
            LocalDateTime endTime = DateUtil.dateToLocalDateTime(DateUtil.initDateByDay24());
            AggregatorDataResponse queryDiff = kairosdbClient.queryDiff(points, startTime, endTime, 1, TimeUnit.DAYS);
            List<DataPointResult> dataPointResults = queryDiff.getValues();
            if (StringUtils.isNotNullAndEmpty(dataPointResults)) {
                for (DataPointResult dataPointResult : dataPointResults) {
                    String metricName = dataPointResult.getMetricName();
                    List<Double> values = dataPointResult.getValues();
                    Double value;
                    if (StringUtils.isNotNullAndEmpty(values)) {
                        value = dataPointResult.getValues().get(0);
                    } else {
                        value = null;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("datasource", metricName);
                    map.put("lastValue", value);
                    list.add(map);
                }
            }
        } catch (Exception e) {
            log.error("综合展示中下角(各种能源或标煤 瞬时量 的当日差值)查询失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * url：输入后台查询各能源种类企业实时负荷排名的接口地址（例如接口名称可以定为energyLoadSiteRank）
     * 具体逻辑如下，收到前台请求后，解析出请求参数中energyTypeId属性值，然后去系统能源种类表查出对应的负荷参数标识
     * SELECT energy_load_para_id FROM `tb_sys_energy_type` WHERE energy_type_id = ?;
     * 然后去对象数据源信息表找出当前用户所能看到的所有企业的当前能源种类负荷参数配置的数据源
     * SELECT * FROM `tb_obj_data_source` WHERE obj_type = 'SITE' AND energy_type_id = ? AND energy_para_id = ?
     * AND obj_id in ( SELECT obj_id FROM `tb_sys_user_group_mapping_obj` WHERE obj_type = 'SITE'
     * AND user_group_id = (SELECT user_group_id FROM `tb_sys_user` WHERE user_id = ? ) );
     * 然后根据各个企业配置的数据源去实时库查询出最新值，配置的数据源可能是单个测点，也可能是公式，以是否有#来区分，
     * 单个测点或者公式中的每个元素都是全测点名称，而且如果后面还有中括号加上能源种类标识，
     * 则取出测点最新值后还需要乘以此能源种类对应的折标系数。查询出测点最新值后还要检查是否超时，
     * 即判断测点最新值时间距离当前时间是否大于系统设定值
     * （SELECT para_value FROM tb_sys_parameter WHERE para_id = 'LastValueTimeOut'）的值，
     * 如果没有设置则无需检查超时。单个测点超时时结果为NULL；公式中的部分测点超时时，当作0参与计算；
     * 公式中的所有测点超时，则结果为NULL。最后将所有企业的最终结果按降序排序（取前10），
     * 并从tb_site表的site_abbr_name字段取出各个企业的简称。
     *
     * @param energyTypeId
     * @return
     */
    @Override
    public EnergyRankingVO energyRealTimeLoadRanking(String energyTypeId, SysUser sysUser) {
        EnergyRankingVO energyRankingVO = new EnergyRankingVO();
        try {
            Long timeOutValue = commonService.getTimeOutValue();
            energyRankingVO.setEnergyTypeId(energyTypeId);

            SysEnergyType sysEnergyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
            String energyLoadParaId = sysEnergyType.getEnergyLoadParaId();
            List<UserGroupMappingObj> userGroupMappingObjs = commonRepo.queryUserGroupMappingObjs(sysUser.getUserId());
            List<String> sites = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(userGroupMappingObjs)) {
                for (UserGroupMappingObj userGroupMappingObj : userGroupMappingObjs) {
                    String objId = userGroupMappingObj.getObjId();
                    sites.add(objId);
                }
            }
            List<DataSource> dataSources = dataSourceRepo.findByObjTypeAndEnergyTypeIdAndEnergyParaIdAndObjIdIn("SITE", energyTypeId, energyLoadParaId, sites);

            List<String> rankSiteName = new ArrayList<>();
            List<String> rankSiteAbbrName = new ArrayList<>();
            List<Double> rankSiteLoad = new ArrayList<>();

            Map<String, Double> mapValue = new HashMap<>();
            Map<String, String> points = new HashMap<>();
            if (dataSources != null && !dataSources.isEmpty()) {
                for (DataSource dataSource : dataSources) {
                    String point = dataSource.getDataSource();
                    String objId = dataSource.getObjId();
                    if (point != null && !"".equals(point)) {
                        points.put(objId, point);
                    } else {
                        mapValue.put(objId, null);
                    }
                }
                List<String> valueList = new ArrayList<String>(points.values());
                List<SampleDataResponse> sampleDataResponses = kairosdbClient.queryLast(valueList, timeOutValue);
                for (SampleDataResponse sampleDataRespons : sampleDataResponses) {
                    Double value = sampleDataRespons.getValue();
                    String point = sampleDataRespons.getPoint();
                    for (String objId : points.keySet()) {
                        String pointId = points.get(objId);
                        if (Objects.equals(pointId, point)) {
                            mapValue.put(objId, value);
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isNotNullAndEmpty(sites)) {
                // 遍历全部的site,如果DataSource没有的塞null
                for (String site : sites) {
                    if (mapValue.get(site) == null) {
                        mapValue.put(site, null);
                    }
                }
            }
            List<DataSource> sources = queryTopTen(mapValue);
            if (sources.size() > 10) {
                List<DataSource> sourceList = sources.subList(0, 10);
                querySource(rankSiteName, rankSiteAbbrName, rankSiteLoad, sourceList);
            } else {
                querySource(rankSiteName, rankSiteAbbrName, rankSiteLoad, sources);
            }
            energyRankingVO.setRankSiteName(rankSiteName);
            energyRankingVO.setRankSiteAbbrName(rankSiteAbbrName);
            energyRankingVO.setRankSiteLoad(rankSiteLoad);
        } catch (Exception e) {
            log.error("综合展示实时负荷排名(从小到大) 电、水、蒸汽---查询失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return energyRankingVO;
    }

    @Override
    public List<ConsumptionReturnVO> energyConsumption(List<ConsumptionVO> consumptionVOS) {
        List<ConsumptionReturnVO> consumptionReturnVOS = new ArrayList<>();
        try {
            List<String> energyTypeIds = new ArrayList<>();
            List<String> points = new ArrayList<>();
            if (consumptionVOS != null && !consumptionVOS.isEmpty()) {
                for (ConsumptionVO consumptionVO : consumptionVOS) {
                    String datasource = consumptionVO.getDatasource();
                    String energyTypeId = consumptionVO.getEnergyTypeId();
                    points.add(datasource);
                    energyTypeIds.add(energyTypeId);
                }
                Map<String, Double> mapEnergyType = new HashMap<>();
                List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findByEnergyTypeIdIn(energyTypeIds);
                for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                    mapEnergyType.put(sysEnergyType.getEnergyTypeId(), sysEnergyType.getStdCoalCoeff());
                }

                /**
                 * 根据energyTypeId去tb_sys_energy_type表查出对应的折标系数。
                 */
                Map<String, Double> mapDMonth = new HashMap<>();
                // 当月差值(当月1号0点到当前时间的差值数据)
                LocalDateTime dMonth = DateUtil.dateToLocalDateTime(DateUtil.firstDayMonth());
                LocalDateTime now = DateUtil.dateToLocalDateTime(new Date());
                AggregatorDataResponse dDiff = kairosdbClient.queryDiff(points, dMonth, now, 1, TimeUnit.MONTHS);
                List<DataPointResult> dataPointResultsD = dDiff.getValues();
                if (dataPointResultsD != null && !dataPointResultsD.isEmpty()) {
                    for (DataPointResult dataPointResult : dataPointResultsD) {
                        List<Double> values = dataPointResult.getValues();
                        if (StringUtils.isNotNullAndEmpty(values)) {
                            mapDMonth.put(dataPointResult.getMetricName() + SAME_MONTH, dataPointResult.getValues().get(0));
                        } else {
                            mapDMonth.put(dataPointResult.getMetricName() + SAME_MONTH, null);
                        }
                    }
                }

                Map<String, Double> mapSMonth = new HashMap<>();
                // 上月差值(上月1号0点到当月1号0点的差值数据)
                LocalDateTime sMonth = DateUtil.dateToLocalDateTime(DateUtil.initDateByUpperMonth());
                AggregatorDataResponse sDiff = kairosdbClient.queryDiff(points, sMonth, dMonth, 1, TimeUnit.MONTHS);
                List<DataPointResult> dataPointResultsS = sDiff.getValues();
                if (dataPointResultsS != null && !dataPointResultsS.isEmpty()) {
                    for (DataPointResult dataPointResult : dataPointResultsS) {
                        List<Double> values = dataPointResult.getValues();
                        if (StringUtils.isNotNullAndEmpty(values)) {
                            mapSMonth.put(dataPointResult.getMetricName() + LAST_MONTH, dataPointResult.getValues().get(0));
                        } else {
                            mapSMonth.put(dataPointResult.getMetricName() + LAST_MONTH, null);
                        }
                    }
                }

                Map<String, Double> mapSTMonth = new HashMap<>();
                // 上月同期差值(上月1号0点到上月当前时间的差值数据)
                LocalDateTime sdMonth = DateUtil.dateToLocalDateTime(DateUtil.initDateByYesMonthNowTime());
                AggregatorDataResponse stDiff = kairosdbClient.queryDiff(points, sMonth, sdMonth, 1, TimeUnit.MONTHS);
                List<DataPointResult> dataPointResultsST = stDiff.getValues();
                if (dataPointResultsST != null && !dataPointResultsST.isEmpty()) {
                    for (DataPointResult dataPointResult : dataPointResultsST) {
                        List<Double> values = dataPointResult.getValues();
                        if (StringUtils.isNotNullAndEmpty(values)) {
                            mapSTMonth.put(dataPointResult.getMetricName() + LAST_SAME_MONTH, dataPointResult.getValues().get(0));
                        } else {
                            mapSTMonth.put(dataPointResult.getMetricName() + LAST_SAME_MONTH, null);
                        }
                    }
                }

                /**
                 * energyTypeId、datasource 和请求参数中的一样
                 * thisMonthUsage：当月使用量
                 * lastMonthUsage：上月使用量
                 * lastMonthThisUsage：当月同期使用量
                 * ratio：同期环比
                 * thisMonthPlan：当月计划量
                 * lastMonthPlan：上月计划量
                 * stdCoalCoeff：折标系数
                 */
                for (ConsumptionVO consumptionVO : consumptionVOS) {
                    String energyTypeId = consumptionVO.getEnergyTypeId();
                    String datasource = consumptionVO.getDatasource();
                    Double valueDMonth = mapDMonth.get(datasource + SAME_MONTH);// 当月差值
                    Double valueSMonth = mapSMonth.get(datasource + LAST_MONTH);// 上月差值
                    Double valueSTMonth = mapSTMonth.get(datasource + LAST_SAME_MONTH);// 上月同期差值
                    Double ratio = null;
                    if (valueDMonth != null && valueSMonth != null && valueSTMonth != null && valueSTMonth != 0d) {
                        ratio = (valueDMonth - valueSTMonth) / valueSTMonth;// 同期环比=（当月差值-上月同期差值）/上月同期差值
                    }
                    Double stdCoalCoeff = mapEnergyType.get(energyTypeId);

                    ConsumptionReturnVO consumptionReturnVO = new ConsumptionReturnVO();
                    consumptionReturnVO.setEnergyTypeId(energyTypeId);
                    consumptionReturnVO.setThisMonthUsage(valueDMonth);
                    consumptionReturnVO.setLastMonthUsage(valueSMonth);
                    consumptionReturnVO.setLastMonthThisUsage(valueSTMonth);
                    consumptionReturnVO.setStdCoalCoeff(stdCoalCoeff);
                    consumptionReturnVO.setRatio(ratio);
                    consumptionReturnVO.setDatasource(datasource);

                    /**
                     * 根据energyTypeId、objType、objId的值去tb_obj_energy_monthly_usage_plan表取当月和上月的计划值；
                     */
                    EnergyMonthlyUsagePlan dMonthPlan = energyMonthlyUsagePlanRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndYearAndMonth(consumptionVO.getObjType(), consumptionVO.getObjId(), energyTypeId, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
                    EnergyMonthlyUsagePlan sMonthPlan = energyMonthlyUsagePlanRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndYearAndMonth(consumptionVO.getObjType(), consumptionVO.getObjId(), energyTypeId, LocalDateTime.now().getYear(), LocalDateTime.now().minusMonths(1).getMonthValue());

                    if (dMonthPlan != null) {
                        consumptionReturnVO.setThisMonthPlan(dMonthPlan.getUsage());
                    } else {
                        consumptionReturnVO.setThisMonthPlan(null);
                    }
                    if (sMonthPlan != null) {
                        consumptionReturnVO.setLastMonthPlan(sMonthPlan.getUsage());
                    } else {
                        consumptionReturnVO.setLastMonthPlan(null);
                    }

                    consumptionReturnVOS.add(consumptionReturnVO);
                }
            }
        } catch (Exception e) {
            log.error("内容左下角展示各种能源和标煤具体数据源查询失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return consumptionReturnVOS;
    }

    /**
     * 收到前台请求后，解析出请求参数中datasource属性值去实时库查询当天每5分钟的历史值。
     * 如果后面还有中括号加上能源种类标识，则取出测点值后还需要乘以此能源种类对应的折标系数。
     *
     * @param datasource
     * @return
     */
    @Override
    public RealTimeVO loadTodayHistoryValue(String datasource) {
        try {
            RealTimeVO realTimeVO = new RealTimeVO();
            LocalDateTime startTime = DateUtil.dateToLocalDateTime(DateUtil.initDateByDay());
            LocalDateTime endTime = DateUtil.dateToLocalDateTime(DateUtil.initDateByDay24());
            SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryHis(datasource, startTime, endTime, 5, TimeUnit.MINUTES);
            List<Long> timestamps = sampleData4KairosResp.getTimestamps();
            List<Double> values = sampleData4KairosResp.getValues();
            String point = sampleData4KairosResp.getPoint();
            realTimeVO.setDatasource(point);
            List<Double> newValues = new ArrayList<>();
            for (Double value : values) {
                if (value != null) {
                    Double v = Double.valueOf(MathUtil.double2String(value));
                    newValues.add(v);
                } else {
                    newValues.add(null);
                }
            }
            realTimeVO.setDailyHisValue(newValues);
            realTimeVO.setTimeValues(timestamps);
            return realTimeVO;
        } catch (Exception e) {
            log.error("综合展示实时负荷查询失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EnergyRankingVO energyTodayUsageRanking(String energyTypeId, SysUser sysUser) {
        EnergyRankingVO energyRankingVO = new EnergyRankingVO();
        LocalDateTime startTime = DateUtil.dateToLocalDateTime(DateUtil.initDateByDay());
        LocalDateTime endTime = DateUtil.dateToLocalDateTime(DateUtil.initDateByDay24());
        try {
            energyRankingVO.setEnergyTypeId(energyTypeId);
            SysEnergyType sysEnergyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
            String energyUsageParaId = sysEnergyType.getEnergyUsageParaId();

            List<UserGroupMappingObj> userGroupMappingObjs = commonRepo.queryUserGroupMappingObjs(sysUser.getUserId());
            List<String> sites = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(userGroupMappingObjs)) {
                for (UserGroupMappingObj userGroupMappingObj : userGroupMappingObjs) {
                    String objId = userGroupMappingObj.getObjId();
                    sites.add(objId);
                }
            }
            List<DataSource> dataSources = dataSourceRepo.findByObjTypeAndEnergyTypeIdAndEnergyParaIdAndObjIdIn("SITE", energyTypeId, energyUsageParaId, sites);

            List<String> rankSiteName = new ArrayList<>();
            List<String> rankSiteAbbrName = new ArrayList<>();
            List<Double> rankSiteLoad = new ArrayList<>();
            Map<String, Double> mapValue = new HashMap<>();
            Map<String, String> points = new HashMap<>();

            if (dataSources != null && !dataSources.isEmpty()) {
                for (DataSource dataSource : dataSources) {
                    String point = dataSource.getDataSource();
                    String objId = dataSource.getObjId();
                    if (point != null && !"".equals(point)) {
                        points.put(objId, point);
                    } else {
                        mapValue.put(objId, null);
                    }
                }
                List<String> valueList = new ArrayList<String>(points.values());
                AggregatorDataResponse queryDiff = kairosdbClient.queryDiff(valueList, startTime, endTime, 1, TimeUnit.DAYS);

                List<DataPointResult> dataPointResults = queryDiff.getValues();
                if (StringUtils.isNotNullAndEmpty(dataPointResults)) {
                    for (DataPointResult dataPointResult : dataPointResults) {
                        List<Double> values = dataPointResult.getValues();
                        String metricName = dataPointResult.getMetricName();
                        for (String objId : points.keySet()) {
                            String pointId = points.get(objId);
                            if (Objects.equals(pointId, metricName)) {
                                if (StringUtils.isNotNullAndEmpty(values)) {
                                    mapValue.put(objId, values.get(0));
                                } else {
                                    mapValue.put(objId, null);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (StringUtils.isNotNullAndEmpty(sites)) {
                // 遍历全部的site,如果DataSource没有的塞null
                for (String site : sites) {
                    if (mapValue.get(site) == null) {
                        mapValue.put(site, null);
                    }
                }
            }
            List<DataSource> sources = queryTopTen(mapValue);
            if (sources.size() > 10) {
                List<DataSource> sourceList = sources.subList(0, 10);
                querySource(rankSiteName, rankSiteAbbrName, rankSiteLoad, sourceList);
            } else {
                querySource(rankSiteName, rankSiteAbbrName, rankSiteLoad, sources);
            }
            energyRankingVO.setRankSiteName(rankSiteName);
            energyRankingVO.setRankSiteAbbrName(rankSiteAbbrName);
            energyRankingVO.setRankSiteLoad(rankSiteLoad);
        } catch (Exception e) {
            log.error("综合展示当日能耗排名(从小到大) 电、水、蒸汽、标煤---查询失败,e={}", e.getMessage());
            e.printStackTrace();
        }
        return energyRankingVO;
    }

    private void querySource(List<String> rankSiteName, List<String> rankSiteAbbrName, List<Double> rankSiteLoad, List<DataSource> sourceList) {
        for (DataSource source : sourceList) {
            String objId = source.getObjId();
            Site site = siteRepo.findBySiteId(objId);
            rankSiteName.add(site.getSiteName());
            rankSiteAbbrName.add(site.getSiteAbbrName());
            if (source.getLastVal() != null) {
                rankSiteLoad.add(Double.valueOf(MathUtil.double2String(source.getLastVal())));
            } else {
                rankSiteLoad.add(null);
            }
        }
    }

    private List<DataSource> queryTopTen(Map<String, Double> map) {
        List<DataSource> dataSources = new ArrayList<>();
        List<String> sites = new ArrayList<>();
        for (String objId : map.keySet()) {
            DataSource dataSource = new DataSource();
            dataSource.setObjId(objId);
            Double value = map.get(objId);
            if (value != null) {
                dataSource.setLastVal(value);
                dataSources.add(dataSource);
            } else {
                sites.add(objId);
            }
        }
        // 倒叙
        dataSources.sort((o1, o2) -> {
            Double val1 = o1.getLastVal();
            Double val2 = o2.getLastVal();
            if (val1 == null || val2 == null) return 0;
            if (val1 >= val2) {
                return -1;
            }
            if (val2 < val1) {
                return 1;
            }
            return 0;
        });
        if (StringUtils.isNotNullAndEmpty(sites)) {
            for (String site : sites) {
                DataSource dataSource = new DataSource();
                dataSource.setObjId(site);
                dataSource.setLastVal(null);
                dataSources.add(dataSource);
            }
        }
        return dataSources;
    }

    private List<DataSource> queryTopTen2(List<DataPointResult> dataPointResults, List<DataSource> dataSources) {
        if (StringUtils.isNotNullAndEmpty(dataPointResults)) {
            for (int i = 0; i < dataPointResults.size(); i++) {
                List<Double> values = dataPointResults.get(i).getValues();
                Double value = null;
                if (StringUtils.isNotNullAndEmpty(values)) {
                    value = dataPointResults.get(i).getValues().get(0);
                }
                dataSources.get(i).setLastVal(value);
            }
            // 倒叙
            dataSources.sort((o1, o2) -> {
                Double val1 = o1.getLastVal();
                Double val2 = o2.getLastVal();
                if (val1 == null || val2 == null) return 0;
                if (val1 >= val2) {
                    return -1;
                }
                if (val2 < val1) {
                    return 1;
                }
                return 0;
            });
        }
        return dataSources;
    }

    public static void main(String[] args) {
        List<Double> doubles = Lists.newArrayList(null, 1.1, null, 2.2, 1.2, null);
        // 倒叙
        doubles.sort((o1, o2) -> {
            Double val1 = o1;
            Double val2 = o2;
            if (val1 == null || val2 == null) return 0;
            if (val1 >= val2) {
                return -1;
            }
            if (val2 < val1) {
                return 1;
            }
            return 0;
        });
      /*  for (int i = 0; i < 5; i++) {
            doubles.add(0, null);
        }*/
        System.out.println(doubles);
    }
}
