package com.jet.cloud.deepmind.controller.report;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.ReportObjDetail;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.report.ReportObjDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/7 14:53
 */
@RestController
@RequestMapping("/reportObjDetail")
public class ReportObjDetailController {

    @Autowired
    private ReportObjDetailService reportObjDetailService;
    @Autowired
    private CommonService commonService;

    /**
     * 查询树型结构
     *
     * @param data
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        return reportObjDetailService.query(objType, objId, reportId);
    }

    /**
     * 新增或修改报表对象明细
     *
     * @param reportObjDetail
     * @return
     */
    @PostMapping("/add")
    public Response add(@Valid @RequestBody ReportObjDetail reportObjDetail) {
        return reportObjDetailService.addOrEdit(reportObjDetail).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody ReportObjDetail reportObjDetail) {
        return reportObjDetailService.addOrEdit(reportObjDetail).getResponse();
    }

    /**
     * 删除报表对象明细
     *
     * @param data
     * @return
     */
    @PostMapping("/delete")
    public Response delete(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        String nodeId = data.getString("nodeId");
        return reportObjDetailService.delete(objType, objId, reportId, nodeId).getResponse();
    }

    /**
     * 导出excel
     *
     * @param objType
     * @param objId
     * @param reportId
     * @param response
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam String objType, @RequestParam String objId, @RequestParam String reportId, @RequestParam String fileName,
                            HttpServletResponse response, HttpServletRequest request) {
        reportObjDetailService.exportExcel(objType, objId, reportId, fileName, response, request);
    }

    /**
     * 报表导入
     *
     * @param file
     * @param objType
     * @param objId
     * @param reportId
     * @return
     */
    @PostMapping("/importExcel")
    public Response importExcel(MultipartFile file, String objType, String objId, String reportId) {
        return reportObjDetailService.importExcel(file, objType, objId, reportId).getResponse();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "ReportDisplayObjTemplate.xlsx";
        commonService.download(fileName, response);
    }
}
