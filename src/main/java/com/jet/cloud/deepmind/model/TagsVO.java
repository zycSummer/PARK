package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:41
 * @desc TagsVO
 */
@Data
public class TagsVO implements Serializable {
    private static final long serialVersionUID = 5255302125819916140L;
    private String op;
    private List<String> tags;
}
