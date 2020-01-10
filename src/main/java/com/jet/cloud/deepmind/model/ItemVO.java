package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/25 11:11
 * @desc ItemVO
 */
@Data
public class ItemVO implements Serializable {
    private String name;
    private Double value;
    private Double stdCoalValue;
    private Double stdCoalPpercent;
    private String unit;
}
