package com.jet.cloud.deepmind.common;


import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/25 10:00
 * @desc app接口返回参数类型
 */
@Data
public class AppResult<T> implements Serializable {
    private static final long serialVersionUID = 7315558055080197188L;
    private Integer code;
    private String msg;
    private T data;

    public AppResult() {
    }

    public AppResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
