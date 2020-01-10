package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author zhuyicheng
 * @create 2019/10/23 15:41
 * @desc 大屏实体
 */
@Entity
@Data
@Table(name = "tb_obj_big_screen")
public class BigScreen extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "ht_img_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String htImgId;

    @Column(name = "is_mainpage", nullable = false)
    private String isMainPage;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Transient
    private String cfgPic;
    @Transient
    private String icon;
}
