package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/24 11:39
 * @desc 导航栏Park
 */
@Data
public class NavigationParkVO {
    private String name;
    private String type;
    private String id;
    private String title;
    private Double longitude;
    private Double latitude;
    private Integer scale;
    private boolean spread;
    private String rtdbProjectId;
    List<NavigationSiteVO> children;
}
