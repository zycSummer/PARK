package com.jet.cloud.deepmind.controller.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.ReportParaDetail;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.report.ReportParaDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/7 13:43
 * @Description: 报表参数明细管理接口
 */
@RestController
@RequestMapping("/reportParaDetail")
public class reportParaDetailController {

    @Autowired
    private ReportParaDetailService reportParaDetailService;

    /**
     * 查询报表参数明细
     *
     * @param data
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        JSONArray energyParaIds = data.getJSONArray("energyParaIds");
        String displayName = data.getString("displayName");
        String energyTypeId = data.getString("energyTypeId");
        return reportParaDetailService.query(objType, objId, reportId, energyParaIds, displayName, energyTypeId);
    }

    /**
     * 新增或修改报表参数明细
     *
     * @param reportParaDetail
     * @return
     */
    @PostMapping("/add")
    public Response add(@Valid @RequestBody ReportParaDetail reportParaDetail) {
        return reportParaDetailService.addOrEdit(reportParaDetail).getResponse();

    }

    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody ReportParaDetail reportParaDetail) {
        return reportParaDetailService.addOrEdit(reportParaDetail).getResponse();

    }

    /**
     * 删除报表明细
     *
     * @param data
     * @return
     */
    @PostMapping("/delete")
    public Response delete(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String reportId = data.getString("reportId");
        String energyParaId = data.getString("energyParaId");
        return reportParaDetailService.delete(objType, objId, reportId, energyParaId).getResponse();
    }
}
