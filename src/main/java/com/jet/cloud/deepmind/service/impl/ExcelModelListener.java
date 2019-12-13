package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.Meter;
import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import com.jet.cloud.deepmind.entity.ReportObjDetail;
import com.jet.cloud.deepmind.entity.ReportParaDetail;
import com.jet.cloud.deepmind.model.MeterModel;
import com.jet.cloud.deepmind.model.OrgTreeDetailModel;
import com.jet.cloud.deepmind.model.ReportObjDetailModel;
import com.jet.cloud.deepmind.model.ReportParaDetailModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/***
 *  监听器
 */
public class ExcelModelListener extends AnalysisEventListener {

    private Set<ReportParaDetailModel> reportParaDetailModels = new LinkedHashSet<>();
    private Set<ReportObjDetailModel> reportObjDetailModels = new LinkedHashSet<>();
    private Set<OrgTreeDetailModel> orgTreeDetailModels = new LinkedHashSet<>();
    private Set<MeterModel> meterModels = new LinkedHashSet<>();

    private List<ReportParaDetail> reportParaDetails;
    private List<ReportObjDetail> reportObjDetails;
    private List<OrgTreeDetail> orgTreeDetails;
    private List<Meter> meterLists;

    private List<Exception> exceptionList;

    private List<String> energyTypeIds;

    private List<String> energyParaIds;

    private String energyTypeId;

    public static ExcelModelListener create(ArrayList<String> energyTypeIds) {
        return new ExcelModelListener(energyTypeIds);
    }

    public static ExcelModelListener create(ArrayList<String> energyParaIds, String energyTypeId) {
        return new ExcelModelListener(energyParaIds, energyTypeId);
    }

    public static ExcelModelListener create() {
        return new ExcelModelListener();
    }

    public ExcelModelListener(List<String> energyParaIds, String energyTypeId) {
        this.energyParaIds = energyParaIds;
        this.energyTypeId = energyTypeId;
    }

    public ExcelModelListener() {
    }

    public ExcelModelListener(List<String> energyTypeIds) {
        this.energyTypeIds = energyTypeIds;
    }


    public List<OrgTreeDetail> getOrgTreeDetails() {
        return orgTreeDetails;
    }

    public void setOrgTreeDetails(List<OrgTreeDetail> orgTreeDetails) {
        this.orgTreeDetails = orgTreeDetails;
    }

    public List<ReportParaDetail> getReportParaDetails() {
        return reportParaDetails;
    }

    public void setReportParaDetails(List<ReportParaDetail> reportParaDetails) {
        this.reportParaDetails = reportParaDetails;
    }

    public List<ReportObjDetail> getReportObjDetails() {
        return reportObjDetails;
    }

    public void setReportObjDetails(List<ReportObjDetail> reportObjDetails) {
        this.reportObjDetails = reportObjDetails;
    }

    public List<Meter> getMeterLists() {
        return meterLists;
    }

    public void setMeterLists(List<Meter> meterLists) {
        this.meterLists = meterLists;
    }

