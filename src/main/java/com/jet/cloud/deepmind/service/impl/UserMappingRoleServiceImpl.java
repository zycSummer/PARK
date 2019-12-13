package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.UserMappingRole;
import com.jet.cloud.deepmind.repository.UserMappingRoleRepo;
import com.jet.cloud.deepmind.service.UserMappingRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jet.cloud.deepmind.common.Constants.SYMBOL_SPLIT;

/**
 * @author yhy
 * @create 2019-10-14 15:15
 */
@Service
public class UserMappingRoleServiceImpl implements UserMappingRoleService {

    @Autowired
    private UserMappingRoleRepo userMappingRoleRepo;


    @Override
    public List<UserMappingRole> findAllByLoginNameAndUserGroup(String loginName, String userGroup) {
        String key;
        if (StringUtils.isNullOrEmpty(userGroup)) {
            key = loginName;
        } else {
            key = userGroup + SYMBOL_SPLIT + loginName;
        }
        return userMappingRoleRepo.findAllByUserId(key);
    }

    @Override
    public List<String> getRoleIdList(String userId) {
        List<String> roleIdList = new ArrayList<>();
        //获取用户角色集合
        for (UserMappingRole userMappingRole : userMappingRoleRepo.findAllByUserId(userId)) {
            roleIdList.add(userMappingRole.getRoleId());
        }
        return roleIdList;
    }
}
