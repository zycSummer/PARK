package com.jet.cloud.deepmind.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.AppResult;
import com.jet.cloud.deepmind.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhuyicheng
 * @create 2019/12/23 10:36
 * @desc App接口
 */
@RequestMapping("/app/v1")
@RestController
public class AppController {
    @Autowired
    private AppService appService;

    /**
     * @param jsonObject
     * @return
     * @apiNote 用户登录
     */
    @PostMapping("/login")
    public AppResult login(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userId = jsonObject.getString("userId");
        String password = jsonObject.getString("password");
        return appService.login(userId, password, request);
    }

    /**
     * @param request
     * @return
     * @apiNote 用户登出
     */
    @PostMapping("/logout")
    public AppResult logout(HttpServletRequest request) {
        return appService.logout(request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 用户修改密码
     */
    @PostMapping("/change-pwd")
    public AppResult changePwd(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String userId = jsonObject.getString("userId");
        String oldPassword = jsonObject.getString("oldPassword");
        String newPassword = jsonObject.getString("newPassword");
        return appService.changePwd(userId, oldPassword, newPassword, request);
    }

    /**
     * @param request
     * @return
     * @apiNote 获取对象列表
     */
    @PostMapping("/obj/list")
    public AppResult objList(HttpServletRequest request) {
        return appService.objList(request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 能耗概况-获取对象能耗概况数据
     */
    @PostMapping("/obj/energy-summary")
    public AppResult objEnergySummary(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String timeType = jsonObject.getString("timeType");
        Long timeValue = jsonObject.getLong("timeValue");
        return appService.objEnergySummary(objType, objId, timeType, timeValue, request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 能耗排名-获取对象能耗排名数据
     */
    @PostMapping("/obj/energy-rank")
    public AppResult objEnergyRank(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String energyType = jsonObject.getString("energyType");
        String timeType = jsonObject.getString("timeType");
        Long timeValue = jsonObject.getLong("timeValue");
        return appService.objEnergyRank(objType, objId, energyType, timeType, timeValue, request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 能耗分析-获取对象能耗分析数据
     */
    @PostMapping("/obj/energy-analysis")
    public AppResult objEnergyAnalysis(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String energyType = jsonObject.getString("energyType");
        String timeType = jsonObject.getString("timeType");
        Long timeValue = jsonObject.getLong("timeValue");
        return appService.objEnergyAnalysis(objType, objId, energyType, timeType, timeValue, request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 报警&交互-获取对象报警信息列表
     */
    @PostMapping("/obj/alarm/list")
    public AppResult objAlarmList(@RequestBody JSONObject jsonObject,HttpServletRequest request){
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        Long startTime = jsonObject.getLong("startTime");
        Long endTime = jsonObject.getLong("endTime");
        return appService.objAlarmList(objType,objId,startTime,endTime,request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 报警&交互-获取对象报警信息明细
     */
    @PostMapping("/obj/alarm/info")
    public AppResult objAlarmInfo(@RequestBody JSONObject jsonObject,HttpServletRequest request){
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String alarmId = jsonObject.getString("alarmId");
        Long alarmTime = jsonObject.getLong("alarmTime");
        return appService.objAlarmInfo(objType,objId,alarmId,alarmTime,request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 报警&交互-获取对象公告信息列表
     */
    @PostMapping("/obj/notice/list")
    public AppResult objNoticeList(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        return appService.objNoticeList(objType, objId, request);
    }

    /**
     * @param jsonObject
     * @param request
     * @return
     * @apiNote 报警&交互-获取对象公告信息明细
     */
    @PostMapping("/obj/notice/info")
    public AppResult objNoticeInfo(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        Long noticeTime = jsonObject.getLong("noticeTime");
        return appService.objNoticeInfo(objType, objId, noticeTime, request);
    }
}
