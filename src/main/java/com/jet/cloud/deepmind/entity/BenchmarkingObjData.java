package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author maohandong
 * @create 2019/12/11 9:32
 * @desc 对标对象指标数据
 */
@Data
@Entity
@Table(name = "tb_obj_benchmarking_obj_data")
public class BenchmarkingObjData extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "benchmarking_obj_id", nullable = false)
    @NotNull(message = "对标对象标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "只能输入字母和数字且不能大于20位")
    private String benchmarkingObjId;

    @Column(name = "`year`", nullable = false)
    @NotNull(message = "年不能为空")
    private Integer year;

    @Column(name = "gdp_electricity")
//    @Pattern(regexp = "^[0-9]*$", message = "只能输入数字")
    private Double gdpElectricity;

    @Column(name = "gdp_water")
//    @Pattern(regexp = "^[0-9]*$", message = "只能输入数字")
    private Double gdpWater;

    @Column(name = "gdp_std_coal")
//    @Pattern(regexp = "^[0-9]*$", message = "只能输入数字")
    private Double gdpStdCoal;

    @Column(name = "add_value_std_coal")
//    @Pattern(regexp = "^[0-9]*$", message = "只能输入数字")
    private Double addValueStdCoal;
}
