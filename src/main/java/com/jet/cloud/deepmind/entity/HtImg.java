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
 * @create 2019/10/25 10:10
 * @desc 对象组态画面实体
 */
@Entity
@Data
@Table(name = "tb_obj_ht_img")
public class HtImg extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "ht_img_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String htImgId;

    @Column(name = "ht_img_name", nullable = false)
    private String htImgName;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,10}$", message = "只能输入字母、数字、下划线组合且不能大于10位")
    private String sortId;

    @Transient
    private String cfgPic;
}
