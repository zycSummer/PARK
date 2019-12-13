package com.jet.cloud.deepmind.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/24 11:24
 * @desc UserGroupMappingObj
 */
@Entity
@Setter
@Getter
@Table(name = "tb_sys_user_group_mapping_obj")
public class UserGroupMappingObj extends BaseEntity {
    @Column(name = "user_group_id", nullable = false)
    private String userGroupId;

    @Column(name = "obj_type", nullable = false)
    private String objType;

    @Column(name = "obj_id", nullable = false)
    private String objId;
}
