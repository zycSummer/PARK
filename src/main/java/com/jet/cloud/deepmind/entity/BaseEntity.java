package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.repository.UserRepo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yhy
 * @create 2019-10-10 09:23
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 5978284414457154390L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "identity")
    @GenericGenerator(name = "identity", strategy = "identity")
    @Column(name = "seq_id")
    protected Integer id;

    @Column(name = "create_user_id")
    protected String createUserId;
    @Column(name = "update_user_id")
    protected String updateUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(name = "create_time")
    protected LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    @Column(name = "update_time")
    protected LocalDateTime updateTime;

    @Column(name = "memo")
    protected String memo;

    public void setCreateNow() {
        this.createTime = LocalDateTime.now();
    }

    public void setUpdateNow() {
        this.updateTime = LocalDateTime.now();
    }

    //@JsonIgnore
    //public SysUser getCreateUser(String createUserName, String userGroupCode, UserRepo userRepo) {
    //    if (StringUtils.isNullOrEmpty(createUserName, userGroupCode)) return null;
    //    return userRepo.findByUserGroupCodeAndUserName(userGroupCode, createUserName).get();
    //}
    //
    //@JsonIgnore
    //public SysUser getUpdateUser(String updateUserName, String userGroupCode, UserRepo userRepo) {
    //    if (StringUtils.isNullOrEmpty(updateUserName, userGroupCode)) return null;
    //    return userRepo.findByUserGroupCodeAndUserName(userGroupCode, updateUserName).get();
    //}
}
