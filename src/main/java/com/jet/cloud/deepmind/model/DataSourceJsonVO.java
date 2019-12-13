package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yhy
 * @create 2019-11-08 15:27
 * {
 * "para_id": "Usage",
 * "data_source": "LYGSHCYJD.DGWSCL.M1-1.Ep_imp[electricity]"
 * }
 */
@Data
public class DataSourceJsonVO implements Serializable {
    private static final long serialVersionUID = 8265064427393616480L;

    private String paraId;
    private String dataSource;
}
