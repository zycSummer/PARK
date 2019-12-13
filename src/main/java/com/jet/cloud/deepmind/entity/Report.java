package com.jet.cloud.deepmind.entity;

import lombok.Data;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.annotation.MatchesPattern;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author maohandong
 * @create 2019/11/4 11:20
 * @desc 对象报表
 */
@Entity
@Data
@Table(name = "tb_obj_report")
public class Report extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "report_id", nullable = false)
    @NotNull(message = "报表标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "只能输入字母和数字且不能大于20位")
    private String reportId;

    @Column(name = "energy_type_id", nullable = false)
    @NotNull(message = "能源种类标识不能为空")
    private String energyTypeId;

    @Column(name = "report_name", nullable = false)
    @NotNull(message = "报表名称不能为空")
    @Size(min = 1, max = 30, message = "报表名称不超过30")
    private String reportName;

    @Column(name = "is_use", nullable = false)
    @NotNull(message = "是否使用不能为空")
    private String isUse;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String sortId;

    @Transient
    private String showName;

    @Transient
    private String energyTypeName;
}
