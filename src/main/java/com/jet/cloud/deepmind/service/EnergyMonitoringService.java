package com.jet.cloud.deepmind.service;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.entity.HtImg;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.model.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/10/25 9:50
 * @desc 用能监测service
 */
public interface EnergyMonitoringService {
    Response queryLeftHtImg(String objType, String objId);

    Response queryRightHtImg(String objType, String objId, String htImgId);

    ServiceData insertRightHtImg(HtImg htImg);

    ServiceData updateRightHtImg(HtImg htImg);

    ServiceData deleteRightHtImg(String objType, String objId, String htImgId);

    Map<String, Object> queryActualMonitorData(List<Map<String, Object>> opNameList, String objId, String objType);

    Response queryHistoryLeftData();

    Response queryHistoryLeftTree(String objType, String objId, String energyTypeId);

    Response queryParameter(String energyTypeId);

    Response queryPageInfoData(HistoryDataVO historyDataVO);

    Response queryHistoryInfoData(HistoryInfoDataVO historyInfoDataVO);
}
