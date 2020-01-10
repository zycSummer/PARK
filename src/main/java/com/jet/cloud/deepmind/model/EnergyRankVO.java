package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/25 14:51
 * @desc EnergyRankVO
 */
@Data
public class EnergyRankVO implements Serializable {
    private static final long serialVersionUID = 8714096369758618325L;
    private String unit;
    private List<RankVO> rank;
}
