package com.jet.cloud.deepmind.rtdb.model;

import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/10/31 16:01
 * @desc
 */
@Data
public class DataPoint {
    private long timestamp;
    private Double value;
}
