package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/8 13:41
 * @desc
 */
@Data
public class ConsumptionReturnVO implements Serializable {
    private String energyTypeId;
    private String datasource;
    private Double thisMonthUsage;
    private Double lastMonthUsage;
    private Double lastMonthThisUsage;
    private Double ratio;
    private Double thisMonthPlan;
    private Double lastMonthPlan;
    private Double stdCoalCoeff;

}
