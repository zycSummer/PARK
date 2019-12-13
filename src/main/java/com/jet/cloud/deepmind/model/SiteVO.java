package com.jet.cloud.deepmind.model;

import lombok.Data;

/**
 * @author maohandong
 * @create 2019/11/14 11:23
 */
@Data
public class SiteVO {
    String objType;
    // objId就是siteId
    String objId;
    String name;
    Boolean isRelate;
}
