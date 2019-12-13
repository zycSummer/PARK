package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author zhuyicheng
 * @create 2019/10/23 14:56
 * @desc
 */
@Entity
@Data
@Table(name = "tb_sys_parameter")
public class SysParameter extends BaseEntity {
    @Column(name = "para_id", unique = true)
    private String paraId;
    @Column(name = "para_value", nullable = false)
    private String paraValue;
    @Column(name = "para_desc", nullable = false)
    private String paraDesc;
}
