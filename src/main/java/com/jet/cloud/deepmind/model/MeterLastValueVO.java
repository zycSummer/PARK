package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/25 13:54
 * @desc
 */
@Data
public class MeterLastValueVO implements Serializable {
    private static final long serialVersionUID = -3106602938388718322L;
    private String meterId;
    private String meterName;
    private String energyTypeId;
    private Long meterLastTime;
    private Object meterLastValue;
}
