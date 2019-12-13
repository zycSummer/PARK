package com.jet.cloud.deepmind.service.report.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.ReportObjDetailModel;
import com.jet.cloud.deepmind.model.ReportParaDetailModel;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.repository.SysEnergyParaRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.repository.report.ReportManageRepo;
import com.jet.cloud.deepmind.repository.report.ReportObjDetailRepo;
import com.jet.cloud.deepmind.repository.report.ReportParaDetailRepo;
import com.jet.cloud.deepmind.service.impl.AfterWriteHandlerImpl;
import com.jet.cloud.deepmind.service.impl.ExcelModelListener;
import com.jet.cloud.deepmind.service.report.ReportManageService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/4 11:17
 */
@Service
public class ReportManageServiceImpl implements ReportManageService {

    @Autowired
    private ReportManageRepo reportManageRepo;

    @Autowired
    private ReportParaDetailRepo reportParaDetailRepo;

    @Autowired
    private ReportObjDetailRepo reportObjDetailRepo;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;

    @Autowired
    private ParkRepo parkRepo;

    @Autowired
    private SiteRepo siteRepo;

    @Override
    public Response query(String objType, String objId, String reportName) {
        Sort sort = new Sort(Sort.Direction.ASC, "sortId");
        QReport obj = QReport.report;
        Predicate pre = obj.isNotNull();
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        if (reportName != null && !reportName.equals("")) {
            pre = ExpressionUtils.and(pre, obj.reportName.containsIgnoreCase(reportName));
        }
        List<Report> reportList = (List<Report>) reportManageRepo.findAll(pre, sort);
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
            report.setEnergyTypeName(energyTypeName);
            report.setShowName("[" + energyTypeName + "]" + "[" + report.getReportId() + "]" + report.getReportName());
        }
        Response ok = Response.ok(reportList);
        ok.setQueryPara(objId, objType, reportName);
        return ok;
    }

    @Override
    @Transactional
    public ServiceData enableOrDisableReport(String objType, String objId, String reportId, String isUse) {
        try {
            if (isUse.equalsIgnoreCase("Y")) {
                reportManageRepo.updateReportState(objType, objId, "N", reportId);
                return ServiceData.success("停用成功", currentUser);
            } else {
                reportManageRepo.updateReportState(objType, objId, "Y", reportId);
                return ServiceData.success("启用成功", currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("启用或停用失败", e, currentUser);
        }
    }

    @Override
    @Transactional
    public ServiceData addOrEdit(Report report) {
        try {
            String objType = report.getObjType();
            String objId = report.getObjId();
            String reportId = report.getReportId();
            Report old = reportManageRepo.findByObjTypeAndObjIdAndReportId(objType, objId, reportId);
            if (report.getId() == null) {
                if (old != null) {
                    return ServiceData.error("报表标识在园区/当前企业唯一", currentUser);
                }
                report.setCreateNow();
                report.setCreateUserId(currentUser.userId());
                reportManageRepo.save(report);
            } else {
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setMemo(report.getMemo());
                old.setReportName(report.getReportName());
                old.setEnergyTypeId(report.getEnergyTypeId());
                old.setIsUse(report.getIsUse());
                old.setSortId(report.getSortId());
                reportManageRepo.save(old);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改报表失败", currentUser);
        }
        return ServiceData.success("新增或修改报表成功", currentUser);
    }

    @Override
    @Transactional
    public ServiceData delete(String objType, String objId, String reportId) {
        try {
            reportManageRepo.deleteByObjTypeAndObjIdAndReportId(objType, objId, reportId);
            reportParaDetailRepo.deleteByObjTypeAndObjIdAndReportId(objType, objId, reportId);
            reportObjDetailRepo.deleteByObjTypeAndObjIdAndReportId(objType, objId, reportId);
            return ServiceData.success("删除报表成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除报表失败", currentUser);
        }
    }

    @Override
    public void exportExcel(String objType, String objId, String reportId, String fileName, HttpServletResponse response, HttpServletRequest request) {
        ExcelWriter writer = null;
        String projectName = null;
        try {
            if (objType.equals("PARK")) {
                Park park = parkRepo.findByParkId(objId);
                projectName = "[" + park.getParkId() + "]" + park.getParkName();
            } else {
                Site site = siteRepo.findBySiteId(objId);
                projectName = "[" + site.getSiteId() + "]" + site.getSiteName();
            }
            String userAgent = request.getHeader("User-Agent");
            String name = projectName + fileName;
            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment; filename=" + StringUtils.resolvingScrambling(name, userAgent) + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            response.setDateHeader("Expires", 0);//设置日期头
            //实例化 ExcelWriter
            writer = EasyExcelFactory.getWriterWithTempAndHandler(null, outputStream, ExcelTypeEnum.XLSX, true, new AfterWriteHandlerImpl());
            //实例化表单
            Sheet sheet1 = new Sheet(1, 0, ReportParaDetailModel.class);
            Sheet sheet2 = new Sheet(2, 0, ReportObjDetailModel.class);
            sheet1.setSheetName("para");
            sheet2.setSheetName("obj");
            //获取ReportObjDetail数据
            List<ReportObjDetail> reportObjDetails = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortIdAsc(objType, objId, reportId);
            List<ReportObjDetailModel> reportObjDetailModelList = new ArrayList<>();
            for (ReportObjDetail reportObjDetail : reportObjDetails) {
                ReportObjDetailModel reportObjDetailModel = new ReportObjDetailModel();
                reportObjDetailModel.setNodeId(reportObjDetail.getNodeId());
                reportObjDetailModel.setNodeName(reportObjDetail.getNodeName());
                reportObjDetailModel.setParentId(reportObjDetail.getParentId());
                reportObjDetailModel.setSortId(reportObjDetail.getSortId());
                reportObjDetailModel.setDataSource(reportObjDetail.getDataSource());
                reportObjDetailModel.setMemo(reportObjDetail.getMemo());
                reportObjDetailModelList.add(reportObjDetailModel);
            }
            //获取ReportObjDetail数据
            List<ReportParaDetail> reportParaDetails = reportParaDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortId(objType, objId, reportId);
            List<ReportParaDetailModel> reportParaDetailModelList = new ArrayList<>();
            for (ReportParaDetail reportParaDetail : reportParaDetails) {
                ReportParaDetailModel reportParaDetailModel = new ReportParaDetailModel();
                reportParaDetailModel.setEnergyParaId(reportParaDetail.getEnergyParaId());
                reportParaDetailModel.setDisplayName(reportParaDetail.getDisplayName());
                reportParaDetailModel.setTimeValue(reportParaDetail.getTimeValue());
                reportParaDetailModel.setMaxValue(reportParaDetail.getMaxValue());
                reportParaDetailModel.setMinValue(reportParaDetail.getMinValue());
                reportParaDetailModel.setDiffValue(reportParaDetail.getDiffValue());
                reportParaDetailModel.setSortId(reportParaDetail.getSortId());
                reportParaDetailModel.setMemo(reportParaDetail.getMemo());
                reportParaDetailModelList.add(reportParaDetailModel);
            }
            //输出
            writer.write(reportParaDetailModelList, sheet1);
            writer.write(reportObjDetailModelList, sheet2);
            writer.finish();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional
    public ServiceData importExcel(MultipartFile file, String objType, String objId, String reportId, String energyTypeId) {
        String message = "";
        try {
            if (StringUtils.isNullOrEmpty(file)) {
                return ServiceData.error("请选择配置文件", currentUser);
            }
            List<SysEnergyPara> sysEnergyParaList = sysEnergyParaRepo.findByEnergyTypeIdOrderBySortId(energyTypeId);
            ArrayList<String> energyParaIds = new ArrayList<>();
            for (SysEnergyPara sysEnergyPara : sysEnergyParaList) {
                String e = sysEnergyPara.getEnergyParaId();
                energyParaIds.add(e);
            }
            ExcelModelListener modelListener = ExcelModelListener.create(energyParaIds, energyTypeId);
            EasyExcelFactory.readBySax(file.getInputStream(), new Sheet(1, 1, ReportParaDetailModel.class), modelListener);
            EasyExcelFactory.readBySax(file.getInputStream(), new Sheet(2, 1, ReportObjDetailModel.class), modelListener);
            List<ReportObjDetail> reportObjDetails = modelListener.getReportObjDetails();
            List<ReportParaDetail> reportParaDetails = modelListener.getReportParaDetails();
            List<Exception> exceptionList = modelListener.getExceptionList();
            List<String> msg = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(exceptionList)) {
                message += "excel批量导入失败";
                for (Exception exception : exceptionList) {
                    msg.add(exception.getMessage());
                }
                return new ServiceData(new Response(HttpConstants.IMPORTEXCELFAIL, msg), message, false);
            } else {
                if (StringUtils.isNotNullAndEmpty(reportObjDetails, reportParaDetails)) {
                    message += "excel批量导入成功";
                    reportParaDetailRepo.deleteByObjTypeAndObjIdAndReportId(objType, objId, reportId);
                    reportObjDetailRepo.deleteByObjTypeAndObjIdAndReportId(objType, objId, reportId);
                    reportObjDetailRepo.flush();
                    reportParaDetailRepo.flush();
                    for (ReportParaDetail reportParaDetail : reportParaDetails) {
                        reportParaDetail.setObjType(objType);
                        reportParaDetail.setObjId(objId);
                        reportParaDetail.setReportId(reportId);
                        reportParaDetail.setCreateNow();
                        reportParaDetail.setCreateUserId(currentUser.userId());
                    }
                    reportParaDetailRepo.saveAll(reportParaDetails);
                    for (ReportObjDetail reportObjDetail : reportObjDetails) {
                        reportObjDetail.setObjId(objId);
                        reportObjDetail.setObjType(objType);
                        reportObjDetail.setReportId(reportId);
                        reportObjDetail.setCreateNow();
                        reportObjDetail.setCreateUserId(currentUser.userId());
                    }
                    reportObjDetailRepo.saveAll(reportObjDetails);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
        return ServiceData.success(message, currentUser);
    }
}
