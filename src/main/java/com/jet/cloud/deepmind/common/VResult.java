package com.jet.cloud.deepmind.common;

import java.util.HashMap;

/**
 * @author zhuyicheng
 * @create 2019/11/25 10:00
 * @desc api接口返回参数类型
 */
public class VResult<T> extends HashMap {
    private String resCode;
    private String resMsg;
    private T result;

    public String getResCode() {
        return resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public T getResult() {
        return result;
    }

    public VResult(String resCode, String resMsg, T result) {
        this.put("resCode", resCode);
        this.put("resMsg", resMsg);
        this.put("result", result);
    }
}
