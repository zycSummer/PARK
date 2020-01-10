package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.ComprehensiveSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maohandong
 * @create 2019/12/27 14:33
 * @desc 综合概述
 */
@RestController
@RequestMapping("/comprehensiveSummary")
public class ComprehensiveSummaryController {
    @Autowired
    private ComprehensiveSummaryService comprehensiveSummaryService;

    /**
     * 查询当前所选对象、国际和国内的gdp能耗信息
     * @param data
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String benchmarkingType = data.getString("benchmarkingType");
        return comprehensiveSummaryService.query(objType, objId, benchmarkingType);
    }

    /**
     * 获取位置信息
     * @param data
     * @return
     */
    @PostMapping("/queryPosition")
    public Response queryPosition(@RequestBody JSONObject data){
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        return comprehensiveSummaryService.queryPosition(objType, objId);
    }

    /**
     * 获取总的能耗信息
     * @param data
     * @return
     */
    @PostMapping("/queryAllEnergy")
    public Response queryAllEnergy(@RequestBody JSONObject data){
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        return comprehensiveSummaryService.queryAllEnergy(objType, objId);
    }

    /**
     * 基础简介信息
     * @param data
     * @return
     */
    @PostMapping("/queryInfo")
    public Response queryInfo(@RequestBody JSONObject data){
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        return comprehensiveSummaryService.queryInfo(objType, objId);
    }
}
