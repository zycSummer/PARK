package com.jet.cloud.deepmind.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ErrResponse<T> extends Response<T> {

    private Logger logger = LoggerFactory.getLogger(ErrResponse.class);

    public ErrResponse() {
        super(Response.CODE_COMMON_ERROR);
    }

    public ErrResponse(String msg) {
        super(Response.CODE_COMMON_ERROR, msg);
        logger.error(msg);
    }

    public ErrResponse(T data) {
        super(Response.CODE_COMMON_ERROR, data);
    }

    public ErrResponse(List<T> data) {
        super(Response.CODE_COMMON_ERROR, data);
    }

    public ErrResponse(T data, String msg) {
        super(Response.CODE_COMMON_ERROR, msg, data);
    }

    public ErrResponse(List<T> data, String msg) {
        super(Response.CODE_COMMON_ERROR, msg, data);
    }

    public ErrResponse(int code, String msg) {
        super(code, msg);
        logger.error(msg);
    }

    public ErrResponse(int code, String msg, T data) {
        super(code, msg, data);
        logger.error(msg);
    }

    public ErrResponse(int code, String msg, List<T> datas) {
        super(code, msg, datas);
        logger.error(msg);
    }

}
