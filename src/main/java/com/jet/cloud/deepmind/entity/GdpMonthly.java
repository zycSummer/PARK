package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 对象月GDP表
 *
 * @author yhy
 * @create 2019-11-05 16:33
 */
@Data
@Entity
@Table(name = "tb_obj_gdp_monthly")
public class GdpMonthly extends BaseEntity {

    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "`year`", nullable = false)
    @NotNull(message = "年不能为空")
    private Integer year;

    @Column(name = "`month`", nullable = false)
    @NotNull(message = "月不能为空")
    private Integer month;

    @Column(name = "gdp", nullable = false)
    @NotNull(message = "用量不能为空")
    private Double gdp;

    @Column(name = "add_value", nullable = false)
    @NotNull(message = "工业增加值不能为空")
    private Double addValue;

}
