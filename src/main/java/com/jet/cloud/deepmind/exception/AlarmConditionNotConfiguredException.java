package com.jet.cloud.deepmind.exception;

import com.jet.cloud.deepmind.entity.AlarmCondition;

/**
 * 报警条件 未配置异常
 *
 * @author yhy
 * @create 2019-11-14 14:52
 */
public class AlarmConditionNotConfiguredException extends Exception {
    private static final long serialVersionUID = 3833909850701656144L;

    public AlarmConditionNotConfiguredException(AlarmCondition alarmCondition, String msg) {
        super("{" + alarmCondition.getObjType() + "}-{" + alarmCondition.getObjId() + "}-{" + alarmCondition.getAlarmId() + "}-{" + alarmCondition.getAlarmId() + "}:" + msg);
    }
}
