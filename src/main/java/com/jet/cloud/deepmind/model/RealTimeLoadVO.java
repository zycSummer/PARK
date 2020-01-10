package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

/**
 * @author yhy
 * @create 2019-11-06 10:15
 */
@Data
public class RealTimeLoadVO implements Serializable {
    private static final long serialVersionUID = 3546555454500383734L;

    private String name;
    private List<Long> timestamps;
    private List<Double> values;
    private String avg;
    private String maxVal;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime maxTime;
    private String minVal;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime minTime;
    private String energyParaName;
    private String unit;

    public void setEnergyPara(SysEnergyPara energyPara) {
        this.energyParaName = energyPara.getEnergyParaName();
        this.unit = energyPara.getUnit();
    }
}
