package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author maohandong
 * @create 2019/12/26 13:45
 */
@Data
public class AppAlarmMsgVO implements Serializable{
    private static final long serialVersionUID = -4721897873404421355L;
    private String alarmId;
    private String alarmName;
    private String alarmType;
    private String alarmMsg;
    private Long alarmTime;
}
