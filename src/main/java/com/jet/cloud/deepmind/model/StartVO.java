package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:46
 * @desc StartVO
 */
@Data
public class StartVO implements Serializable {
    private static final long serialVersionUID = 7036708891698736071L;
    private String hour;
    private String min;
}
