package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.CoManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author maohandong
 * @create 2019/10/28 9:49
 */

@RestController
@RequestMapping("/co2Manage")
public class CoManageController {

    @Autowired
    private CoManageService coManageService;

    @PostMapping("/getData")
    public Response getData(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String time = data.getString("time");
        String timeType = data.getString("timeType");
        return coManageService.getData(objType, objId, time, timeType);
    }
}
