package com.jet.cloud.deepmind.rtdb.model;

import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/10/31 15:54
 * @desc 实时库返还值封装类
 */
@Data
public class BasicPointValue {
    private String pointId;
    private Integer resultCode;
    private Long resultTime;
    private Double resultValue;
}
