package com.jet.cloud.deepmind.exception;

import com.jet.cloud.deepmind.entity.Alarm;

/**
 * @author yhy
 * @create 2019-11-15 10:54
 */
public class AlarmNotConfiguredException extends Exception {

    private static final long serialVersionUID = -2559294699630643897L;

    public AlarmNotConfiguredException(Alarm alarm, String msg) {
        super("{" + alarm.getObjType() + "}-{" + alarm.getObjId() + "}-{" + alarm.getAlarmId() + "}-{" + alarm.getAlarmName() + "}:" + msg);
    }
}
