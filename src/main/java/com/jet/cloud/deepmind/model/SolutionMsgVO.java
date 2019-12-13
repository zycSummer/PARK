package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/3 13:55
 * @desc
 */
@Data
public class SolutionMsgVO implements Serializable {
    private static final long serialVersionUID = 772460106252059184L;
    private String parkId;
    private String diagnosisId;
    private String diagnosisDesc;
    private Long diagnosisTime;
    private String problemDesc;
    private String solution;
}
