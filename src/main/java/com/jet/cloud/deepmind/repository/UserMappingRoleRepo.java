package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.UserMappingRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Class UserMappingRoleRepo
 *
 * @package
 */
@Repository
public interface UserMappingRoleRepo extends JpaRepository<UserMappingRole, Integer> {

    List<UserMappingRole> findByRoleIdIn(List<String> roleIdList);

    List<UserMappingRole> findByRoleId(String roleId);

    @Modifying
    @Transactional
    void deleteAllByUserId(String userId);

    @Modifying
    @Query(nativeQuery = true, value = "update tb_sys_user_mapping_role t  set t.role_id = ?2 where t.role_id =?1")
    @Transactional
    void updateRoleIdByOldRoleId(String oldRoleId, String newRoleId);

    @Modifying
    @Query("update UserMappingRole u set u.userId = ?2 where u.userId =?1")
    @Transactional
    void updateUserIdByOldUserId(String oldUserId, String newUserId);

    List<UserMappingRole> findAllByUserId(String userId);


    @Query("select t.roleId from UserMappingRole t where t.userId=?1")
    List<String> findRoleIdByUserId(String userId);

    List<UserMappingRole> findByUserIdIn(List<String> userIdList);
}
