package com.jet.cloud.deepmind.service.report.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.Park;
import com.jet.cloud.deepmind.entity.ReportObjDetail;
import com.jet.cloud.deepmind.entity.Site;
import com.jet.cloud.deepmind.model.ReportObjDetailModel;
import com.jet.cloud.deepmind.model.ReportObjDetailVo;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.repository.report.ReportObjDetailRepo;
import com.jet.cloud.deepmind.service.impl.AfterWriteHandlerImpl;
import com.jet.cloud.deepmind.service.impl.ExcelModelListener;
import com.jet.cloud.deepmind.service.report.ReportObjDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/7 14:55
 */
@Service
public class ReportObjDetailServiceImpl implements ReportObjDetailService {

    @Autowired
    private ReportObjDetailRepo reportObjDetailRepo;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private ParkRepo parkRepo;

    @Autowired
    private SiteRepo siteRepo;

    @Override
    public Response query(String objType, String objId, String reportId) {
        List<ReportObjDetail> allList = null;
        String parentName = null;
        try {
            allList = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortIdAsc(objType, objId, reportId);
            HashMap<String, String> map = new HashMap<>();
            for (ReportObjDetail reportObjDetail : allList) {
                String parentId = reportObjDetail.getParentId();
                if (StringUtils.isNotNullAndEmpty(parentId)) {
                    ReportObjDetail reportObjDetailObj = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdAndNodeId(objType, objId, reportId, parentId);
                    parentName = reportObjDetailObj.getNodeName();
                }
                reportObjDetail.setParentName(parentName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ReportObjDetailVo> reportObjDetailVos = new ArrayList<>();
        if (StringUtils.isNotNullAndEmpty(allList)) {
            Multimap<String, ReportObjDetailVo> reportObjDetailVoMultimap = ArrayListMultimap.create();
            for (ReportObjDetail reportObjDetail : allList) {
                String parentId = reportObjDetail.getParentId();
                if ("".equals(parentId)) {
                    reportObjDetailVos.add(new ReportObjDetailVo(reportObjDetail));
                }
                reportObjDetailVoMultimap.put(reportObjDetail.getParentId(), new ReportObjDetailVo(reportObjDetail));
            }
            for (ReportObjDetailVo reportObjDetailVo : reportObjDetailVos) {
                addChild(reportObjDetailVo, reportObjDetailVoMultimap, 10);
            }
        }
        Response ok = Response.ok(reportObjDetailVos);
        ok.setQueryPara(objId, objType, reportId);
        return ok;
    }

    @Override
    public ServiceData addOrEdit(ReportObjDetail reportObjDetail) {
        try {
            String objType = reportObjDetail.getObjType();
            String objId = reportObjDetail.getObjId();
            String reportId = reportObjDetail.getReportId();
            String nodeId = reportObjDetail.getNodeId();
            ReportObjDetail old = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdAndNodeId(objType, objId, reportId, nodeId);
            if (reportObjDetail.getId() == null) {
                if (old != null) {
                    return ServiceData.error("报表对象明细对象不唯一", currentUser);
                }
                reportObjDetail.setCreateNow();
                reportObjDetail.setCreateUserId(currentUser.userId());
                reportObjDetailRepo.save(reportObjDetail);
            } else {
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setMemo(reportObjDetail.getMemo());
                old.setNodeName(reportObjDetail.getNodeName());
                old.setSortId(reportObjDetail.getSortId());
                old.setParentId(reportObjDetail.getParentId());
                old.setDataSource(reportObjDetail.getDataSource());
                reportObjDetailRepo.save(old);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改报表对象明细成功", e, currentUser);
        }
        return ServiceData.success("新增或修改报表对象明细成功", currentUser);

    }

    @Override
    @Transactional
    public ServiceData delete(String objType, String objId, String reportId, String nodeId) {
        try {
            reportObjDetailRepo.deleteByObjTypeAndObjIdAndReportIdAndNodeId(objType, objId, reportId, nodeId);
            List<ReportObjDetail> reportObjDetails = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdAndParentId(objType, objId, reportId, nodeId);
            if (StringUtils.isNotNullAndEmpty(reportObjDetails)) {
                reportObjDetailRepo.deleteAll(reportObjDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除报表对象明细失败", e, currentUser);
        }
        return ServiceData.success("删除报表对象明细成功", currentUser);
    }

    @Override
    public void exportExcel(String objType, String objId, String reportId, String fileName, HttpServletResponse response, HttpServletRequest request) {
        ExcelWriter writer = null;
        String projectName = null;
        try {
            if (objType.equals("SITE")) {
                Site site = siteRepo.findBySiteId(objId);
                projectName ="["+site.getSiteId()+"]"+site.getSiteName();
            } else {
                Park park = parkRepo.findByParkId(objId);
                projectName = "["+park.getParkId()+"]"+park.getParkName();
            }
            String userAgent = request.getHeader("User-Agent");
            String name = projectName + fileName+"_展示对象";
            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment; filename=" + StringUtils.resolvingScrambling(name, userAgent)+".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            response.setDateHeader("Expires", 0);//设置日期头
            //实例化 ExcelWriter
            writer = EasyExcelFactory.getWriterWithTempAndHandler(null, outputStream, ExcelTypeEnum.XLSX, true, new AfterWriteHandlerImpl());
            //实例化表单
            Sheet sheet1 = new Sheet(1, 0, ReportObjDetailModel.class);
            sheet1.setSheetName("obj");
            //获取ReportObjDetail数据
            List<ReportObjDetail> reportObjDetails = reportObjDetailRepo.findByObjTypeAndObjIdAndReportIdOrderBySortIdAsc(objType, objId, reportId);
            List<ReportObjDetailModel> reportObjDetailModelList = new ArrayList<>();
            for (ReportObjDetail reportObjDetail : reportObjDetails) {
                ReportObjDetailModel reportObjDetailModel = new ReportObjDetailModel();
                reportObjDetailModel.setNodeId(reportObjDetail.getNodeId());
                reportObjDetailModel.setNodeName(reportObjDetail.getNodeName());
                reportObjDetailModel.setMemo(reportObjDetail.getMemo());
                reportObjDetailModel.setParentId(reportObjDetail.getParentId());
                reportObjDetailModel.setSortId(reportObjDetail.getSortId());
                reportObjDetailModel.setDataSource(reportObjDetail.getDataSource());
                reportObjDetailModelList.add(reportObjDetailModel);
            }
            writer.write(reportObjDetailModelList, sheet1);
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

    @Transactional
    @Override
    public ServiceData importExcel(MultipartFile file, String objType, String objId, String reportId) {
        String message = "";
        try {
            if (StringUtils.isNullOrEmpty(file)) {
                return ServiceData.error("请选择展示对象文件", currentUser);
            }
            ExcelModelListener modelListener = ExcelModelListener.create();
            EasyExcelFactory.readBySax(file.getInputStream(), new Sheet(1, 1, ReportObjDetailModel.class), modelListener);
            List<ReportObjDetail> reportObjDetails = modelListener.getReportObjDetails();
            List<Exception> exceptionList = modelListener.getExceptionList();
            List<String> msg = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(exceptionList)) {
                message += "excel批量导入失败";
                for (Exception exception : exceptionList) {
                    msg.add(exception.getMessage());
                }
                return new ServiceData(new Response(HttpConstants.IMPORTEXCELFAIL, msg), message, false);
            } else {
                if (reportObjDetails.size() > 0) {
                    message += "excel批量导入成功";
                    reportObjDetailRepo.deleteByObjTypeAndObjIdAndReportId(objType, objId, reportId);
                    reportObjDetailRepo.flush();
                    for (ReportObjDetail reportObjDetail : reportObjDetails) {
                        reportObjDetail.setObjId(objId);
                        reportObjDetail.setReportId(reportId);
                        reportObjDetail.setCreateUserId(currentUser.userId());
                        reportObjDetail.setObjType(objType);
                        reportObjDetail.setCreateNow();
                    }
                    reportObjDetailRepo.saveAll(reportObjDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
        return ServiceData.success(message, currentUser);
    }

    private <T extends Object> void addChild(T t, Multimap<String, T> reportObjDetailVoMultimap, int size) {
        if (t instanceof ReportObjDetailVo) {
            ReportObjDetailVo reportObjDetailVo = (ReportObjDetailVo) t;
            if (size > 0 && reportObjDetailVo != null) {
                reportObjDetailVo.setChildren(new ArrayList<>());
                Collection<ReportObjDetailVo> objs = (Collection<ReportObjDetailVo>) reportObjDetailVoMultimap.get(reportObjDetailVo.getNodeId());
                if (objs.size() > 0) {
                    for (ReportObjDetailVo obj : objs) {
                        addChild((T) obj, reportObjDetailVoMultimap, --size);
                        reportObjDetailVo.getChildren().add(obj);
                    }
                } else {
                    reportObjDetailVo.setChildren(null);
                }
            }
        }
    }
}
