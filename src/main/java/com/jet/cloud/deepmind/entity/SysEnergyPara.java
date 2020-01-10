package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhuyicheng
 * @create 2019/10/29 9:31
 * @desc 系统能源参数表实体类
 */

@Data
@Entity
@Table(name = "tb_sys_energy_para")
public class SysEnergyPara extends BaseEntity {
    @Column(name = "energy_type_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String energyTypeId;

    @Column(name = "energy_para_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String energyParaId;

    @Column(name = "energy_para_name", nullable = false)
    @Size(min = 1, max = 20, message = "能源参数名称不能超过20")
    private String energyParaName;

    @Column(name = "unit")
    private String unit;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,10}$", message = "只能输入字母、数字、下划线组合且不能大于10位")
    private String sortId;
}
