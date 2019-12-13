package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/11/25 15:01
 * @desc
 */
@Data
public class StreamVO implements Serializable {
    private Double Temp;
    private Double Press;
    private Double Totalflow_std;
    private Double Flowrate_std;
    private Double Totalflow_work;
    private Double Flowrate_work;

    public StreamVO() {
    }

    public StreamVO(Double temp, Double press, Double totalflow_std, Double flowrate_std, Double totalflow_work, Double flowrate_work) {
        Temp = temp;
        Press = press;
        Totalflow_std = totalflow_std;
        Flowrate_std = flowrate_std;
        Totalflow_work = totalflow_work;
        Flowrate_work = flowrate_work;
    }
}
