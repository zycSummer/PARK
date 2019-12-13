package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/14 11:22
 */
@Data
public class ParkVO {
    String objType;
    // objId就是parkId
    String objId;
    String name;
    Boolean isRelate;
    List<SiteVO> children;
}
