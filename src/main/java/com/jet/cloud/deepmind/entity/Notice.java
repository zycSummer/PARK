package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author maohandong
 * @create 2019/12/23 16:12
 */
@Data
@Entity
@Table(name = "tb_obj_notice")
public class Notice extends BaseEntity{
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "notice_title", nullable = false)
    @Size(min = 1, max = 30, message = "公告标题长度不超过30个字符")
    private String noticeTitle;

    @Column(name = "notice_content")
    @Size(min = 1, max = 30, message = "公告内容长度不超过1000个字符")
    private String noticeContent;

}
