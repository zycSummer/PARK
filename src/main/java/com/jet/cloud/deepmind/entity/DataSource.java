package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author maohandong
 * @create 2019/10/29 10:31
 */
@Entity
@Data
@Table(name = "tb_obj_data_source")
public class DataSource extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    private String objType;
    @Column(name = "obj_id", nullable = false)
    private String objId;
    @Column(name = "energy_type_id", nullable = false)
    private String energyTypeId;
    @Column(name = "energy_para_id", nullable = false)
    private String energyParaId;
    @Column(name = "data_source")
    private String dataSource;

    @Transient
    private Double lastVal;
    @Transient
    private String energyTypeName;
    @Transient
    private String energyParaName;
}
