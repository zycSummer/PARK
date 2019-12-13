package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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
    private String objType;
    @Column(name = "obj_id", nullable = false)
    private String objId;
    @Column(name = "ht_img_id", nullable = false)
    private String htImgId;
    @Column(name = "ht_img_name", nullable = false)
    private String htImgName;
    @Column(name = "parent_id")
    private String parentId;
    @Column(name = "file_path", nullable = false)
    private String filePath;
    @Column(name = "sort_id")
    private String sortId;

    @Transient
    private String cfgPic;
}
