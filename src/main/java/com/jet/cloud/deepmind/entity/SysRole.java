package com.jet.cloud.deepmind.entity;

import lombok.Data;
import javax.persistence.*;

/**
 * @author yhy
 * @create 2019-10-11 14:56
 */
@Entity
@Data
@Table(name = "tb_sys_role")
public class SysRole extends BaseEntity {

    @Column(name = "role_id", unique = true)
    private String roleId;


    @Column(name = "role_name", nullable = false)
    private String roleName;

    public SysRole() {
    }

    public SysRole(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    //@Column(name = "is_default")
    //private boolean isDefault;

    //@JsonIgnore
    //@OneToMany
    //@JoinColumn(name = "role_code", referencedColumnName = "role_code", insertable = false, updatable = false)
    //@LazyCollection(LazyCollectionOption.FALSE)
    //private Set<MenuMappingRole> menuMappingRoleSet;
}
