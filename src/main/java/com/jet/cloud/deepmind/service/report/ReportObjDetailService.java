package com.jet.cloud.deepmind.service.report;

import com.jet.cloud.deepmind.entity.ReportObjDetail;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author maohandong
 * @create 2019/11/7 14:55
 */
public interface ReportObjDetailService {
    Response query(String objType, String objId, String reportId);

    ServiceData addOrEdit(ReportObjDetail reportObjDetail);

    ServiceData delete(String objType, String objId, String reportId, String nodeId);

    ServiceData importExcel(MultipartFile file, String objType, String objId, String reportId);

    void exportExcel(String objType, String objId, String reportId, String fileName, HttpServletResponse response, HttpServletRequest request);
}
