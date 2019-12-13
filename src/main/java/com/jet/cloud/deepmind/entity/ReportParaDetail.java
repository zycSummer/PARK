package com.jet.cloud.deepmind.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author maohandong
 * @create 2019/11/6 14:58
 * @desc 对象报表参数明细
 */
@Entity
@Data
@Table(name = "tb_obj_report_para_detail")
public class ReportParaDetail extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "report_id", nullable = false)
    @NotNull(message = "报表标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String reportId;

    @Column(name = "energy_para_id", nullable = false)
    @NotNull(message = "能源参数标识不能为空")
    private String energyParaId;

    @Column(name = "display_name", nullable = false)
    @NotNull(message = "展示名称不能为空")
    @Size(min = 1, max = 20, message = "展示名称不超过20")
    private String displayName;

    @Column(name = "time_value", nullable = false)
    @NotNull(message = "时刻值不能为空")
    private String timeValue;

    @Column(name = "max_value", nullable = false)
    @NotNull(message = "最大值不能为空")
    private String maxValue;

    @Column(name = "min_value", nullable = false)
    @NotNull(message = "最小值不能为空")
    private String minValue;

    @Column(name = "avg_value", nullable = false)
    @NotNull(message = "平均值不能为空")
    private String avgValue;

    @Column(name = "diff_value", nullable = false)
    @NotNull(message = "差值不能为空")
    private String diffValue;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String sortId;

    @Transient
    private String energyParaName;
}
