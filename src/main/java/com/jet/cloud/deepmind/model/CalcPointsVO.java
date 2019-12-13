package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/6 14:52
 * @desc
 */
@Data
public class CalcPointsVO implements Serializable {
    private String name;
    private List<Long> timestamps;
    private List<Double> values;
    private String avg;
    private String maxVal;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime maxTime;
    private String minVal;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime minTime;
}
