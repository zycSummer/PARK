package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/5 17:13
 * @desc
 */
@Data
public class NodeVO implements Serializable {
    private String nodeId;
    private String objType;
    private String objId;
    private String objTreeId;
    private String nodeName;
    private String dataSource;

    private Double average;
    private Double max;
    private String maxTime;
    private Double min;
    private String minTime;
    private String nodeNameAll;

    private List<Double> values;
    private List<Long> times;

    public void setValues(List<Double> values) {
        this.values = values;
    }
}
