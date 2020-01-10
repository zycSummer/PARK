package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/25 11:09
 * @desc AppEnergySummaryVO
 */
@Data
public class AppEnergySummaryVO implements Serializable {
    private static final long serialVersionUID = 7253996889519003696L;
    private TotalVO totalVO;
    private List<ItemVO> item;
}
