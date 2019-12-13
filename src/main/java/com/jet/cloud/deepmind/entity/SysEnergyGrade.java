package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 能耗强度等级
 *
 * @author yhy
 * @create 2019-11-06 13:49
 */
@Data
@Entity
@Table(name = "tb_sys_energy_grade")
public class SysEnergyGrade extends BaseEntity {

    private static final long serialVersionUID = -7052599626230484199L;
    @Column(name = "energy_grade_id", nullable = false)
    private String energyGradeId;

    @Column(name = "lower", nullable = false)
    private String lower;

    @Column(name = "upper", nullable = false)
    private String upper;

    @Column(name = "color", nullable = false)
    private String color;

}
