package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:21
 * @desc 对象设备系统信息表
 */
@Data
@Entity
@Table(name = "tb_obj_equip_sys")
public class EquipSys extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "equip_sys_id", nullable = false)
    @NotNull(message = "设备系统标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String equipSysId;

    @Column(name = "equip_sys_name", nullable = false)
    @NotNull(message = "设备系统名称不能为空")
    @Size(min = 1, max = 30, message = "设备系统名称不能超过30")
    private String equipSysName;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,10}$", message = "只能输入字母、数字、下划线组合且不能大于10位")
    private String sortId;
}
