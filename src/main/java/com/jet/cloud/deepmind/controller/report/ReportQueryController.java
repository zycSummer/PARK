package com.jet.cloud.deepmind.controller.report;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.ReportInfosVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.report.ReportQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author maohandong
 * @create 2019/11/12 11:48
 * @Description: 报表查询接口
 */
@RestController
@RequestMapping("/reportQuery")
public class ReportQueryController {

    @Autowired
    private ReportQueryService reportQueryService;

    @PostMapping("/queryReport")
    public Response queryReport(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        return reportQueryService.queryReport(objType, objId);
    }

    /**
     * 报表查询
     * timeUnit(year/month/day)
     *
     * @param data
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        Long date = data.getLong("date");
        String timeUnit = data.getString("timeUnit");
        return reportQueryService.query(objType, objId, reportId, date, timeUnit);
    }


    /**
     * 导出excel
     *
     * @param objType
     * @param objId
     * @param reportId
     * @param timeUnit
     * @param data
     * @param response
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam String objType, @RequestParam String objId, @RequestParam String reportId,
                            @RequestParam String timeUnit, @RequestParam Long data, @RequestParam String fileName, HttpServletResponse response, HttpServletRequest request) {
        ReportInfosVO reportInfosVO = (ReportInfosVO) reportQueryService.query(objType, objId, reportId, data, timeUnit).getOne();
        reportQueryService.exportExcel(reportInfosVO, response,request, objType, objId, reportId, fileName);
    }
}
