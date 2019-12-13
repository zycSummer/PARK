package com.jet.cloud.deepmind.model;

import java.util.List;

public class OKResponse<T> extends Response<T> {

    public OKResponse() {
        super(Response.CODE_OK);
    }

    public OKResponse(T one) {
        super(Response.CODE_OK, one);
    }

    public OKResponse(List<T> data) {
        super(Response.CODE_OK, data);
    }

    public OKResponse(List<T> data, Long count) {
        super(Response.CODE_OK, data, count);
    }

    public OKResponse(String msg, T one) {
        super(Response.CODE_OK, msg, one);
    }

    public OKResponse(String msg) {
        super(Response.CODE_OK, msg);
    }


    public OKResponse(String msg, List<T> datas) {
        super(Response.CODE_OK, msg, datas);
    }

    public OKResponse(String msg, List<T> datas, Long count) {
        super(Response.CODE_OK, msg, datas, count);
    }
}
