package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author yhy
 * @create 2019-10-11 14:56
 */
@Entity
@Data
@Table(name = "tb_sys_role")
public class SysRole extends BaseEntity {

    @Column(name = "role_id", unique = true)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String roleId;


    @Column(name = "role_name", nullable = false)
    @Size(min = 1, max = 30, message = "角色名称不超过30")
    private String roleName;

    public SysRole() {
    }

    public SysRole(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
}
