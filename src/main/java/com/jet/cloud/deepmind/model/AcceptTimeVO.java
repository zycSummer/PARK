package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:45
 * @desc
 */
@Data
public class AcceptTimeVO implements Serializable {
    private static final long serialVersionUID = -7057009069518275937L;
    private StartVO start;
    private EndVO end;
}
