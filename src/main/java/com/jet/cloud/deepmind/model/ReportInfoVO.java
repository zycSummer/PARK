package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/18 13:31
 * @desc 报表查询内容
 */
@Data
public class ReportInfoVO implements Serializable {
    private static final long serialVersionUID = 5568543705841541717L;
    private List<Double> first;
    private List<Double> max;
    private List<Double> min;
    private List<Double> avg;
    private List<Double> diff;
    private String pointId;
    private String energyParaId;

}
