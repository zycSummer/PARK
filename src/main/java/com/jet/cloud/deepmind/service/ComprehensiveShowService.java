package com.jet.cloud.deepmind.service;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.BigScreen;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.model.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/10/23 16:06
 * @desc 综合展示
 */
public interface ComprehensiveShowService {
    ServiceData insertSiteImg(BigScreen bigScreen);

    Response querySiteImg(String objType, String objId, String isMainPage);

    Response queryAllSiteImg(String objType, String objId);

    Response queryAllSiteImgByHtImgId(String objType, String objId, String htImgId);

    ServiceData updateSiteImg(BigScreen bigScreen);

    ServiceData deleteSiteImg(String objType, String objId, String htImgId);

    List<Map<String, Object>> energyRealTimeValue(JSONObject datasources);

    EnergyRankingVO energyRealTimeLoadRanking(String energyTypeId, SysUser sysUser);

    List<ConsumptionReturnVO> energyConsumption(List<ConsumptionVO> consumptionVOS);

    RealTimeVO loadTodayHistoryValue(String datasource);

    EnergyRankingVO energyTodayUsageRanking(String energyTypeId, SysUser sysUser);

    List<Map<String, Object>> energyTodayDiffValue(JSONObject j);
}
