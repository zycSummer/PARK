package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/26 14:06
 * @desc
 */
@Data
public class MeterNextMaxLoadValueVO implements Serializable {
    private static final long serialVersionUID = -97824374724616304L;
    private String meterId;
    private String meterName;
    private String energyTypeId;
    private Double maxLoadValue;
}
