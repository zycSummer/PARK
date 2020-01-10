package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/25 9:05
 * @desc AppSiteVO
 */
@Data
public class AppSiteVO implements Serializable {
    private static final long serialVersionUID = -8101401466049557671L;
    private String siteId;
    private String siteName;
}
