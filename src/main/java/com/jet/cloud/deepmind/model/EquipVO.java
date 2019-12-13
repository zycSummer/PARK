package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/11 16:19
 * @desc
 */
@Data
public class EquipVO implements Serializable {
    private static final long serialVersionUID = 9071255284504823604L;
    private String objType;
    private String objId;
    private String equipId;
}
