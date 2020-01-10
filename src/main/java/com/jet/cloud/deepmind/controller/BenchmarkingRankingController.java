package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.BenchmarkingRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maohandong
 * @create 2019/12/11 11:27
 * @desc 指标排名
 */
@RestController
@RequestMapping("/benchmarkingRanking")
public class BenchmarkingRankingController {

    @Autowired
    private BenchmarkingRankingService benchmarkingRankingService;

    @PostMapping("/queryObj")
    public Response queryObj(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String benchmarkingType = data.getString("benchmarkingType");
        Long timestamp = data.getLong("timestamp");
        String timeUnit = data.getString("timeUnit");
        return benchmarkingRankingService.queryObj(objType, objId, benchmarkingType, timestamp, timeUnit);
    }
}
