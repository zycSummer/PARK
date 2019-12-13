package com.jet.cloud.deepmind.service.report;

import com.jet.cloud.deepmind.entity.Report;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/11/4 11:16
 */
public interface ReportManageService {


    Response query(String objType, String objId, String reportName);

    @Transactional
    ServiceData addOrEdit(Report report);

    ServiceData delete(String objType, String objId, String reportId);

    @Transactional
    ServiceData enableOrDisableReport(String objType, String objId, String reportId, String isUse);

    ServiceData importExcel(MultipartFile file, String objType, String objId, String reportId, String energyTypeId);

    void exportExcel(String objType, String objId, String reportId, String fileName, HttpServletResponse response, HttpServletRequest request);
}
