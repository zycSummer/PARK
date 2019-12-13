package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.AlarmCondition;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * Class AlarmConditionService
 *
 * @package
 */
public interface AlarmConditionService {
    ServiceData add(AlarmCondition alarmCondition);

    ServiceData update(AlarmCondition condition);

    Response getByIndex(String objType, String objId, String alarmId, String conditionId);

    ServiceData delete(String objType, String objId, String alarmId, String conditionId);

    Response query(QueryVO vo);
}
