package com.jet.cloud.deepmind.controller;


import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.model.EnergyAnalysisVO;
import com.jet.cloud.deepmind.model.HistoryDataVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.EnergyAnalysisService;
import com.jet.cloud.deepmind.service.EnergyMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhuyicheng
 * @create 2019/10/25 9:24
 * @desc 能耗分析
 */
@RestController
@RequestMapping("/energyAnalysis")
public class EnergyAnalysisController {
    @Autowired
    private EnergyAnalysisService energyAnalysisService;
    @Autowired
    private CurrentUser currentUser;

    /**
     * 能耗分析(能耗时比)-页面具体计算逻辑
     */
    @PostMapping("/queryPageInfoTimeData")
    public Response queryPageInfoTimeData(@RequestBody EnergyAnalysisVO energyAnalysisVO) {
        return energyAnalysisService.queryPageInfoData(energyAnalysisVO);
    }
}
