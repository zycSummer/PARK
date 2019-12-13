package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.ProjectEnergyConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目能耗
 *
 * @author yhy
 * @create 2019-11-06 09:46
 */
@RestController
@RequestMapping("/projectEnergyConsume")
public class ProjectEnergyConsumeController {

    @Autowired
    private ProjectEnergyConsumeService projectEnergyConsumeService;

    /**
     * 实时负荷
     */
    @PostMapping("/realTimeLoad")
    public Response realTimeLoad(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String energyTypeId = data.getString("energyTypeId");
        return projectEnergyConsumeService.realTimeLoadResp(objType, objId, energyTypeId);
    }


    /**
     * 能耗日历
     *
     * @param data {"objType":"PARK","objId":"LYGSHCYJD","timestamp":1546272000000}
     * @return
     */
    @PostMapping("/calendar")
    public Response calendar(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        Long timestamp = data.getLong("timestamp");
        return projectEnergyConsumeService.calendar(objType, objId, timestamp);
    }

    /**
     * 能耗排名
     *
     * @param data {"objType":"PARK","objId":"LYGSHCYJD","timestamp":1546272000000,"energyTypeId":"water"}
     */
    @PostMapping("/rank")
    public Response rank(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String energyTypeId = data.getString("energyTypeId");
        Long timestamp = data.getLong("timestamp");
        return projectEnergyConsumeService.rank(objType, objId, energyTypeId, timestamp);
    }


    /**
     * 用量信息 - 饼图
     * timeUnit : year/month
     * {"objType":"PARK","objId":"LYGSHCYJD","timeUnit":"year"}
     */
    @PostMapping("/usageInfoPie")
    public Response usageInfoPie(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String timeUnit = data.getString("timeUnit");
        return projectEnergyConsumeService.usageInfoPie(objType, objId, timeUnit);
    }

    /**
     * 用量信息 - 月度对比
     * {"objType":"PARK","objId":"LYGSHCYJD","timestamp":1572537600000,"energyTypeId":"water"}
     */
    @PostMapping("/usageInfoCompare")
    public Response usageInfoCompare(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String energyTypeId = data.getString("energyTypeId");
        Long timestamp = data.getLong("timestamp");
        return projectEnergyConsumeService.usageInfoCompare(objType, objId, energyTypeId, timestamp);
    }
}
