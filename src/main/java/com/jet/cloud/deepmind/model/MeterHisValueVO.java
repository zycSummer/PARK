package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/25 13:54
 * @desc
 */
@Data
public class MeterHisValueVO implements Serializable {
    private static final long serialVersionUID = 8673686104729513033L;
    private String meterId;
    private String meterName;
    private String energyTypeId;
    private String paraId;
    private List<Long> hisTime;
    private List<Double> hisData;
}
