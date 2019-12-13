package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/26 10:44
 * @desc
 */
@Data
public class MeterUsageValueVO implements Serializable {
    private static final long serialVersionUID = 4728992932982917076L;
    private String meterId;
    private String meterName;
    private String energyTypeId;
    private List<Double> usageHourly;
}
