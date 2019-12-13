package com.jet.cloud.deepmind.rtdb.model;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-31 10:36
 */
@Data
public class SampleData4KairosResp implements Serializable {
    private static final long serialVersionUID = -9188576605105115095L;

    private String point;

    /**
     * SUCCESS,FAIL
     */
    private String opResult = "SUCCESS";
    /**
     * 此测点不存在
     */
    private String msg;

    private List<Long> timestamps;
    private List<Double> values;

    public SampleData4KairosResp() {
        super();
    }

    public SampleData4KairosResp(String para, boolean b) {
        this.point = para;
        if (!b) {
            this.opResult = "FAIL";
            this.msg = "此测点不存在";
        }
    }

    public SampleData4KairosResp(String point, String opResult, String msg, List<Long> timestamp, List<Double> value) {
        this.point = point;
        this.opResult = opResult;
        this.msg = msg;
        this.timestamps = timestamp;
        this.values = value;
    }

    public SampleData4KairosResp(DataPointResult result) {
        this.point = result.getMetricName();
        this.timestamps = result.getTimestamps();
        this.values = result.getValues();
        this.opResult = result.getOpResult();
        this.msg = result.getMsg();
    }
}
