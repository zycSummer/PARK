package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/25 14:53
 * @desc
 */
@Data
public class RankVO implements Serializable {
    private static final long serialVersionUID = -7911894113459832940L;
    private String name;
    private Double value;
}
