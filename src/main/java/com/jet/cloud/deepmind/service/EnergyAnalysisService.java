package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.EnergyAnalysisVO;
import com.jet.cloud.deepmind.model.Response;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhuyicheng
 * @create 2019/10/30 10:38
 * @desc 能耗分析service
 */
public interface EnergyAnalysisService {
    Response queryPageInfoData(EnergyAnalysisVO energyAnalysisVO);
}
