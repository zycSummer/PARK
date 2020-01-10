package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.BigScreen;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.service.ComprehensiveShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/10/23 15:39
 * @desc 综合展示
 */
@RestController
@RequestMapping("/comprehensiveShow")
public class ComprehensiveShowController {
    @Autowired
    private ComprehensiveShowService comprehensiveShowService;
    @Autowired
    private CurrentUser currentUser;
    @Autowired//消息发送模板
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * @apiNote 大屏展示组态编辑查询
     */
    @PostMapping("/querySiteImg")
    public Response querySiteImg(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        return comprehensiveShowService.querySiteImg(objType, objId, "Y");
    }

    /**
     * @apiNote 大屏展示组态编辑新增
     */
    @PostMapping("/insertSiteImg")
    public Response insertSiteImg(@RequestBody BigScreen bigScreen) {
        return comprehensiveShowService.insertSiteImg(bigScreen).getResponse();
    }

    /**
     * @apiNote 大屏展示组态编辑更新
     */
    @PostMapping("/updateSiteImg")
    public Response updateSiteImg(@RequestBody BigScreen bigScreen) {
        return comprehensiveShowService.updateSiteImg(bigScreen).getResponse();
    }

    /**
     * @apiNote 大屏展示组态编辑删除
     */
    @PostMapping("/deleteSiteImg")
    public Response deleteSiteImg(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String htImgId = jsonObject.getString("htImgId");
        ServiceData serviceData = comprehensiveShowService.deleteSiteImg(objType, objId, htImgId);
        return serviceData.getResponse();
    }

    /**
     * @apiNote 查询对应企业全部大屏展示组态编辑
     */
    @PostMapping("/queryAllSiteImg")
    public Response queryAllSiteImg(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        return comprehensiveShowService.queryAllSiteImg(objType, objId);
    }

    /**
     * @apiNote 根据HtImgId查询对应企业全部大屏展示组态编辑
     */
    @PostMapping("/queryAllSiteImgByHtImgId")
    public Response queryAllSiteImgByHtImgId(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String htImgId = jsonObject.getString("htImgId");
        return comprehensiveShowService.queryAllSiteImgByHtImgId(objType, objId, htImgId);
    }


    /**
     * @param jsonObject
     * @apiNote 综合展示中下角(各种能源或标煤 瞬时量 的当日差值)
     */
    @MessageMapping("/energyTodayDiffValue")
    public void energyTodayDiffValue(JSONObject jsonObject) {
        String paras = jsonObject.getString("paras");
        Integer key = jsonObject.getInteger("key");
        JSONObject j = JSON.parseObject(paras);
        List<Map<String, Object>> list = comprehensiveShowService.energyTodayDiffValue(j);
//      JSONArray jsonArray = JSONArray.parseArray(JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue));
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("paras", list);
        JSONObject real = (JSONObject) JSONObject.toJSON(map);
        simpMessagingTemplate.convertAndSend("/serverStatus/energyTodayDiffValue", real);
    }

    /**
     * @param datasource
     * @apiNote 上月万元GDP排名(标煤总量std_coal)
     */
    @MessageMapping("/energyRealTimeLoadRanking")
    public void energyRealTimeLoadRanking(@RequestParam JSONObject datasource, SimpMessageHeaderAccessor accessor) {
        Map<String, Object> map = accessor.getSessionAttributes();
        if (map == null) return;
        SysUser sysUser = (SysUser) map.get(Constants.SESSION_USER_ID);
        String paras = datasource.getString("paras");
        Integer key = datasource.getInteger("key");

        JSONObject jsonObject = JSON.parseObject(paras);
        String energyTypeId = jsonObject.getString("energyTypeId");
        EnergyRankingVO energyRankingVO = comprehensiveShowService.energyRealTimeLoadRanking(energyTypeId, sysUser);
        Map<String, Object> mapAll = new HashMap<>();
        mapAll.put("key", key);
        mapAll.put("paras", energyRankingVO);
        JSONObject rankingVO = (JSONObject) JSONObject.toJSON(mapAll);
        simpMessagingTemplate.convertAndSend("/serverStatus/energyRealTimeLoadRanking", rankingVO);
    }

