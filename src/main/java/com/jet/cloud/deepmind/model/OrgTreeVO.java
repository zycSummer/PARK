package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/28 15:01
 * @desc OrgTreeVO
 */
@Data
public class OrgTreeVO implements Serializable {
    private static final long serialVersionUID = 2519570669724633594L;
    private String orgTreeId;
    private String orgTreeName;
    private String energyTypeId;
    private List<OrgTreeDetailVO> children;
}
