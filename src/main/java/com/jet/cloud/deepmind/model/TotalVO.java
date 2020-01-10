package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/25 11:11
 * @desc TotalVO
 */
@Data
public class TotalVO implements Serializable {
    private String name;
    private Double value;
    private String unit;
}
