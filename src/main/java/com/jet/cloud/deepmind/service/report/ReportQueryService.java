package com.jet.cloud.deepmind.service.report;

import com.jet.cloud.deepmind.model.ReportInfosVO;
import com.jet.cloud.deepmind.model.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author maohandong
 * @create 2019/11/12 11:50
 */
public interface ReportQueryService {
    Response queryReport(String objType, String objId);

    Response<ReportInfosVO> query(String objType, String objId, String reportId, Long date, String timeUnit);

    void exportExcel(ReportInfosVO reportInfosVO, HttpServletResponse response, HttpServletRequest request, String objType, String objId, String reportId, String fileName);
}
