package com.jet.cloud.deepmind.controller.report;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.Report;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.report.ReportManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author maohandong
 * @create 2019/11/4 11:15
 * @Description: 报表管理接口
 */
@RestController
@RequestMapping("/reportManage")
public class ReportManageController {

    @Autowired
    private ReportManageService reportManageService;

    /**
     * 查询报表
     *
     * @param data
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportName = data.getString("reportName");
        return reportManageService.query(objType, objId, reportName);
    }

    /**
     * 新增或修改报表
     *
     * @param report
     * @return
     */
    @PostMapping("/add")
    public Response add(@Valid @RequestBody Report report) {
        return reportManageService.addOrEdit(report).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody Report report) {
        return reportManageService.addOrEdit(report).getResponse();
    }

    /**
     * 删除报表,并删除此报表的展示参数及展示对象配置
     *
     * @param data
     * @return
     */
    @PostMapping("/delete")
    public Response delete(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        return reportManageService.delete(objType, objId, reportId).getResponse();
    }

    /**
     * 启用或停用报表
     *
     * @return
     */
    @PostMapping("/enableOrDisableReport")
    public Response enableOrDisableReport(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        String isUse = data.getString("isUse");
        return reportManageService.enableOrDisableReport(objType, objId, reportId, isUse).getResponse();
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
        reportManageService.exportExcel(objType, objId, reportId, fileName, response, request);
    }

    /**
     * 报表导入
     *
     * @param file
     * @param objType
     * @param objId
     * @param reportId
     * @param energyTypeId
     * @return
     */
    @PostMapping("/importExcel")
    public Response importExcel(MultipartFile file, String objType, String objId, String reportId, String energyTypeId) {
        return reportManageService.importExcel(file, objType, objId, reportId, energyTypeId).getResponse();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "ReportTemplate.xlsx";
            //设置要下载的文件的名称
            response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //获取文件的路径
            String filePath = getClass().getResource("/file/" + fileName).getPath();
            FileInputStream input = new FileInputStream(filePath);
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            response.setHeader("Content-Length", String.valueOf(input.getChannel().size()));
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
