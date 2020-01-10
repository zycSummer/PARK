package com.jet.cloud.deepmind.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author maohandong
 * @create 2019/12/26 10:26
 */
@Data
public class AppNoticeListVO implements Serializable {
    private static final long serialVersionUID = 6617980763811519323L;
    private String noticeTitle;
    private Long noticeTime;
}
