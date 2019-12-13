package com.jet.cloud.deepmind.model;

import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/10/24 11:39
 * @desc 导航栏site
 */
@Data
public class NavigationSiteVO {
    private String type;
    private String id;
    private String title;
    private Double longitude;
    private Double latitude;
    private String rtdbProjectId;
}
