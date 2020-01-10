package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/25 9:04
 * @desc AppParkVO
 */
@Data
public class AppParkVO implements Serializable {
    private String parkId;
    private String parkName;
}
