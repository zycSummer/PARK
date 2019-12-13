package com.jet.cloud.deepmind.entity;

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
@Table(name = "tb_sys_role_mapping_menu",
        indexes = {@Index(columnList = "role_id,menu_id", name = "UN_SYS_ROLE_MAPPING_MENU", unique = true)
        })
public class MenuMappingRole extends BaseEntity {

    @Column(name = "menu_id", nullable = false)
    private String menuId;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private SysRole sysRole;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
    private SysMenu sysMenu;

    public MenuMappingRole(String roleId, String menuId, CurrentUser currentUser) {
        this.menuId = menuId;
        this.roleId = roleId;
        super.createUserId = currentUser.userId();
        super.createTime = LocalDateTime.now();
    }
}
