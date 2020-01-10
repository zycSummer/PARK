package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.SysEnergyParaRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.DataPointResult;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.EnergyAnalysisService;
import com.jet.cloud.deepmind.service.EnergyMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/30 10:38
 * @desc 能耗分析serviceImpl
 */
@Service
public class EnergyAnalysisServiceImpl implements EnergyAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(EnergyAnalysisServiceImpl.class);

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private EnergyMonitoringService energyMonitoringService;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;
    @Autowired
    private CommonService commonService;

    @Override
    public Response queryPageInfoData(EnergyAnalysisVO energyAnalysisVO) {
        List<HistoryVO> historyVOS = new ArrayList<>();
        /**
         * 和能耗类比 基本一样，区别在于这里左侧的树状节点只能单选，同时右侧的查询条件中时间改为两个时间选择
         *
         * 日：默认昨天和今天
         * 月：默认上月和当月
         * 年：默认去年和今年
         *
         * 查询结果标题
         * 查询结果的标题部分由  导航栏中所选对象名称 - 左侧所选节点 - 能耗时比 - 查询条件中所选参数类别(单位) 组成。
         */
        try {
            String energyTypeId = energyAnalysisVO.getEnergyTypeId();// 能源种类标识(electricity...)
            NodeInfoVO nodeInfo = energyAnalysisVO.getNodeInfo(); // 数据源+节点id+对象类型+对象标识+展示结构树标识
            Response ok = Response.ok("查询成功", historyVOS);
            ok.setQueryPara(energyAnalysisVO);
            if (nodeInfo == null) return ok;
            String startTime = energyAnalysisVO.getStart();// 时间"2019-10-17"
            String endTime = energyAnalysisVO.getEnd();// 时间"2019-10-17"
            String timeType = energyAnalysisVO.getTimeType();// 时间year,month,day
            Integer interval = energyAnalysisVO.getInterval();// 时间间隔分钟
            String type = energyAnalysisVO.getType();//数值类型 first/average/max/min/diff
            List<String> energyParaIds = energyAnalysisVO.getEnergyParaIds();// 能源参数标识(Pa,Ic,Ep_imp,Usage...)
            List<String> points = new ArrayList<>();
            Multimap<String, String> multimap = ArrayListMultimap.create();

            String dataSource = nodeInfo.getDataSource();
            List<Map<String, List<String>>> nodeInfoSourceMap = new ArrayList<>();
            if (!"std_coal".equals(energyTypeId)) {
                if (dataSource != null && !Objects.equals("", dataSource)) {
                    Map<String, List<String>> map = new HashMap<>();
                    for (String energyParaId : energyParaIds) {
                        String newDataSource = null;
                        if (dataSource.contains("#")) {
                            newDataSource = StringUtils.splicingFormula(energyParaId, dataSource);
                            multimap.put(nodeInfo.getNodeId() + energyParaId, newDataSource);
                            points.add(newDataSource);
                        } else {
                            newDataSource = dataSource + "." + energyParaId;
                            multimap.put(nodeInfo.getNodeId() + energyParaId, newDataSource);
                            points.add(newDataSource);
                        }
                    }
                }
            } else {
                if (dataSource != null && !Objects.equals("", dataSource)) {
                    JSONArray jsonArray = JSONArray.parseArray(dataSource);
                    List<Map> maps = jsonArray.toJavaList(Map.class);
                    for (Map map : maps) {
                        for (String energyParaId : energyParaIds) {
                            String paraId = map.get("para_id").toString();
                            if (Objects.equals(paraId, energyParaId)) {
                                String source = map.get("data_source").toString();
                                if (source != null && !Objects.equals("", source)) {
                                    multimap.put(nodeInfo.getNodeId() + energyParaId, source);
                                    points.add(source);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            List<AggregatorDataResponse> aggregatorDataResponses = new ArrayList<>();
            switch (timeType) {
                case "year":
                    Long startYTime1 = DateUtil.stringToLong(startTime + "-01-01 00:00:00");
                    Long endYTime1 = DateUtil.stringToLong((Integer.valueOf(startTime) + 1) + "-01-01 00:00:00");

                    Long startYTime2 = DateUtil.stringToLong(endTime + "-01-01 00:00:00");
                    Long endYTime2 = DateUtil.stringToLong((Integer.valueOf(endTime) + 1) + "-01-01 00:00:00");

                    AggregatorDataResponse aggYear1 = queryData(type, interval, points, startYTime1, endYTime1);
                    AggregatorDataResponse aggYear2 = queryData(type, interval, points, startYTime2, endYTime2);

                    aggregatorDataResponses.add(aggYear1);
                    aggregatorDataResponses.add(aggYear2);
                    historyVOS = queryAggInfo(nodeInfo, aggregatorDataResponses, energyParaIds, energyTypeId, multimap, timeType);
                    break;
                case "month":
                    Long startMTime1 = DateUtil.stringToLong(startTime + "-01 00:00:00");
                    Date date1 = DateUtil.stringToDate2(startTime);
                    LocalDate localDate1 = DateUtil.dateToLocalDate(date1).plusMonths(1);
                    String end1 = DateUtil.localDateToString(localDate1);
                    Long endMTime1 = DateUtil.stringToLong(end1 + " 00:00:00");

                    Long startMTime2 = DateUtil.stringToLong(endTime + "-01 00:00:00");
                    Date date2 = DateUtil.stringToDate2(endTime);
                    LocalDate localDate2 = DateUtil.dateToLocalDate(date2).plusMonths(1);
                    String end2 = DateUtil.localDateToString(localDate2);
                    Long endMTime2 = DateUtil.stringToLong(end2 + " 00:00:00");

                    AggregatorDataResponse aggMonth1 = queryData(type, interval, points, startMTime1, endMTime1);
                    AggregatorDataResponse aggMonth2 = queryData(type, interval, points, startMTime2, endMTime2);

                    aggregatorDataResponses.add(aggMonth1);
                    aggregatorDataResponses.add(aggMonth2);
                    historyVOS = queryAggInfo(nodeInfo, aggregatorDataResponses, energyParaIds, energyTypeId, multimap, timeType);
                    break;
                case "day":
                    Long startDTime1 = DateUtil.stringToLong(startTime + " 00:00:00");
                    Date dateD1 = DateUtil.stringToDate(startTime);
                    LocalDate localDateD1 = DateUtil.dateToLocalDate(dateD1).plusDays(1);
                    String endD1 = DateUtil.localDateToString(localDateD1);
                    Long endDTime1 = DateUtil.stringToLong(endD1 + " 00:00:00");


                    Long startDTime2 = DateUtil.stringToLong(endTime + " 00:00:00");
                    Date dateD2 = DateUtil.stringToDate(endTime);
                    LocalDate localDateD2 = DateUtil.dateToLocalDate(dateD2).plusDays(1);
                    String endD2 = DateUtil.localDateToString(localDateD2);
                    Long endDTime2 = DateUtil.stringToLong(endD2 + " 00:00:00");

                    AggregatorDataResponse aggDay1 = queryData(type, interval, points, startDTime1, endDTime1);
                    AggregatorDataResponse aggDay2 = queryData(type, interval, points, startDTime2, endDTime2);

                    aggregatorDataResponses.add(aggDay1);
                    aggregatorDataResponses.add(aggDay2);
                    historyVOS = queryAggInfo(nodeInfo, aggregatorDataResponses, energyParaIds, energyTypeId, multimap, timeType);
                    break;
            }
            ok.setData(historyVOS);
            return ok;
        } catch (Exception e) {
            log.error("(能耗时比)-页面具体计算逻辑失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询失败", e);
            error.setQueryPara(energyAnalysisVO);
            return error;
        }
    }

    private List<HistoryVO> queryAggInfo(NodeInfoVO nodeInfo, List<AggregatorDataResponse> aggs, List<String> energyParaIds, String energyTypeId, Multimap<String, String> multimap, String timeType) {
        List<HistoryVO> historyVOS = new ArrayList<>();
        for (String energyParaId : energyParaIds) {
            HistoryVO historyVO = new HistoryVO();
            historyVO.setEnergyParaId(energyParaId);
            SysEnergyPara sysEnergyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyParaId);
            historyVO.setEnergyParaName(sysEnergyPara.getEnergyParaName());
            historyVO.setUnit(sysEnergyPara.getUnit());

            if (aggs != null && !aggs.isEmpty()) {
                List<NodeVO> nodeVOS = new ArrayList<>();
                for (AggregatorDataResponse agg : aggs) {
                    NodeVO nodeVO = new NodeVO();
                    String nodeId = nodeInfo.getNodeId();
                    nodeVO.setNodeId(nodeId);
                    nodeVO.setObjType(nodeInfo.getObjType());
                    nodeVO.setObjId(nodeInfo.getObjId());
                    nodeVO.setNodeName(nodeInfo.getNodeName());
                    String dataSource = nodeInfo.getDataSource();
                    if (dataSource != null && !Objects.equals("", dataSource)) {
                        if (dataSource.contains("#") && !dataSource.contains("[")) {
                            nodeVO.setDataSource(StringUtils.splicingFormula(energyParaId, dataSource));
                        } else if (!dataSource.contains("[") && !dataSource.contains("#")) {
                            nodeVO.setDataSource(dataSource + "." + energyParaId);
                        }
                    }
                    List<String> points = (List<String>) multimap.get(nodeId + energyParaId);

                    List<Long> timestamps = agg.getTimestamps();
                    Long aLong = timestamps.get(0);
                    String format = null;
                    if ("day".equals(timeType)) {
                        format = DateUtil.longToLocalTime(aLong).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } else if ("month".equals(timeType)) {
                        format = DateUtil.longToLocalTime(aLong).format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    } else if ("year".equals(timeType)) {
                        format = DateUtil.longToLocalTime(aLong).format(DateTimeFormatter.ofPattern("yyyy"));
                    }
                    nodeVO.setNodeNameAll(format);
                    List<DataPointResult> dataPointResults = agg.getValues();
                    if (dataPointResults != null && !dataPointResults.isEmpty()) {
                        boolean flag = false;
                        for (DataPointResult dataPointResult : dataPointResults) {
                            if (flag) {
                                break;
                            }
                            String metricName = dataPointResult.getMetricName();
                            for (String point : points) {
                                if (Objects.equals(metricName, point)) {
                                    List<Double> values = dataPointResult.getValues();
                                    values = commonService.queryHistoryData(values, timestamps);
                                    CalcPointsVO calcPointsVO = commonService.getMathHandlePoints(timestamps, values);
                                    if (calcPointsVO != null) {
                                        String avg = calcPointsVO.getAvg();
                                        if (avg != null) {
                                            Double average = Double.valueOf(avg);
                                            nodeVO.setAverage(average);
                                        } else {
                                            nodeVO.setAverage(null);
                                        }
                                        String maxVal = calcPointsVO.getMaxVal();
                                        if (maxVal != null) {
                                            Double max = Double.valueOf(maxVal);
                                            nodeVO.setMax(max);
                                        } else {
                                            nodeVO.setMax(null);
                                        }
                                        String minVal = calcPointsVO.getMinVal();
                                        if (minVal != null) {
                                            Double min = Double.valueOf(minVal);
                                            nodeVO.setMin(min);
                                        } else {
                                            nodeVO.setMin(null);
                                        }
                                        LocalDateTime maxTime = calcPointsVO.getMaxTime();
                                        LocalDateTime minTime = calcPointsVO.getMinTime();
                                        if ("year".equals(timeType)) {
                                            if (maxTime != null) {
                                                String max = maxTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"));
                                                nodeVO.setMaxTime(max);
                                            } else {
                                                nodeVO.setMaxTime(null);
                                            }
                                            if (minTime != null) {
                                                String min = minTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"));
                                                nodeVO.setMinTime(min);
                                            } else {
                                                nodeVO.setMinTime(null);
                                            }
                                        }
                                        if ("month".equals(timeType)) {
                                            if (maxTime != null) {
                                                String max = maxTime.format(DateTimeFormatter.ofPattern("dd HH:mm:ss"));
                                                nodeVO.setMaxTime(max);
                                            } else {
                                                nodeVO.setMaxTime(null);
                                            }
                                            if (minTime != null) {
                                                String min = minTime.format(DateTimeFormatter.ofPattern("dd HH:mm:ss"));
                                                nodeVO.setMinTime(min);
                                            } else {
                                                nodeVO.setMinTime(null);
                                            }
                                        }
                                        if ("day".equals(timeType)) {
                                            if (maxTime != null) {
                                                String max = maxTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                                nodeVO.setMaxTime(max);
                                            } else {
                                                nodeVO.setMaxTime(null);
                                            }
                                            if (minTime != null) {
                                                String min = minTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                                nodeVO.setMinTime(min);
                                            } else {
                                                nodeVO.setMinTime(null);
                                            }
                                        }
                                        nodeVO.setValues(values);
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        nodeVO.setMax(null);
                        nodeVO.setMin(null);
                        nodeVO.setAverage(null);
                        nodeVO.setMaxTime(null);
                        nodeVO.setMinTime(null);
                        nodeVO.setValues(null);
                    }
                    nodeVO.setTimes(timestamps);
                    nodeVOS.add(nodeVO);
                }
                historyVO.setNodeVOs(nodeVOS);
            } else {
                historyVO.setNodeVOs(new ArrayList<>());
            }
            historyVOS.add(historyVO);
        }
        return historyVOS;
    }

    private AggregatorDataResponse queryData(String type, Integer interval, List<String> points, Long startTime, Long endTime) {
        if (points != null && !points.isEmpty()) {
            switch (type) {
                case "first":
                    AggregatorDataResponse aggFirst = null;
                    if (interval == 43200) {
                        aggFirst = kairosdbClient.queryHis(points, startTime, endTime, 1, TimeUnit.MONTHS);
                    } else {
                        aggFirst = kairosdbClient.queryHis(points, startTime, endTime, interval, TimeUnit.MINUTES);
                    }
                    return aggFirst;
                case "average":
                    AggregatorDataResponse aggAverage = null;
                    if (interval == 43200) {
                        aggAverage = kairosdbClient.queryAvg(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, TimeUnit.MONTHS);
                    } else {
                        aggAverage = kairosdbClient.queryAvg(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, interval, TimeUnit.MINUTES);
                    }
                    return aggAverage;
                case "max":
                    AggregatorDataResponse aggMax = null;
                    if (interval == 43200) {
                        aggMax = kairosdbClient.queryMax(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, TimeUnit.MONTHS);
                    } else {
                        aggMax = kairosdbClient.queryMax(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, interval, TimeUnit.MINUTES);
                    }
                    return aggMax;
                case "min":
                    AggregatorDataResponse aggMin = null;
                    if (interval == 43200) {
                        aggMin = kairosdbClient.queryMin(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, TimeUnit.MONTHS);
                    } else {
                        aggMin = kairosdbClient.queryMin(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, interval, TimeUnit.MINUTES);
                    }
                    return aggMin;
                case "diff":
                    AggregatorDataResponse aggDiff = null;
                    if (interval == 43200) {
                        aggDiff = kairosdbClient.queryDiff(points, startTime, endTime, 1, TimeUnit.MONTHS);
                    } else {
                        aggDiff = kairosdbClient.queryDiff(points, startTime, endTime, interval, TimeUnit.MINUTES);
                    }
                    return aggDiff;
            }
        }
        return null;
    }
}
