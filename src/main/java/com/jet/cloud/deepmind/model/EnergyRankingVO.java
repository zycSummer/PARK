package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/7 14:55
 * @desc 能耗排名VO
 */
@Data
public class EnergyRankingVO implements Serializable {
    private String energyTypeId;
    private List<String> rankSiteName;
    private List<String> rankSiteAbbrName;
    private List<Double> rankSiteLoad;
}
