package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/29 11:00
 * @desc 能耗分析能耗时比VO
 */
@Data
public class EnergyAnalysisVO implements Serializable {
    private String energyTypeId;// 能源种类标识(electricity...)

    private NodeInfoVO nodeInfo;// 数据源+节点id+对象类型+对象标识+展示结构树标识

    private List<String> energyParaIds;// 能源参数标识(Pa,Ic,Ep_imp,Usage...)

    private String timeType;// 时间year,month,day
    private String start;// 时间"2019-10-17"
    private String end;// 时间"2019-10-17"
    private Integer interval;// 时间间隔分钟
    private String type;// 数值类型 first/average/max/min/diff
    private String title;// 标题
}
