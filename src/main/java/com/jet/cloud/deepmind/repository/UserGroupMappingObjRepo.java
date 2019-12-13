package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.UserGroupMappingObj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/10/24 17:51
 */
@Repository
public interface UserGroupMappingObjRepo extends JpaRepository<UserGroupMappingObj, Integer> {

    List<UserGroupMappingObj> findAllByUserGroupIdAndObjType(String userGroupId, String objType);

    UserGroupMappingObj findByUserGroupIdAndObjTypeAndObjId(String userGroupId, String objType, String objId);

    @Modifying
    @Transactional
    void deleteByUserGroupId(String oldUserGroupId);

    List<UserGroupMappingObj> findByUserGroupId(String userGroupId);
}
