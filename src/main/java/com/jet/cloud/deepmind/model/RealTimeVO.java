package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/11 9:21
 * @desc 展示 各种能源 和 标煤 当天实时负荷
 */
@Data
public class RealTimeVO implements Serializable {
    private String datasource;
    private List<Double> dailyHisValue;
    private List<Long> timeValues;
}
