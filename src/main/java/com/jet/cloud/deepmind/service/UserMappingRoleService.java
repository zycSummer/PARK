package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.UserMappingRole;

import java.util.List;

/**
 * @author yhy
 * @create 2019-10-14 15:14
 */
public interface UserMappingRoleService {

    List<UserMappingRole> findAllByLoginNameAndUserGroup(String loginName, String userGroup);

    List<String> getRoleIdList(String userId);
}
