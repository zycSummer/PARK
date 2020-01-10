package com.jet.cloud.deepmind.model;

import lombok.Data;

/**
 * @author maohandong
 * @create 2019/12/11 15:25
 */
@Data
public class BenchmarkingVO {
    private String name;
    private String abbrName;
    private String type;
    private Double data;
    private Integer ranking;
}
