package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.HtImg;
import com.jet.cloud.deepmind.model.HtImgVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.service.EnergyMonitoringService;
import com.jet.cloud.deepmind.socket.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/10/25 9:24
 * @desc 用能监测
 */
@RestController
@RequestMapping("/energyMonitoring")
public class EnergyMonitoringController {
    @Autowired
    private EnergyMonitoringService energyMonitoringService;
    @Autowired//消息发送模板
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 实时监测
     * 查询左侧树状结构
     *
     * @param data
     */
    @PostMapping("/queryLeftHtImg")
    public Response queryLeftHtImg(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        return energyMonitoringService.queryLeftHtImg(objType, objId);
    }

    /**
     * 实时监测
     * 右侧组态画面查询
     */
    @PostMapping("/queryRightHtImg")
    public Response queryRightHtImg(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String htImgId = data.getString("htImgId");
        return energyMonitoringService.queryRightHtImg(objType, objId, htImgId);
    }

    /**
     * 右侧组态画面新增
     *
     * @param htImg
     * @return
     */
    @PostMapping("/insertRightHtImg")
    public Response insertRightHtImg(@RequestBody HtImg htImg) {
        return energyMonitoringService.insertRightHtImg(htImg).getResponse();
    }

    /**
     * 右侧组态画面更新
     *
     * @param htImg
     * @return
     */
    @PostMapping("/updateRightHtImg")
    public Response updateRightHtImg(@RequestBody HtImg htImg) {
        return energyMonitoringService.updateRightHtImg(htImg).getResponse();
    }

    /**
     * 右侧组态画面删除
     * <p>
     * objType
     * ObjId
     * htImgId
     */
    @PostMapping("/deleteRightHtImg")
    public Response deleteRightHtImg(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String htImgId = jsonObject.getString("htImgId");
        ServiceData serviceData = energyMonitoringService.deleteRightHtImg(objType, objId, htImgId);
        return serviceData.getResponse();
    }

    /**
     * 实时监测-页面数据
     *
     * @return
     */
    @MessageMapping("/queryActualMonitorData")
    public void queryActualMonitorData(@RequestBody JSONObject jsonObject) {
        List<Map<String, Object>> opNameList = jsonObject.getObject("opNameList", List.class);
        String objId = jsonObject.getString("objId");
        String objType = jsonObject.getString("objType");
        try {
            Map<String, Object> mapLastData = energyMonitoringService.queryActualMonitorData(opNameList, objId, objType);
            simpMessagingTemplate.convertAndSend("/serverStatus/queryActualMonitorData", mapLastData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
