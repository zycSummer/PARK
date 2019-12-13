package com.jet.cloud.deepmind.model;

import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/10/24 18:01
 * @desc ComprehensiveShowVO
 */
@Data
public class ComprehensiveShowVO {
    private String objType;

    private String objId;

    private String objName;

    private Double longitude;

    private Double latitude;

    private Integer scale;

    private String parkName;

    private String siteName;

    private String rtdbProjectId;

    private String rtdbTenantId;

    private String addr;

}