    public List<Exception> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(Exception e) {
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        }
        this.exceptionList.add(e);
    }

    /**
     * 解析监听器，每解析一行会回调invoke()方法。整个excel解析结束会执行doAfterAllAnalysed()方法
     *
     * @param o
     * @param analysisContext
     */
    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        if (o instanceof ReportParaDetailModel) {
            reportParaDetailModels.add((ReportParaDetailModel) o);
        } else if (o instanceof ReportObjDetailModel) {
            reportObjDetailModels.add((ReportObjDetailModel) o);
        } else if (o instanceof MeterModel) {
            meterModels.add((MeterModel) o);
        } else if (o instanceof OrgTreeDetailModel) {
            orgTreeDetailModels.add((OrgTreeDetailModel) o);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        int sheetNo = context.getCurrentSheet().getSheetNo();
        Object o = context.getCurrentRowAnalysisResult();
        if (o instanceof ReportParaDetailModel) {
            setReportParaDetails(turnReportParaDetail(sheetNo));
        } else if (o instanceof ReportObjDetailModel) {
            setReportObjDetails(turnReportObjDetail(sheetNo));
        } else if (o instanceof MeterModel) {
            setMeterLists(turnMeter(sheetNo));
        } else if (o instanceof OrgTreeDetailModel) {
            setOrgTreeDetails(turnOrgTreeDetail(sheetNo));
        }

    }

    private List<OrgTreeDetail> turnOrgTreeDetail(int sheetNo) {
        ArrayList<OrgTreeDetail> list = new ArrayList<>();
        int countRow = 1;
        for (OrgTreeDetailModel orgTreeDetailModel : orgTreeDetailModels) {
            countRow++;

            try {
                OrgTreeDetail orgTreeDetail = new OrgTreeDetail();
                String nodeId = orgTreeDetailModel.getNodeId();
                if (StringUtils.isNotNullAndEmpty(nodeId)) {
                    if (nodeId.matches("^[A-Za-z0-9]{0,20}$")) {
                        orgTreeDetail.setNodeId(nodeId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeId只能输入字母和数字且不能大于20位");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeId不能为空");
                }
                String nodeName = orgTreeDetailModel.getNodeName();
                if (StringUtils.isNotNullAndEmpty(nodeName)) {
                    if (nodeName.length() <= 30) {
                        orgTreeDetail.setNodeName(nodeName);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeName长度不超过30");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeName不能为空");
                }

                String parentId = orgTreeDetailModel.getParentId();
                if (StringUtils.isNullOrEmpty(parentId)) {
                    orgTreeDetail.setParentId("");
                } else {
                    orgTreeDetail.setParentId(parentId);
                }
                String sortId = orgTreeDetailModel.getSortId();
                if (sortId != null) {
                    if (sortId.matches("^[A-Za-z0-9]{0,10}$")) {
                        orgTreeDetail.setSortId(sortId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的sortId只能输入字母和数字且不能大于10位");
                    }
                } else {
                    orgTreeDetail.setSortId(sortId);
                }
                orgTreeDetail.setDataSource(orgTreeDetailModel.getDataSource());
                orgTreeDetail.setMemo(orgTreeDetailModel.getMemo());
                list.add(orgTreeDetail);
            } catch (Exception e) {
                e.printStackTrace();
                setExceptionList(e);
            }
        }
        return list;
    }

    private List<Meter> turnMeter(int sheetNo) {
        ArrayList<Meter> list = new ArrayList<>();
        int countRow = 1;
        for (MeterModel meterObj : meterModels) {
            countRow++;
            try {
                Meter meter = new Meter();
                String meterId = meterObj.getMeterId();
                if (StringUtils.isNotNullAndEmpty(meterId)) {
                    if (meterId.matches("^[A-Za-z0-9]([-_A-Za-z0-9]{0,20})$")) {
                        meter.setMeterId(meterId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的meterId要以字母、数字开头，字母、数字、中横线、下划线组合，且长度不能大于20位");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的meterId不能为空");
                }
                String meterName = meterObj.getMeterName();
                if (StringUtils.isNotNullAndEmpty(meterName)) {
                    if (meterName.length() <= 30) {
                        meter.setMeterName(meterName);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的meterName长度不超过30");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的meterName不能为空");
                }
                String energyTypeId = meterObj.getEnergyTypeId();
                if (StringUtils.isNotNullAndEmpty(energyTypeId)) {
                    if (energyTypeIds.contains(energyTypeId)) {
                        meter.setEnergyTypeId(energyTypeId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的energyTypeId不属于能源种类范围之内");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的energyTypeId不能为空");
                }
                String sortId = meterObj.getSortId();
                if (sortId != null) {
                    if (sortId.matches("^[A-Za-z0-9]{0,10}$")) {
                        meter.setSortId(sortId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的sortId只能输入字母和数字且不能大于10位");
                    }
                } else {
                    meter.setSortId(sortId);
                }

                String isRanking = meterObj.getIsRanking();
                if (StringUtils.isNotNullAndEmpty(isRanking)) {
                    if (isRanking.equals("Y")) {
                        meter.setIsRanking(true);
                    } else if (isRanking.equals("N")) {
                        meter.setIsRanking(false);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的isRanking只能输入Y或N");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的isRanking不能为空");
                }
                meter.setMemo(meterObj.getMemo());
                list.add(meter);
            } catch (Exception e) {
                e.printStackTrace();
                setExceptionList(e);
            }
        }
        return list;
    }

    private List<ReportObjDetail> turnReportObjDetail(int sheetNo) {
        ArrayList<ReportObjDetail> list = new ArrayList<>();
        int countRow = 1;
        for (ReportObjDetailModel objDetail : reportObjDetailModels) {
            countRow++;
            try {
                ReportObjDetail reportObjDetail = new ReportObjDetail();
                String nodeId = objDetail.getNodeId();
                if (StringUtils.isNotNullAndEmpty(nodeId)) {
                    if (nodeId.matches("^[A-Za-z0-9]{0,20}$")) {
                        reportObjDetail.setNodeId(nodeId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeId只能输入字母和数字且不能大于20位");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeId不能为空");
                }
                String nodeName = objDetail.getNodeName();
                if (StringUtils.isNotNullAndEmpty(nodeName)) {
                    if (nodeName.length() <= 30) {
                        reportObjDetail.setNodeName(nodeName);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeName长度不超过30");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的nodeName不能为空");
                }

                String parentId = objDetail.getParentId();
                if (StringUtils.isNullOrEmpty(parentId)) {
                    reportObjDetail.setParentId("");
                } else {
                    reportObjDetail.setParentId(parentId);
                }
                String sortId = objDetail.getSortId();
                if (sortId != null) {
                    if (sortId.matches("^[A-Za-z0-9]{0,10}$")) {
                        reportObjDetail.setSortId(sortId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的sortId只能输入字母和数字且不能大于10位");
                    }
                } else {
                    reportObjDetail.setSortId(sortId);
                }
                reportObjDetail.setDataSource(objDetail.getDataSource());
                reportObjDetail.setMemo(objDetail.getMemo());
                list.add(reportObjDetail);
            } catch (Exception e) {
                e.printStackTrace();
                setExceptionList(e);
            }
        }
        return list;
    }

    private List<ReportParaDetail> turnReportParaDetail(int sheetNo) {
        ArrayList<ReportParaDetail> list = new ArrayList<>();
        int countRow = 1;
        for (ReportParaDetailModel paraDetail : reportParaDetailModels) {
            countRow++;
            try {
                ReportParaDetail reportParaDetail = new ReportParaDetail();
                String energyParaId = paraDetail.getEnergyParaId();
                if (StringUtils.isNotNullAndEmpty(energyParaId)) {
                    if (energyParaIds.contains(energyParaId)) {
                        reportParaDetail.setEnergyParaId(energyParaId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的energyParaId不属于" + energyTypeId + "的参数");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的energyParaId不能为空");
                }
                String displayName = paraDetail.getDisplayName();
                if (StringUtils.isNotNullAndEmpty(displayName)) {
                    if (displayName.length() <= 20) {
                        reportParaDetail.setDisplayName(displayName);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的displayName长度不超过20");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的displayName不能为空");
                }
                String timeValue = paraDetail.getTimeValue();
                if (StringUtils.isNotNullAndEmpty(timeValue)) {
                    if (timeValue.equals("Y") || timeValue.equals("N")) {
                        reportParaDetail.setTimeValue(timeValue);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的timeValue只能为Y或N");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的timeValue不能为空");
                }
                String avgValue = paraDetail.getAvgValue();
                if (StringUtils.isNotNullAndEmpty(avgValue)) {
                    if (avgValue.equals("Y") || avgValue.equals("N")) {
                        reportParaDetail.setAvgValue(avgValue);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的avgValue只能为Y或N");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的avgValue不能为空");
                }
                String diffValue = paraDetail.getDiffValue();
                if (StringUtils.isNotNullAndEmpty(diffValue)) {
                    if (diffValue.equals("Y") || diffValue.equals("N")) {
                        reportParaDetail.setDiffValue(diffValue);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的diffValue只能为Y或N");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的diffValue不能为空");
                }
                String minValue = paraDetail.getMinValue();
                if (StringUtils.isNotNullAndEmpty(minValue)) {
                    if (minValue.equals("Y") || minValue.equals("N")) {
                        reportParaDetail.setMinValue(minValue);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的minValue只能为Y或N");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的minValue不能为空");
                }
                String maxValue = paraDetail.getMaxValue();
                if (StringUtils.isNotNullAndEmpty(maxValue)) {
                    if (maxValue.equals("Y") || maxValue.equals("N")) {
                        reportParaDetail.setMaxValue(maxValue);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的maxValue只能为Y或N");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的maxValue不能为空");
                }

                String sortId = paraDetail.getSortId();
                if (sortId != null) {
                    if (sortId.matches("^[A-Za-z0-9]{0,10}$")) {
                        reportParaDetail.setSortId(sortId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的sortId只能输入字母和数字且不能大于10位");
                    }
                } else {
                    reportParaDetail.setSortId(sortId);
                }
                reportParaDetail.setMemo(paraDetail.getMemo());
                list.add(reportParaDetail);
            } catch (Exception e) {
                e.printStackTrace();
                setExceptionList(e);
            }
        }
        return list;
    }
}