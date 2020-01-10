package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/25 14:51
 * @desc AppEnergyAnalysisVO
 */
@Data
public class AppEnergyAnalysisVO implements Serializable {
    private static final long serialVersionUID = -192712235606547606L;
    private String unit;
    private List<Double> thisTotal;
    private Double lastTotal;
    private List<Double> lastDetail;
    private Double samePeriodRatio;
}
