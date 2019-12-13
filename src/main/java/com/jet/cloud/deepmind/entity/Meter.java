package com.jet.cloud.deepmind.entity;

import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 对象仪表信息表
 *
 * @author yhy
 * @create 2019-11-07 10:52
 */
@Data
@Table(name = "tb_obj_meter")
@Entity
public class Meter extends BaseEntity {

    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "meter_id", nullable = false)
    @NotNull(message = "仪表标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]([-_A-Za-z0-9]{0,20})$", message = "以字母、数字开头，字母、数字、中横线、下划线组合，且长度不能大于20位")
    private String meterId;

    @Column(name = "meter_name", nullable = false)
    @NotNull(message = "仪表名称不能为空")
    @Size(min = 1, max = 30, message = "仪表名称不超过30")
    private String meterName;

    @Column(name = "energy_type_id")
    @NotNull(message = "能源种类标识不能为空")
    private String energyTypeId;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String sortId;

    @Column(name = "is_ranking")
    @NotNull(message = "是否参与负荷排名不能为空")
    @Convert(converter = String2BooleanConverter.class)
    private Boolean isRanking;

    @OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "energy_type_id", referencedColumnName = "energy_type_id", insertable = false, updatable = false)
    private SysEnergyType sysEnergyType;

    @Transient
    private String energyTypeName;

    public SysEnergyType getSysEnergyType() {
        if (sysEnergyType != null) {
            energyTypeName = sysEnergyType.getEnergyTypeName();
        }
        return null;
    }
}
