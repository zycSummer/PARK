package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/3 13:55
 * @desc
 */
@Data
public class AlarmMsgVO implements Serializable {
    private static final long serialVersionUID = 3967495824999094000L;
    private String parkId;
    private String alarmId;
    private String alarmName;
    private String alarmType;
    private String alarmMsg;
    private Long alarmTime;
    private Long recoveryTime;
}
