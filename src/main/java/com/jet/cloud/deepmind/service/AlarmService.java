package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * @author zhuyicheng
 * @create 2019/11/11 14:11
 * @desc 报警管理service
 */
public interface AlarmService {
    Response queryLeftAlarmInfos(QueryVO vo);

    ServiceData insertOrUpdateAlarm(Alarm alarm);

    Response queryLeftAlarmById(Integer id);

    ServiceData delete(String objType, String objId, String alarmId);
}
