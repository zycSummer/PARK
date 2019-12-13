package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * @author yhy
 * @create 2019-10-11 14:56
 */
@Data
@Entity
@Table(name = "tb_sys_menu_function")
public class SysMenuFunction extends BaseEntity {

    @Column(name = "menu_id")
    private String menuId;

    @Column(name = "function_id", unique = true)
    private String functionId;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
    private SysMenu sysMenu;

    @Column(name = "function_name")
    private String functionName;
    @Column(name = "function_desc")
    private String functionDesc;
    private String url;

    private String method;

    @Transient
    private boolean checked;
}
