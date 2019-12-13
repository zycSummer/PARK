package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.SysEnergyParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yhy
 * @create 2019-11-21 15:10
 */
@RestController
@RequestMapping("/sysEnergyPara")
public class SysEnergyParaController {

    @Autowired
    private SysEnergyParaService sysEnergyParaService;


    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String energyTypeId = data.getString("energyTypeId");
        String energyParaId = data.getString("energyParaId");
        String energyParaNameLike = data.getString("energyParaNameLike");
        return sysEnergyParaService.query(energyTypeId, energyParaId, energyParaNameLike);
    }

    @PostMapping("/add")
    public Response add(@RequestBody SysEnergyPara energyPara) {
        return sysEnergyParaService.addOrEdit(energyPara).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody SysEnergyPara energyPara) {
        return sysEnergyParaService.addOrEdit(energyPara).getResponse();
    }

    @GetMapping("/delete/{energyParaId}")
    public Response delete(@PathVariable String energyParaId) {
        return sysEnergyParaService.delete(energyParaId).getResponse();
    }


}
