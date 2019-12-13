package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 拼接测点 标煤和非标煤均实用
 *
 * @author yhy
 * @create 2019-11-07 11:21
 */
@Data
public class CombinePointVO implements Serializable {
    private static final long serialVersionUID = -3733460797450687697L;

    private String meterName;
    private String pointId;

    public void setPointId(String tenantId, String projectId, String meterId, String energyLoadParaId) {
        this.pointId = new StringJoiner(".").add(tenantId).add(projectId).add(meterId).add(energyLoadParaId).toString();
    }
}
