package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author yhy
 * @create 2019-10-11 14:55
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tb_sys_menu")
public class SysMenu extends BaseEntity {

    @Column(name = "menu_id", unique = true)
    private String menuId;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "sort_id")
    private String sortId;

    @Column(name = "parent_id")
    private String parentId;

    ///**
    // * 是否可分配
    // */
    //@Column(name = "is_allocation")
    //private boolean isAllocation;

    private String icon;

    private String url;
    /**
     * enum:'POST','GET','PUT','DELETE'
     */
    private String method;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
    @JsonIgnore
    private Set<SysMenuFunction> sysMenuFunctionList;

    @Transient
    private boolean checked;
}
