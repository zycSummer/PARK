package com.jet.cloud.deepmind.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * @author yhy
 * @create 2019-10-23 09:33
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "tb_sys_user_mapping_role",
        indexes = {@Index(columnList = "role_id,user_id", name = "UN_SYS_ROLE_MAPPING_USER", unique = true)
        })
public class UserMappingRole extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private SysRole sysRole;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private SysUser sysUser;

    public UserMappingRole(String userId, SysRole role) {
        this.userId = userId;
        this.roleId = role.getRoleId();
    }

    public UserMappingRole(String userId, String roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
