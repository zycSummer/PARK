package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.CurrentUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author yhy
 * @create 2019-10-11 15:52
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "tb_sys_role_mapping_menu_function", indexes = {
        @Index(columnList = "role_id, menu_id, function_id", unique = true)
})
public class MenuFunctionMappingRole extends BaseEntity {

    @Column(name = "function_id", nullable = false)
    private String functionId;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "menu_id", nullable = false)
    private String menuId;


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    @JsonIgnore
    private SysRole sysRole;


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "function_id", referencedColumnName = "function_id", insertable = false, updatable = false),
            @JoinColumn(name = "menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)

    })
    @JsonIgnore
    private SysMenuFunction sysMenuFunction;

    public MenuFunctionMappingRole(String roleId, String menuId, String functionId, CurrentUser currentUser) {
        this.roleId = roleId;
        this.menuId = menuId;
        this.functionId = functionId;
        super.createUserId = currentUser.userId();
        super.createTime = LocalDateTime.now();
    }
}
