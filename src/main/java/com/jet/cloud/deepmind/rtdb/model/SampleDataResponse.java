package com.jet.cloud.deepmind.rtdb.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 样本数据返回
 *
 * @author yhy
 * @create 2019-10-29 13:23
 */
@Data
public class SampleDataResponse implements Serializable {
    private static final long serialVersionUID = -3074186169619863064L;

    private String point;
    private Long timestamp;
    private Double value;
    //单侧点（或者公式中全部测点）超时 全部超时 的时候 是 true
    private boolean isAllExpired = false;

    //公式中 部分测点超时  全部不超时 的时候 是 false
    private boolean partExpired = false;

    //公式中 部分测点有值  部分测点有值为 true
    private boolean partValues = false;
    /**
     * SUCCESS,FAIL
     */
    private String opResult = "SUCCESS";
    /**
     * 此测点不存在
     */
    private String msg;

    public SampleDataResponse() {
    }

    public SampleDataResponse(String metricName, Long timestamp, Double value, boolean isAllExpired, boolean partExpired, boolean partValues) {
        this.point = metricName;
        this.partExpired = partExpired;
        this.isAllExpired = isAllExpired;
        this.partValues = partValues;
        if (timestamp == null) {
            this.opResult = "FAIL";
            this.msg = "服务器无数据";
        } else {
            this.timestamp = timestamp;
            this.opResult = "SUCCESS";
            this.value = value;
        }

    }

    public SampleDataResponse(String metricName, Long timestamp, Double value) {
        this.point = metricName;
        if (timestamp == null) {
            this.opResult = "FAIL";
            this.msg = "服务器无数据";
        } else {
            this.timestamp = timestamp;
            this.opResult = "SUCCESS";
            this.value = value;
        }

    }

    public static SampleDataResponse getExpiredInstance(String metricName, Long timestamp) {
        SampleDataResponse t = new SampleDataResponse();
        t.setPoint(metricName);
        t.setTimestamp(timestamp);
        t.setAllExpired(true);
        t.setPartExpired(true);
        t.setMsg("数据已超时");
        return t;
    }

    public SampleDataResponse(String metricName, String opResult, String msg, Double value) {
        this.point = metricName;
        this.opResult = opResult;
        this.msg = msg;
        this.value = value;
    }

    public SampleDataResponse(String para, boolean b) {
        this.point = para;
        if (!b) {
            this.opResult = "FAIL";
            this.msg = "此测点不存在";
        }
    }
}
