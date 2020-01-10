package com.jet.cloud.deepmind.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/27 13:29
 * @desc AppResultVO
 */
@Data
public class AppResultVO implements Serializable {
    private static final long serialVersionUID = 830792964830856982L;
    private Integer seq;
    @JSONField(name = "push_id")
    private String pushId;
    @JSONField(name = "ret_code")
    private Integer retCode;
    private String environment;
    @JSONField(name = "err_msg")
    private String errMsg;
    private String result;
}
