package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.Response;

/**
 * Class EnergyBalanceService
 *
 * @package
 */
public interface EnergyBalanceService {
    Response getObjectClassType(String objType, String objId, String energyTypeId);

    Response getTreeData(String objType, String objId, String objTreeId, String energyTypeId, Long timestamp, String timeUnit);
}
