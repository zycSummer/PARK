package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author maohandong
 * @create 2019/12/26 10:03
 */
@Data
public class AppNoticeVO implements Serializable {
    private static final long serialVersionUID = -8263603000405960843L;
    private String noticeTitle;
    private String noticeContent;
    private Long noticeTime;
}
