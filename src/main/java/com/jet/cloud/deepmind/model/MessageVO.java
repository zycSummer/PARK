package com.jet.cloud.deepmind.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:43
 * @desc MessageVO
 */
@Data
public class MessageVO implements Serializable {
    private String title;
    private String content;
    @JSONField(name = "accept_time")
    private List<AcceptTimeVO> acceptTime;
    @JSONField(name = "custom_content")
    private JSONObject customContent;
}
