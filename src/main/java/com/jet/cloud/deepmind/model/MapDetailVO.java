package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yhy
 * @create 2019-11-05 10:56
 */
@Data
public class MapDetailVO implements Serializable {
    private static final long serialVersionUID = 199041542443236059L;

    private String electric;

    private String water;

    private String steam;

    //煤炭
    private String stdCoal;

    //上年同期
    private String lastYearCompareRate;

    private String icon;

    private String gdp;

    private String addr;


}
