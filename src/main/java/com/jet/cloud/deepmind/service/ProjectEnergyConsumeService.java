package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.Response;

/**
 * Class ProjectEnergyConsumeService
 *
 * @package
 */
public interface ProjectEnergyConsumeService {
    Response realTimeLoadResp(String objType, String objId, String energyTypeId);

    Response calendar(String objType, String objId, Long timestamp);

    Response rank(String objType, String objId, String energyTypeId, Long timestamp);

    Response usageInfoPie(String objType, String objId,String timeUnit);

    Response usageInfoCompare(String objType, String objId, String energyTypeId, Long timestamp);
}
