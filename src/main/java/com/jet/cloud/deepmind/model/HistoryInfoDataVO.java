package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/10/29 11:00
 * @desc 历史数据对话框VO
 */
@Data
public class HistoryInfoDataVO implements Serializable {
    private String start;// 时间"2019-10-17"
    private String end;// 时间"2019-10-17"
    private Integer interval;// 时间间隔分钟
    private String point;// 测点
}
