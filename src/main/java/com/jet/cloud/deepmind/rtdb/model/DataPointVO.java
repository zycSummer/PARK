package com.jet.cloud.deepmind.rtdb.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/31 15:54
 * @desc 实时库返还值封装类
 */
@Data
public class DataPointVO implements Serializable {
    private static final long serialVersionUID = 5289504017898204605L;
    private List<Long> timestamps;
    private List<DataPointResult> values;

}
