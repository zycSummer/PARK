package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/8 13:38
 * @desc 用量VO
 */
@Data
public class ConsumptionVO implements Serializable {
    private String energyTypeId;
    private String datasource;
    private String objType;
    private String objId;
}
