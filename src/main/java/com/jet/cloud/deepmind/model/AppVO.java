package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/24 15:02
 * @desc
 */
@Data
public class AppVO implements Serializable {

    private static final long serialVersionUID = -1654841426937363961L;
    private String userId;
    private String userKey;
    private String token;
    private Long time;

}
