package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.MenuMappingRole;
import com.jet.cloud.deepmind.entity.UserMappingRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Class MenuMappingRoleRepo
 *
 * @package
 */
@Repository
public interface MenuMappingRoleRepo extends JpaRepository<MenuMappingRole, Integer> {

    List<MenuMappingRole> findByRoleIdIn(List<String> roleIdList);

    List<MenuMappingRole> findByRoleId(String roleId);

    @Modifying
    @Transactional
    void deleteAllByIdIn(List<Integer> idList);

    @Modifying
    @Transactional
    void deleteAllByRoleId(String roleId);

    @Modifying
    @Transactional
    void deleteAllByRoleIdIn(List<String> roleIdList);

    @Modifying
    @Query("update MenuMappingRole u set u.roleId = ?2 where u.roleId =?1")
    @Transactional
    void updateRoleIdByOldRoleId(String oldRoleId, String newRoleId);
}
