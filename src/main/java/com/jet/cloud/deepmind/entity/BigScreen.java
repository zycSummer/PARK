package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

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
    private String objType;
    @Column(name = "obj_id", nullable = false)
    private String objId;
    @Column(name = "ht_img_id", nullable = false)
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
