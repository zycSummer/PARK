package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    private String energyTypeId;

    @Column(name = "energy_type_name", nullable = false)
    private String energyTypeName;

    @Column(name = "std_coal_coeff")
    private Double stdCoalCoeff;

    @Column(name = "co2_coeff")
    private Double co2Coeff;

    @Column(name = "energy_usage_para_id")
    private String energyUsageParaId;

    @Column(name = "energy_load_para_id")
    private String energyLoadParaId;

    @Column(name = "sort_id")
    private String sortId;
}
