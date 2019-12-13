package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.EnergyMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuyicheng
 * @create 2019/11/28 13:39
 * @desc 组态画面
 */
@RestController
@RequestMapping("/htImg")
public class HtImgController {
    @Autowired
    private EnergyMonitoringService energyMonitoringService;

    /**
     * 右侧组态画面查询
     */
    @PostMapping("/queryRightHtImg")
    public Response queryRightHtImg(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String htImgId = data.getString("htImgId");
        return energyMonitoringService.queryRightHtImg(objType, objId, htImgId);
    }
}
