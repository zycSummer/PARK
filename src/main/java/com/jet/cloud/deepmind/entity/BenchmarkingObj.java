package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author maohandong
 * @create 2019/12/11 9:31
 * @desc 对标对象
 */
@Data
@Entity
@Table(name = "tb_obj_benchmarking_obj")
public class BenchmarkingObj extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "benchmarking_obj_id", nullable = false)
    @NotNull(message = "对标对象标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String benchmarkingObjId;

    @Column(name = "benchmarking_obj_name", nullable = false)
    @NotNull(message = "对标对象名称不能为空")
    @Size(min = 1, max = 30, message = "对标对象名称不超过30")
    private String benchmarkingObjName;

    @Column(name = "benchmarking_obj_abbr_name", nullable = false)
    @Size(min = 1, max = 15, message = "对标对象简称长度不超过15")
    @NotNull(message = "对标对象简称不能为空")
    private String benchmarkingObjAbbrName;

    @Column(name = "benchmarking_obj_type", nullable = false)
    @NotNull(message = "对标对象类型不能为空")
    private String benchmarkingObjType;

    @Column(name = "world_longitude")
    private Double worldLongitude;

    @Column(name = "world_latitude")
    private Double worldLatitude;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,10}$", message = "只能输入字母、数字、下划线组合且不能大于10位")
    private String sortId;

    @Transient
    private Double data;
}
