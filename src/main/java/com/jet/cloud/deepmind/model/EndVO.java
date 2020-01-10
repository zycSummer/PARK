package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:46
 * @desc EndVO
 */
@Data
public class EndVO implements Serializable {
    private static final long serialVersionUID = -6107787044979147207L;
    private String hour;
    private String min;
}
