package com.jet.cloud.deepmind.controller.report;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.ReportObjDetail;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.report.ReportObjDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author maohandong
 * @create 2019/11/7 14:53
 */
@RestController
@RequestMapping("/reportObjDetail")
public class ReportObjDetailController {

    @Autowired
    private ReportObjDetailService reportObjDetailService;

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
     * @param objType
     * @param objId
     * @param reportId
     * @param response
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam String objType, @RequestParam String objId, @RequestParam String reportId, @RequestParam String fileName,
                            HttpServletResponse response, HttpServletRequest request) {
        reportObjDetailService.exportExcel(objType, objId, reportId,fileName,response,request);
    }
    /**
     * 报表导入
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
    public void download(HttpServletResponse response){
        try {
            //获取要下载的模板名称
            String fileName = "ReportDisplayObjTemplate.xlsx";
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
            throw new RuntimeException("应用导入模板下载失败！");
        }
    }
}
