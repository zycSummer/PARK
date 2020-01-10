package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhuyicheng
 * @create 2019/10/28 10:01
 * @desc 系统能源种类表实体类
 */
@Data
@Entity
@Table(name = "tb_sys_energy_type")
public class SysEnergyType extends BaseEntity {
    @Column(name = "energy_type_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String energyTypeId;

    @Column(name = "energy_type_name", nullable = false)
    @Size(min = 1, max = 20, message = "能源种类名称不能超过20")
    private String energyTypeName;

    @Column(name = "std_coal_coeff")
    private Double stdCoalCoeff;

    @Column(name = "co2_coeff")
    private Double co2Coeff;

    @Column(name = "energy_usage_para_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String energyUsageParaId;

    @Column(name = "energy_load_para_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String energyLoadParaId;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,10}$", message = "只能输入字母、数字、下划线组合且不能大于10位")
    private String sortId;
}