    /**
     * @param datasource
     * @apiNote 综合展示当日能耗排名(从小到大) 电、水、蒸汽、标煤
     */
    @MessageMapping("/energyTodayUsageRanking")
    public void energyTodayUsageRanking(@RequestParam JSONObject datasource, SimpMessageHeaderAccessor accessor) {
        Map<String, Object> map = accessor.getSessionAttributes();
        if (map == null) return;
        SysUser sysUser = (SysUser) map.get(Constants.SESSION_USER_ID);
        String paras = datasource.getString("paras");
        Integer key = datasource.getInteger("key");

        JSONObject jsonObject = JSON.parseObject(paras);
        String energyTypeId = jsonObject.getString("energyTypeId");
        EnergyRankingVO energyRankingVO = comprehensiveShowService.energyTodayUsageRanking(energyTypeId, sysUser);
        Map<String, Object> mapAll = new HashMap<>();
        mapAll.put("key", key);
        mapAll.put("paras", energyRankingVO);
        JSONObject rankingVO = (JSONObject) JSONObject.toJSON(mapAll);
        simpMessagingTemplate.convertAndSend("/serverStatus/energyTodayUsageRanking", rankingVO);
    }

    /**
     * @param datasource
     * @apiNote 画面内容右边展示 各种能源 和 标煤 当月用量 、当月计划值、上月用量、上月计划值、同期环比，具体数据源在前台配置（基础数据-对象综合展示画面配置模块）
     */
    @MessageMapping("/energyConsumption")
    public void energyConsumption(JSONObject datasource) {
        String paras = datasource.getString("paras");
        Integer key = datasource.getInteger("key");

        JSONArray jsonArray = JSONArray.parseArray(paras);
        List<ConsumptionVO> consumptionVOS = jsonArray.toJavaList(ConsumptionVO.class);
        List<ConsumptionReturnVO> consumptionReturnVOs = comprehensiveShowService.energyConsumption(consumptionVOS);
//      JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(consumptionReturnVOs, SerializerFeature.WriteMapNullValue));
        Map<String, Object> mapAll = new HashMap<>();
        mapAll.put("key", key);
        mapAll.put("paras", consumptionReturnVOs);
        JSONObject consumption = (JSONObject) JSONObject.toJSON(mapAll);
        simpMessagingTemplate.convertAndSend("/serverStatus/energyConsumption", consumption);
    }

    /**
     * @param datasource
     * @apiNote 展示 电、水、蒸汽 当天实时负荷，具体数据源在前台配置（基础数据-对象综合展示画面配置模块）
     */
    @MessageMapping("/loadTodayHistoryValue")
    public void loadTodayHistoryValue(JSONObject datasource) {
        String paras = datasource.getString("paras");
        Integer key = datasource.getInteger("key");

        JSONObject jsonObject = JSON.parseObject(paras);
        String pointId = jsonObject.getString("datasource");
        RealTimeVO realTimeVO = comprehensiveShowService.loadTodayHistoryValue(pointId);
        Map<String, Object> mapAll = new HashMap<>();
        mapAll.put("key", key);
        mapAll.put("paras", realTimeVO);
        JSONObject real = (JSONObject) JSONObject.toJSON(mapAll);
        simpMessagingTemplate.convertAndSend("/serverStatus/loadTodayHistoryValue", real);
    }

    /**
     * @param jsonObject
     * @apiNote 综合展示最新值查询
     */
    @MessageMapping("/energyRealTimeValue")
    public void energyRealTimeValue(JSONObject jsonObject) {
        String paras = jsonObject.getString("paras");
        Integer key = jsonObject.getInteger("key");
        JSONObject j = JSON.parseObject(paras);
        List<Map<String, Object>> list = comprehensiveShowService.energyRealTimeValue(j);
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("paras", list);
        JSONObject real = (JSONObject) JSONObject.toJSON(map);
        simpMessagingTemplate.convertAndSend("/serverStatus/energyRealTimeValue", real);
    }
}
