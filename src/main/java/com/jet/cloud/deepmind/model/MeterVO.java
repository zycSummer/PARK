package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/28 15:07
 * @desc
 */
@Data
public class MeterVO implements Serializable {

    private static final long serialVersionUID = -6179593574821019193L;
    private String meterId;
    private String meterName;
    private String energyTypeId;
    private String memo;

}
