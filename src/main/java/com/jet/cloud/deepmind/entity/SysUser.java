package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import com.jet.cloud.deepmind.common.util.StringUtils;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author yhy
 * @create 2019-10-10 09:39
 */

@Data
@Entity
@Table(name = "tb_sys_user", indexes = {
        @Index(columnList = "user_id", name = "UN_SYS_USER", unique = true)
})
public class SysUser extends BaseEntity {

    @Column(name = "user_id", unique = true)
    @Pattern(regexp = "^[A-Za-z0-9_]{0,20}$", message = "只能输入字母、数字、下划线组合且不能大于20位")
    private String userId;

    @Column(name = "user_group_id")
    private String userGroupId;

    @Column(name = "user_name")
    private String userName;
    @JsonIgnore
    @Column(name = "pwd")
    private String password;

    @Column(name = "is_enabled")
    @Convert(converter = String2BooleanConverter.class)
    private boolean isEnabled;

    @Column(name = "is_locked")
    @Convert(converter = String2BooleanConverter.class)
    private boolean isLocked;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "expire_date")
    private LocalDate expireDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_group_id", referencedColumnName = "user_group_id", insertable = false, updatable = false)
    private UserGroup userGroup;


    @Transient
    private List<SysRole> roleList;
    @Transient
    private String userGroupName;

}
