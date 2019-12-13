package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.util.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yhy
 * @create 2019-11-20 13:22
 */
@Data
public class ButtonVO implements Serializable {
    private static final long serialVersionUID = -2112499968856748134L;

    private String menuId;

    private String functionId;

    private String functionName;

    private boolean visible = false;

    @JsonIgnore
    public String getToken() {
        if (StringUtils.isNullOrEmpty(menuId, functionId)) return "";
        return menuId + functionId;
    }
}
