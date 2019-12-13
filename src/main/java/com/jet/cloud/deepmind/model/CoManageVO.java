package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/5 15:47
 * @desc 碳排
 */
@Data
public class CoManageVO {
    private List<Integer> date;
    private List<Double> value;
}
