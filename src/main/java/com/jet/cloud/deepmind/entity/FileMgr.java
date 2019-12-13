package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author maohandong
 * @create 2019/10/30 10:40
 */
@Data
@Entity
@Table(name = "tb_obj_file_mgr")
public class FileMgr extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    private String objType;
    @Column(name = "obj_id", nullable = false)
    private String objId;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_desc", nullable = false)
    private String fileDesc;

}
