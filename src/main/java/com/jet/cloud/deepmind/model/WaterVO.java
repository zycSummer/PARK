package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/25 15:01
 * @desc
 */
@Data
public class WaterVO implements Serializable {
    private static final long serialVersionUID = -831335295861542927L;
    private Double Totalflow;
    private Double Flowrate;
    private Double Temp;

    public WaterVO() {
    }

    public WaterVO(Double totalflow, Double flowrate, Double temp) {
        Totalflow = totalflow;
        Flowrate = flowrate;
        Temp = temp;
    }
}
