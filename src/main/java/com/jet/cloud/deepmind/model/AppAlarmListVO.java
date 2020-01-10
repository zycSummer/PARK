package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/26 16:31
 * @desc AppAlarmListVO
 */
@Data
public class AppAlarmListVO implements Serializable {
    private static final long serialVersionUID = 7717998402372094474L;
    private String alarmId;
    private String alarmName;
    private Long alarmTime;
}
