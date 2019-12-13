package com.jet.cloud.deepmind.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.jet.cloud.deepmind.rtdb.model.QueryBody;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T> {

    public static final int CODE_OK = 0;

    public static final int CODE_COMMON_ERROR = 1;

    @JsonIgnore
    protected String queryPara;
    protected int code;
    protected String msg;
    protected T one;
    protected List<T> data;

    protected Long count;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(int code) {
        this.code = code;
    }

    public Response(int code, T data) {
        this.code = code;
        this.one = data;
    }

    public Response(int code, List<T> data) {
        this.code = code;
        this.data = data;
    }

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.one = data;
    }

    public Response(int code, String msg, List<T> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Response(int code, List<T> data, Long count) {
        this.code = code;
        this.data = data;
        this.count = count;
    }

    public Response(int code, String msg, List<T> data, Long count) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.count = count;
    }

    public Response(List<T> data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public Response(T data, String msg) {
        this.one = data;
        this.msg = msg;
    }

    public static Response ok() {
        return new OKResponse();
    }

    public static <T> Response ok(T one) {
        return new OKResponse(null, one);
    }

    public static Response ok(String msg) {
        return new OKResponse(msg);
    }

    public static <T> Response ok(List<T> data) {
        return new OKResponse(data);
    }

    public static <T> Response ok(List<T> data, Long count) {
        return new OKResponse(data, count);
    }

    public static <T> Response ok(String msg, T data) {
        return new OKResponse(msg, data);
    }

    public static <T> Response ok(String msg, List<T> dataList) {
        return new OKResponse(msg, dataList);
    }

    public static <T> Response ok(String msg, List<T> dataList, Long count) {
        return new OKResponse(msg, dataList, count);
    }

    public static Response error() {
        return new ErrResponse();
    }

    public static Response error(String msg) {
        return new ErrResponse(msg);
    }

    public static Response error(int code, String msg) {
        return new ErrResponse(code, msg);
    }

    public static <T> Response error(T data) {
        return new ErrResponse(data);
    }

    public static <T> Response error(T data, String msg) {
        return new ErrResponse(data, msg);
    }

    public static Response error(String msg, Exception e) {
        return new ErrResponse(msg + ":" + e.getMessage());
    }


    public static <T> Response error(List<T> dataList) {
        return new ErrResponse(dataList);
    }

    public static <T> Response error(int code, String msg, T data) {
        return new ErrResponse(code, msg, data);
    }

    public static <T> Response error(int code, String msg, List<T> dataList) {
        return new ErrResponse(code, msg, dataList);
    }

    public void setQueryPara(Object... para) {
        if (para.length == 0) return;
        this.queryPara = JSON.toJSONString(para);
    }

}
