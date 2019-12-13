package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.Meter;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.MeterService;
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
import java.util.List;

/**
 * 仪表
 *
 * @author yhy
 * @create 2019-11-13 14:33
 * @desc 基础数据(仪表管理)
 */
@RestController
@RequestMapping("/meter")
public class MeterController {

    @Autowired
    private MeterService meterService;

    @PostMapping("/getAllCurrentSite")
    public Response getAllMeterBySite(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String key = data.getString("key");
        return meterService.getAllMeterBySiteResp(objType, objId, key);
    }

    @PostMapping("/getCurrentSiteByEnergyTypeId")
    public Response getCurrentSiteByEnergyTypeId(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String energyTypeId = data.getString("energyTypeId");
        String key = data.getString("key");
        return meterService.getCurrentSiteByEnergyTypeId(objType, objId, energyTypeId, key);
    }

    /**
     * 查询仪表（分页）
     *
     * @param vo
     * @return
     */
    @PostMapping("/queryMeter")
    public Response queryMeter(@RequestBody QueryVO vo) {
        return meterService.queryMeter(vo);
    }
    /**
     * 根据id查询仪表
     *
     * @param id
     * @return
     */
    @GetMapping("/queryMeterById/{id}")
    public Response queryMeterById(@PathVariable Integer id) {
        return meterService.queryMeterById(id);
    }

    /**
     * @return
     * @apiNote 对象管理(新增或者修改仪表)
     */
    @PostMapping("/add")
    public Response add(@RequestBody @Valid Meter meter) {
        return meterService.addOrEditMeter(meter).getResponse();
    }

    /**
     * @return
     * @apiNote 对象管理(新增或者修改仪表)
     */
    @PostMapping("/edit")
    public Response edit(@RequestBody @Valid Meter meter) {
        return meterService.addOrEditMeter(meter).getResponse();
    }

    /**
     * 删除一个或多个仪表
     *
     * @return
     */
    @PostMapping("/delete")
    public Response delete(@RequestBody List<Integer> idList) {
        return meterService.delete(idList).getResponse();
    }


    /**
     * 导出仪表
     * @param response
     * @param objType
     * @param objId
     * @param meterId
     * @param meterName
     * @param energyTypeId
     */
    @GetMapping("/exportExcel")
    public void exportExcel(String objType,String objId,String meterId,String meterName,JSONArray energyTypeId,HttpServletResponse response,HttpServletRequest request) {
        meterService.exportExcel(objType,objId,meterId,meterName,energyTypeId,response,request);
    }


    /**
     * 导入仪表信息
     *
     * @param file
     * @param objType
     * @param objId
     * @return
     */
    @PostMapping("/importExcel")
    public Response importExcel(MultipartFile file, String objType, String objId) {
        return meterService.importExcel(file, objType, objId).getResponse();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "MeterTemplate.xlsx";
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
