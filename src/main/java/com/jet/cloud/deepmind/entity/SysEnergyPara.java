package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    private String energyTypeId;

    @Column(name = "energy_para_id", nullable = false)
    private String energyParaId;

    @Column(name = "energy_para_name", nullable = false)
    private String energyParaName;

    @Column(name = "unit")
    private String unit;

    @Column(name = "sort_id")
    private String sortId;
}
