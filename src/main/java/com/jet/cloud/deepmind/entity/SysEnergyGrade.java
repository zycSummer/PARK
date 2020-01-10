package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

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
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String energyGradeId;

    @Column(name = "lower", nullable = false)
    private String lower;

    @Column(name = "upper", nullable = false)
    private String upper;

    @Column(name = "color", nullable = false)
    private String color;

}
