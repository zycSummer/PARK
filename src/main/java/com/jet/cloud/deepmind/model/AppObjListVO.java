package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/25 9:04
 * @desc AppObjListVO
 */
@Data
public class AppObjListVO implements Serializable {
    private static final long serialVersionUID = 6877732700984322355L;
    private AppParkVO park;
    private List<AppSiteVO> site;
}
