package com.jet.cloud.deepmind.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:39
 * @desc AppQqVO
 */
@Data
public class AppTencentVO implements Serializable {
    private static final long serialVersionUID = -2152549886084744864L;
    private String platform;
    @JSONField(name = "audience_type")
    private String audienceType;
    @JSONField(name = "tag_list")
    private TagsVO tagList;
    private String environment;
    @JSONField(name = "message_type")
    private String messageType;
    private MessageVO message;
}
