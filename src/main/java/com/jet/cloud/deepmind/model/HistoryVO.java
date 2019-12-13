package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/5 15:26
 * @desc
 */
@Data
public class HistoryVO implements Serializable {
    private String energyParaId;
    private String energyParaName;
    private String unit;
    List<NodeVO> nodeVOs;
}
