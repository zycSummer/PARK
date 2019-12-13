package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/18 10:48
 * @desc 报表查询表头VO
 */
@Data
public class ReportTableVO implements Serializable {

    private static final long serialVersionUID = -4102811837830303899L;
    private String energyParaId;
    private String displayName;
    private String timeValue;
    private String maxValue;
    private String minValue;
    private String avgValue;
    private String diffValue;


    public ReportTableVO() {
    }

    public ReportTableVO(String energyParaId, String displayName, String timeValue, String maxValue, String minValue, String avgValue, String diffValue) {
        this.energyParaId = energyParaId;
        this.displayName = displayName;
        this.timeValue = timeValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.avgValue = avgValue;
        this.diffValue = diffValue;
    }
}
