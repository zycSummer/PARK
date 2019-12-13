package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.SysEnergyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 能源种类配置
 *
 * @author yhy
 * @create 2019-11-21 14:21
 */
@RestController
@RequestMapping("/sysEnergyType")
public class SysEnergyTypeController {


    @Autowired
    private SysEnergyTypeService sysEnergyTypeService;

    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String energyTypeId = data.getString("energyTypeId");
        String energyTypeNameLike = data.getString("energyTypeNameLike");
        return sysEnergyTypeService.query(energyTypeId, energyTypeNameLike);
    }

    @PostMapping("/add")
    public Response add(@RequestBody SysEnergyType sysEnergyType) {
        return sysEnergyTypeService.addOrEdit(sysEnergyType).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody SysEnergyType sysEnergyType) {
        return sysEnergyTypeService.addOrEdit(sysEnergyType).getResponse();
    }

    @GetMapping("/delete/{energyTypeId}")
    public Response delete(@PathVariable String energyTypeId) {
        return sysEnergyTypeService.delete(energyTypeId).getResponse();
    }

}
