package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 租户实体
 *
 * @author yhy
 * @create 2019-10-10 14:26
 */
@Data
@Entity
@Table(name = "tb_sys_user_group")
public class UserGroup extends BaseEntity {
    @Column(name = "user_group_id", unique = true, nullable = false)
    @NotNull(message = "用户组标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String userGroupId;

    @Column(name = "user_group_name", nullable = false)
    @NotNull(message = "用户组名称不能为空")
    @Size(min = 1, max = 30, message = "用户组名称不超过30")
    private String userGroupName;

    @Transient
    private List<Map<String,String>> obj;

    @Transient
    private List<UserGroupMappingObj> list;

    public UserGroup() {
    }

    public UserGroup(String userGroupId, String userGroupName) {
        this.userGroupId = userGroupId;
        this.userGroupName = userGroupName;
    }
}
