package com.jet.cloud.deepmind.rtdb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jet.cloud.deepmind.rtdb.model.Constants.*;

/**
 * @author yhy
 * @create 2019-10-29 13:23
 */
@Data
public class DataPointResult implements Serializable {
    private static final long serialVersionUID = -3074186169619863064L;

    private String metricName;
    @JsonIgnore
    private List<Long> timestamps;
    @JsonIgnore
    private Long timestamp;
    @JsonIgnore
    private Double value;

    /**
     * SUCCESS,FAIL
     */
    private String opResult = "SUCCESS";
    /**
     * 此测点不存在
     */
    private String msg;

    private List<Double> values;

    public DataPointResult() {
    }

    public DataPointResult(String metricName, Long timestamp, Double value) {
        this.metricName = metricName;
        if (timestamp == null) {
            this.opResult = "FAIL";
            this.msg = "服务器无数据";
        } else {
            this.timestamp = timestamp;
            this.opResult = "SUCCESS";
            this.value = value;
        }

    }

    public DataPointResult(String metricName, List<Double> values) {
        this.metricName = metricName;
        this.values = values;
    }

    public DataPointResult(String para, boolean b) {
        this.metricName = para;
        if (!b) {
            this.opResult = "FAIL";
            this.msg = "此测点不存在";
        }
    }

    public DataPointResult(String para) {
        this.metricName = para;
    }

    public void setMetricName(Map<String, List<String>> tags, String name) {

        List<String> tenant = tags.get(TENANT);
        List<String> project = tags.get(PROJECT);
        List<String> meter = tags.get(METER);
        if (tenant != null && project != null && meter != null && tenant.size() == 1 && project.size() == 1 && meter.size() == 1) {
            this.metricName = tenant.get(0) + "." +
                    project.get(0) + "." +
                    meter.get(0) + "." + name;
        } else {
            this.metricName = name;
        }
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        if (dataPoints == null || dataPoints.size() == 0) return;

        for (DataPoint point : dataPoints) {

            if (this.timestamps == null) {
                this.timestamps = new ArrayList<>();
                this.values = new ArrayList<>();
            }
            this.timestamps.add(point.getTimestamp());
            this.values.add(point.getValue());
        }
    }
}
