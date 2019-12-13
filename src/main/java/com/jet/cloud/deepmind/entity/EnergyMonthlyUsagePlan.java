package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * @author zhuyicheng
 * @create 2019/11/8 16:23
 * @desc 对象能源月使用计划表
 */
@Entity
@Data
@Table(name = "tb_obj_energy_monthly_usage_plan")
public class EnergyMonthlyUsagePlan extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "energy_type_id", nullable = false)
    @NotNull(message = "能源种类标识不能为空")
    private String energyTypeId;

    @Column(name = "`year`", nullable = false)
    @NotNull(message = "年不能为空")
    private Integer year;

    @Column(name = "`month`", nullable = false)
    @NotNull(message = "月不能为空")
    private Integer month;

    @Column(name = "`usage`", nullable = false)
    @NotNull(message = "用量不能为空")
    private Double usage;

    @Transient
    private String energyTypeName;

}
