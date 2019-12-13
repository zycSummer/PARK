package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.SysEnergyGrade;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.SysEnergyGradeService;
import com.jet.cloud.deepmind.service.SysEnergyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 能耗强度等级
 *
 * @author yhy
 * @create 2019-11-22 10:40
 */
@RestController
@RequestMapping("/sysEnergyGrade")
public class SysEnergyGradeController {


    @Autowired
    private SysEnergyGradeService sysEnergyGradeService;

    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String energyGradeId = data.getString("energyGradeId ");
        return sysEnergyGradeService.query(energyGradeId);
    }

    @PostMapping("/add")
    public Response add(@RequestBody SysEnergyGrade energyGrade) {
        return sysEnergyGradeService.addOrEdit(energyGrade).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody SysEnergyGrade energyGrade) {
        return sysEnergyGradeService.addOrEdit(energyGrade).getResponse();
    }

    @GetMapping("/delete/{energyGradeId}")
    public Response delete(@PathVariable String energyGradeId) {
        return sysEnergyGradeService.delete(energyGradeId).getResponse();
    }


}
