package com.jet.cloud.deepmind.service.report.impl;


import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.repository.report.ReportManageRepo;
import com.jet.cloud.deepmind.repository.report.ReportObjDetailRepo;
import com.jet.cloud.deepmind.repository.report.ReportParaDetailRepo;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.DataPointResult;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.impl.ReportWriteHandlerImpl;
import com.jet.cloud.deepmind.service.report.ReportQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author maohandong
 * @create 2019/11/12 11:51
 */
@Service
public class ReportQueryServiceImpl implements ReportQueryService {
    private static final Logger log = LoggerFactory.getLogger(ReportQueryServiceImpl.class);


    @Autowired
    private ReportManageRepo reportManageRepo;

    @Autowired
    private ReportObjDetailRepo reportObjDetailRepo;

    @Autowired
    private ReportParaDetailRepo reportParaDetailRepo;

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Autowired
    private KairosdbClient kairosdbClient;

    @Autowired
    private ParkRepo parkRepo;

    @Autowired
    private SiteRepo siteRepo;

    private static final String tab = "         ";

    @Override
    public Response queryReport(String objType, String objId) {
        List<Report> reportList = null;
        try {
            reportList = reportManageRepo.findByObjTypeAndObjIdAndIsUseOrderBySortId(objType, objId, "Y");
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < reportList.size(); i++) {
                Report report = reportList.get(i);
                list.add(report.getEnergyTypeId());
            }
            List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findByEnergyTypeIdIn(list);
            HashMap<String, String> map = new HashMap<>();
            for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                String energyTypeName = sysEnergyType.getEnergyTypeName();
                String energyTypeId = sysEnergyType.getEnergyTypeId();
                map.put(energyTypeId, energyTypeName);
            }
            for (Report report : reportList) {
                String energyTypeName = map.get(report.getEnergyTypeId());
                report.setShowName("[" + energyTypeName + "]" + "[" + report.getReportId() + "]" + report.getReportName());
            }
            Response ok = Response.ok(reportList);
            ok.setQueryPara(objId, objType);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objId, objType);
            return error;
        }
    }

    @Override
    public Response<ReportInfosVO> query(String objType, String objId, String reportId, Long date, String timeUnit) {
        ReportInfosVO reportInfosVO = new ReportInfosVO();
        LocalDateTime startTime;
        LocalDateTime endTime;
        TimeUnit unit;
        if (Objects.equals("year", timeUnit)) {
            startTime = DateUtil.longToLocalTime(date);
            endTime = startTime.plusYears(1);
            unit = TimeUnit.MONTHS;
        } else if (Objects.equals("month", timeUnit)) {
            startTime = DateUtil.longToLocalTime(date);
            endTime = startTime.plusMonths(1);
            unit = TimeUnit.DAYS;
        } else {
            startTime = DateUtil.longToLocalTime(date);
            endTime = startTime.plusDays(1);
            unit = TimeUnit.HOURS;
        }
        try {
            List<ReportParaDetail> reportParaDetails = reportParaDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortId(objType, objId, reportId);
            // 表头
            List<ReportTableVO> reportTableVOS = new ArrayList<>();
            if (reportParaDetails != null && !reportParaDetails.isEmpty()) {
                for (ReportParaDetail reportParaDetail : reportParaDetails) {
                    String energyParaId = reportParaDetail.getEnergyParaId();
                    String displayName = reportParaDetail.getDisplayName();

                    String timeValue = reportParaDetail.getTimeValue();
                    String maxValue = reportParaDetail.getMaxValue();
                    String minValue = reportParaDetail.getMinValue();
                    String avgValue = reportParaDetail.getAvgValue();
                    String diffValue = reportParaDetail.getDiffValue();

                    ReportTableVO reportTableVO = new ReportTableVO(energyParaId, displayName, timeValue, maxValue, minValue, avgValue, diffValue);
                    reportTableVOS.add(reportTableVO);
                }
            }
            reportInfosVO.setReportTableVOS(reportTableVOS);

            // 内容
            List<Map<String, Object>> reportInfoVOS = new ArrayList<>();
            List<ReportObjDetail> reportObjDetails = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortIdAsc(objType, objId, reportId);
            Map<String, String> mapPoints = new HashMap<>();
            if (reportObjDetails != null && !reportObjDetails.isEmpty()) {
                for (ReportObjDetail reportObjDetail : reportObjDetails) {
                    String dataSource = reportObjDetail.getDataSource();
                    if (reportParaDetails != null && !reportParaDetails.isEmpty()) {
                        for (ReportParaDetail reportParaDetail : reportParaDetails) {
                            String energyParaId = reportParaDetail.getEnergyParaId();
                            if (dataSource != null) {
                                String pointId = StringUtils.splicingFormula(energyParaId, dataSource);
                                mapPoints.put(objType + objId + reportId + reportObjDetail.getNodeId() + energyParaId, pointId);
                            }
                        }
                    }
                }
            }

            List<String> points = new ArrayList<String>(mapPoints.values());
            if (StringUtils.isNotNullAndEmpty(points)) {
                AggregatorDataResponse queryHis = kairosdbClient.queryHis(points, startTime, endTime, 1, unit);// 时刻值
                AggregatorDataResponse queryMax = kairosdbClient.queryMax(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, unit);// 最大值
                AggregatorDataResponse queryMin = kairosdbClient.queryMin(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, unit);// 最小值
                AggregatorDataResponse queryAvg = kairosdbClient.queryAvg(points, startTime, endTime, Constants.MINIMUM_MINUTE, TimeUnit.MINUTES, 1, unit);// 平均值
                AggregatorDataResponse queryDiff = kairosdbClient.queryDiff(points, startTime, endTime, 1, unit);// 差值

                if (reportObjDetails != null && !reportObjDetails.isEmpty()) {
                    for (ReportObjDetail reportObjDetail : reportObjDetails) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nodeId", reportObjDetail.getNodeId());
                        map.put("nodeName", reportObjDetail.getNodeName());
                        map.put("parentId", reportObjDetail.getParentId());

                        List<ReportInfoVO> reportInfoVOList = new ArrayList<>();
                        if (reportParaDetails != null && !reportParaDetails.isEmpty()) {
                            for (ReportParaDetail reportParaDetail : reportParaDetails) {
                                ReportInfoVO reportInfoVO = new ReportInfoVO();
                                String energyParaId = reportParaDetail.getEnergyParaId();
                                String timeValue = reportParaDetail.getTimeValue();
                                String maxValue = reportParaDetail.getMaxValue();
                                String minValue = reportParaDetail.getMinValue();
                                String avgValue = reportParaDetail.getAvgValue();
                                String diffValue = reportParaDetail.getDiffValue();
                                String pointId = mapPoints.get(objType + objId + reportId + reportObjDetail.getNodeId() + energyParaId);
                                if (pointId != null) {
                                    if (Objects.equals("Y", timeValue)) {
                                        List<DataPointResult> dataPointResults = queryHis.getValues();
                                        for (DataPointResult dataPointResult : dataPointResults) {
                                            String metricName = dataPointResult.getMetricName();
                                            List<Double> values = dataPointResult.getValues();
                                            if (Objects.equals(metricName, pointId)) {
                                                reportInfoVO.setFirst(values);
                                                reportInfoVO.setPointId(metricName);
                                                reportInfoVO.setEnergyParaId(energyParaId);
                                                break;
                                            }
                                        }
                                    }
                                    if (Objects.equals("Y", maxValue)) {
                                        List<DataPointResult> dataPointResults = queryMax.getValues();
                                        for (DataPointResult dataPointResult : dataPointResults) {
                                            String metricName = dataPointResult.getMetricName();
                                            List<Double> values = dataPointResult.getValues();
                                            if (Objects.equals(metricName, pointId)) {
                                                reportInfoVO.setMax(values);
                                                reportInfoVO.setPointId(metricName);
                                                reportInfoVO.setEnergyParaId(energyParaId);
                                                break;
                                            }
                                        }
                                    }
                                    if (Objects.equals("Y", minValue)) {
                                        List<DataPointResult> dataPointResults = queryMin.getValues();
                                        for (DataPointResult dataPointResult : dataPointResults) {
                                            String metricName = dataPointResult.getMetricName();
                                            List<Double> values = dataPointResult.getValues();
                                            if (Objects.equals(metricName, pointId)) {
                                                reportInfoVO.setMin(values);
                                                reportInfoVO.setPointId(metricName);
                                                reportInfoVO.setEnergyParaId(energyParaId);
                                                break;
                                            }
                                        }
                                    }
                                    if (Objects.equals("Y", avgValue)) {
                                        List<DataPointResult> dataPointResults = queryAvg.getValues();
                                        for (DataPointResult dataPointResult : dataPointResults) {
                                            String metricName = dataPointResult.getMetricName();
                                            List<Double> values = dataPointResult.getValues();
                                            if (Objects.equals(metricName, pointId)) {
                                                reportInfoVO.setAvg(values);
                                                reportInfoVO.setPointId(metricName);
                                                reportInfoVO.setEnergyParaId(energyParaId);
                                                break;
                                            }
                                        }
                                    }
                                    if (Objects.equals("Y", diffValue)) {
                                        List<DataPointResult> dataPointResults = queryDiff.getValues();
                                        for (DataPointResult dataPointResult : dataPointResults) {
                                            String metricName = dataPointResult.getMetricName();
                                            List<Double> values = dataPointResult.getValues();
                                            if (Objects.equals(metricName, pointId)) {
                                                reportInfoVO.setDiff(values);
                                                reportInfoVO.setPointId(metricName);
                                                reportInfoVO.setEnergyParaId(energyParaId);
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    reportInfoVO.setEnergyParaId(energyParaId);
                                }
                                reportInfoVOList.add(reportInfoVO);
                            }
                            map.put("result", reportInfoVOList);
                            reportInfoVOS.add(map);
                        }
                    }
                }
            }
            reportInfosVO.setReportInfoVOS(reportInfoVOS);
            List<Long> timeStamps = DateUtil.calcDate(startTime, endTime, unit);
            reportInfosVO.setTimeStamps(timeStamps);
            Response ok = Response.ok("查询成功", reportInfosVO);
            ok.setQueryPara(objType, objId, reportId, date, timeUnit);
            return ok;
        } catch (Exception e) {
            log.error("报表查询失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询失败", e);
            error.setQueryPara(objType, objId, reportId, date, timeUnit);
            return error;
        }
    }


    @Override
    public void exportExcel(ReportInfosVO reportInfosVO, HttpServletResponse response, HttpServletRequest request, String objType, String objId, String reportId, String fileName) {
        ExcelWriter writer = null;
        String projectName = null;
        try {
            if (objType.equals("PARK")) {
                Park park = parkRepo.findByParkId(objId);
                projectName =  "["+park.getParkId()+"]"+park.getParkName();
            } else {
                Site site = siteRepo.findBySiteId(objId);
                projectName = "["+site.getSiteId()+"]"+site.getSiteName();
            }
            String name = projectName + "_"+fileName;
            String userAgent = request.getHeader("User-Agent");
            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment; filename=" + StringUtils.resolvingScrambling(name, userAgent) + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            response.setDateHeader("Expires", 0);//设置日期头
            Sheet sheet = new Sheet(1, 0);
            sheet.setSheetName("report");
            writer = EasyExcelFactory.getWriterWithTempAndHandler(null, outputStream, ExcelTypeEnum.XLSX, true, new ReportWriteHandlerImpl());
            Table table = new Table(1);
            // 动态添加 表头 headList --> 所有表头行集合
            List<List<String>> headList = new ArrayList<>();
            List<String> headTitle0 = new ArrayList<>();
            headTitle0.add("节点名称");
            headTitle0.add("节点名称");
            headTitle0.add("节点名称");
            headList.add(headTitle0);
            List<Long> timeStamps = reportInfosVO.getTimeStamps();
            List<ReportTableVO> reportTableVOS = reportInfosVO.getReportTableVOS();
            Multimap<String, String> mapValues = ArrayListMultimap.create();
            Multimap<String, String> dataValues = ArrayListMultimap.create();
            for (ReportTableVO reportTableVO : reportTableVOS) {
                String energyParaId = reportTableVO.getEnergyParaId();
                String displayName = reportTableVO.getDisplayName();
                String avgValue = reportTableVO.getAvgValue();
                if (avgValue.equals("Y")) {
                    mapValues.put(displayName, "平均值");
                    dataValues.put(energyParaId, "avg");
                }
                String diffValue = reportTableVO.getDiffValue();
                if (diffValue.equals("Y")) {
                    mapValues.put(displayName, "差值");
                    dataValues.put(energyParaId, "diff");
                }
                String maxValue = reportTableVO.getMaxValue();
                if (maxValue.equals("Y")) {
                    mapValues.put(displayName, "最大值");
                    dataValues.put(energyParaId, "max");
                }
                String minValue = reportTableVO.getMinValue();
                if (minValue.equals("Y")) {
                    mapValues.put(displayName, "最小值");
                    dataValues.put(energyParaId, "min");
                }
                String timeValue = reportTableVO.getTimeValue();
                if (timeValue.equals("Y")) {
                    mapValues.put(displayName, "时刻值");
                    dataValues.put(energyParaId, "first");
                }
            }
            List<Map<String, Object>> reportInfoVOS = reportInfosVO.getReportInfoVOS();
            ArrayList<List<ReportInfoVO>> result = new ArrayList<>();
            for (Map<String, Object> reportInfoVO : reportInfoVOS) {
                List<ReportInfoVO> o1 = (List<ReportInfoVO>) reportInfoVO.get("result");
                result.add(o1);
            }
            List<ReportObjDetailVos> treeInfoDetails = queryTreeInfoDetails(objType, objId, reportId);
            List<ReportObjDetailVos> res = new ArrayList<>();
            iter(res, treeInfoDetails);
            for (ReportObjDetailVos vo : res) {
                vo.setNodeName(setName(vo.getNodeName(), vo.getDeep()));
            }
            ArrayList<String> nodeNames = new ArrayList<>();
            for (ReportObjDetailVos re : res) {
                String nodeName = re.getNodeName();
                nodeNames.add(nodeName);
            }
            ArrayList<List<Object>> listAll = new ArrayList<>();
            for (int k = 0; k < result.size(); k++) {
                ArrayList<Object> list = new ArrayList<>();
                list.add(nodeNames.get(k));
                for (int i = 0; i < timeStamps.size(); i++) {
                    List<ReportInfoVO> reportInfoVOS1 = result.get(k);
                    for (int j = 0; j < reportInfoVOS1.size(); j++) {
                        ReportInfoVO reportInfoVO = reportInfoVOS1.get(j);
                        String energyParaId = reportInfoVO.getEnergyParaId();
                        Collection<String> values = dataValues.get(energyParaId);
                        String pointId = reportInfoVO.getPointId();
                        //数据库中没有配置数据源
                        if (StringUtils.isNullOrEmpty(pointId)) {
                            for (int m = 0; m < values.size(); m++) {
                                list.add("");
                            }
                        } else {
                            List<Double> avg = reportInfoVO.getAvg();
                            if (values.contains("avg")) {
                                if (avg.size() > 0) {
                                    Double aDouble = avg.get(i);
                                    saveData(aDouble, list);
                                } else {
                                    list.add("");
                                }
                            }
                            List<Double> diff = reportInfoVO.getDiff();
                            if (values.contains("diff")) {
                                if (diff.size() > 0) {
                                    Double aDouble = diff.get(i);
                                    saveData(aDouble, list);
                                } else {
                                    list.add("");
                                }
                            }
                            //测点存在
                            List<Double> first = reportInfoVO.getFirst();
                            //包含此测点
                            if (values.contains("first")) {
                                //测点有值
                                if (first.size() > 0) {
                                    Double aDouble = first.get(i);
                                    saveData(aDouble, list);
                                } else {
                                    //测点没有值
                                    list.add("");
                                }
                            }
                            List<Double> max = reportInfoVO.getMax();
                            if (values.contains("max")) {
                                if (max.size() > 0) {
                                    Double aDouble = max.get(i);
                                    saveData(aDouble, list);
                                } else {
                                    list.add("");
                                }
                            }
                            List<Double> min = reportInfoVO.getMin();
                            if (values.contains("min")) {
                                if (min.size() > 0) {
                                    Double aDouble = min.get(i);
                                    saveData(aDouble, list);
                                } else {
                                    list.add("");
                                }
                            }
                        }
                    }
                }
                listAll.add(list);
            }
            String headDate = null;
            if (timeStamps.size() == 12) {
                //按年
                for (int i = 0; i < timeStamps.size(); i++) {
                    if (i <= 9) {
                        headDate = "0" + i + "月";
                    } else {
                        headDate = i + "月";
                    }
                    queryHead(headDate, mapValues, headList);
                }
            } else if (timeStamps.size() == 24) {
                //按日
                for (int i = 0; i < timeStamps.size(); i++) {
                    if (i <= 9) {
                        headDate = "0" + i + "时";
                    } else {
                        headDate = i + "时";
                    }
                    queryHead(headDate, mapValues, headList);
                }
            } else {
                //按月
                for (int i = 0; i < timeStamps.size(); i++) {
                    if (i <= 9) {
                        headDate = "0" + i + "日";
                    } else {
                        headDate = i + "日";
                    }
                    queryHead(headDate, mapValues, headList);
                }
            }
            table.setHead(headList);
            writer.write1(listAll, sheet, table);
            writer.finish();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData(Double aDouble, ArrayList<Object> list) {
        if (aDouble == null) {
            list.add(aDouble);
        } else {
            list.add(Double.valueOf(MathUtil.double2String(aDouble)));
        }
    }

    private List<ReportObjDetailVos> queryTreeInfoDetails(String objType, String objId, String reportId) {
        List<ReportObjDetailVos> reportObjDetailVos = new ArrayList<>();
        try {
            List<ReportObjDetail> reportObjDetailList = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortIdAsc(objType, objId, reportId);
            Map<String, String> map = new HashMap<>();
            if (reportObjDetailList != null && !reportObjDetailList.isEmpty()) {
                for (ReportObjDetail reportObjDetail : reportObjDetailList) {
                    String nodeId = reportObjDetail.getNodeId();
                    String nodeName = reportObjDetail.getNodeName();
                    map.put(nodeId, nodeName);
                }
                for (ReportObjDetail reportObjDetail : reportObjDetailList) {
                    String parentName = map.get(reportObjDetail.getParentId());
                    reportObjDetail.setParentName(parentName);
                }
                Multimap<String, ReportObjDetailVos> OrgTreeDetailMultimap = ArrayListMultimap.create();
                for (ReportObjDetail reportObjDetail : reportObjDetailList) {
                    String parentId = reportObjDetail.getParentId();
                    if (parentId == null || "".equals(parentId)) {
                        reportObjDetailVos.add(new ReportObjDetailVos(reportObjDetail, 0));
                    } else {
                        OrgTreeDetailMultimap.put(reportObjDetail.getParentId(), new ReportObjDetailVos(reportObjDetail, 0));
                    }
                }
                for (ReportObjDetailVos reportObjDetailVo : reportObjDetailVos) {
                    addChild(reportObjDetailVo, OrgTreeDetailMultimap, 10, 0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return reportObjDetailVos;
    }

    private void iter(List<ReportObjDetailVos> res, List<ReportObjDetailVos> treeInfoDetails) {
        if (treeInfoDetails != null && treeInfoDetails.size() > 0) {
            for (ReportObjDetailVos treeInfoDetail : treeInfoDetails) {
                res.add(new ReportObjDetailVos(treeInfoDetail));
                iter(res, treeInfoDetail.getChildren());
            }
        }
    }

    private String setName(String nodeName, Integer deep) {
        if (deep == null || deep == 0) {
            return nodeName;
        }
        String prefix = "";
        for (Integer i = 0; i < deep; i++) {
            prefix += tab;
        }
        return prefix + nodeName;
    }

    private void queryHead(String headDate, Multimap<String, String> mapValues, List<List<String>> headList) {
        Collection<Map.Entry<String, String>> entries = mapValues.entries();
        Multimap<String, ArrayList<String>> map = ArrayListMultimap.create();
        for (Map.Entry<String, String> entry : entries) {
            ArrayList<String> headTitle = new ArrayList<>();
            headTitle.add(headDate);
            headTitle.add(entry.getKey());
            headTitle.add(entry.getValue());
            map.put(entry.getKey(), headTitle);
            headList.add(headTitle);
        }
    }

    public <T> void addChild(T t, Multimap<String, T> dataMultimap, int size, Integer flag) {
        if (t instanceof ReportObjDetailVos) {
            ReportObjDetailVos reportObjDetailVos = (ReportObjDetailVos) t;
            if (size > 0 && reportObjDetailVos != null) {
                reportObjDetailVos.setChildren(new ArrayList<>());
                Collection<ReportObjDetailVos> objs = (Collection<ReportObjDetailVos>) dataMultimap.get(reportObjDetailVos.getNodeId());
                if (objs.size() > 0) {
                    flag = flag + 1;
                    for (ReportObjDetailVos subModel : objs) {
                        Integer deep = subModel.getDeep();
                        deep = deep + flag;
                        subModel.setDeep(deep);
                        addChild((T) subModel, dataMultimap, --size, flag);
                        reportObjDetailVos.getChildren().add(subModel);
                    }
                } else {
                    reportObjDetailVos.setChildren(null);
                }
            }
        }
    }

    public static void main(String[] args) {
        Double aDouble = 50.40;
        String s = MathUtil.double2String(aDouble);
        Double aDouble1 = MathUtil.keepDigitsAfterPoint(aDouble, 3);
        System.out.println(aDouble1);
    }
}
