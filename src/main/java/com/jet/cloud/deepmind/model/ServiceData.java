package com.jet.cloud.deepmind.model;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.SysLog;

public class ServiceData<T> {

    private Response<T> response;
    private SysLog log;

    public ServiceData() {
    }

    public ServiceData(Response<T> response, SysLog log) {
        this.response = response;
        this.log = log;
    }

    public static <T> ServiceData success(T data, String operateContent, CurrentUser user) {
        return new ServiceData(data, operateContent, user, true);
    }

    public static <T> ServiceData success(String operateContent, CurrentUser user) {
        return new ServiceData(null, operateContent, user, true);
    }

    public static <T> ServiceData error(T data, String operateContent, CurrentUser user) {
        return new ServiceData(data, operateContent, user, false);
    }

    public static ServiceData error(String operateContent, Exception e, CurrentUser user) {
        return new ServiceData(null, operateContent + ":" + e.getMessage(), user, false);
    }

    public static ServiceData error(String operateContent, CurrentUser user) {
        return new ServiceData(null, operateContent, user, false);
    }

    public ServiceData(T data, String operateContent, CurrentUser user, boolean result) {

        if (result) {
            if (data == null) {
                this.response = Response.ok(operateContent);
            } else {
                this.response = Response.ok(operateContent, data);
            }
        } else {
            if (data == null) {
                this.response = Response.error(operateContent);
            } else {
                this.response = Response.error(data, operateContent);
            }
        }
        this.log = new SysLog(operateContent, user, result);
    }

    public ServiceData(String operateContent, Boolean result) {
        this.response = Response.error(operateContent);
        this.log = new SysLog(operateContent, result);
    }

    public ServiceData(Response<T> response, String operateContent, Boolean result) {
        this.response = response;
        this.log = new SysLog(operateContent, result);
    }

    public ServiceData(Response<T> response, String operateContent, Boolean result, String memo) {
        this.response = response;
        this.log = new SysLog(operateContent, result, memo);
    }

    public Response<T> getResponse() {
        return response;
    }

    public void setResponse(Response<T> response) {
        this.response = response;
    }

    public SysLog getLog() {
        return log;
    }

    public void setLog(SysLog log) {
        this.log = log;
    }
}
