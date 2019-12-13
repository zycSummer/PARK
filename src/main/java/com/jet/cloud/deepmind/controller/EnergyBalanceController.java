package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.EnergyBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 能源平衡
 *
 * @author yhy
 * @create 2019-11-07 16:39
 */
@RestController
@RequestMapping("/energyBalance")
public class EnergyBalanceController {

    @Autowired
    private EnergyBalanceService energyBalanceService;

    /**
     * 获取对象类别
     */
    @PostMapping("/getObjectClassType")
    public Response getObjectClassType(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String energyTypeId = data.getString("energyTypeId");
        return energyBalanceService.getObjectClassType(objType, objId, energyTypeId);
    }

    /**
     * @param data { "energyTypeId": "std_coal", "orgTreeId": "ORG_TREE_5", "objType": "SITE", "objId": "RTHGCC", "timestamp": 1573201060299, "timeUnit": "days" }
     */
    @PostMapping("/getTreeData")
    public Response getTreeData(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String orgTreeId = data.getString("orgTreeId");
        String energyTypeId = data.getString("energyTypeId");
        Long timestamp = data.getLong("timestamp");
        String timeUnit = data.getString("timeUnit");
        return energyBalanceService.getTreeData(objType, objId, orgTreeId, energyTypeId, timestamp, timeUnit);
    }


}
