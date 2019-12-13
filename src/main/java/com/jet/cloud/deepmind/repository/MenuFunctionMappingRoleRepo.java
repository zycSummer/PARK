package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.MenuFunctionMappingRole;
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
public interface MenuFunctionMappingRoleRepo extends JpaRepository<MenuFunctionMappingRole, Integer> {

    List<MenuFunctionMappingRole> findByRoleIdIn(List<String> roleIdList);

    @Query(nativeQuery = true, value = "select concat(f.menu_id, f.function_id ) AS token from tb_sys_role_mapping_menu_function f where f.role_id in ?1 and f.menu_id = ?2 ")
    List<String> findByRoleIdInAndMenuIdStr(List<String> roleIdList, String menuId);

    List<MenuFunctionMappingRole> findByRoleIdInAndMenuId(List<String> roleIdList, String menuId);

    List<MenuFunctionMappingRole> findByRoleId(String roleId);

    @Modifying
    @Transactional
    void deleteAllByIdIn(List<Long> idList);

    @Modifying
    @Transactional
    void deleteAllByRoleId(String roleId);

    @Modifying
    @Transactional
    void deleteAllByRoleIdIn(List<String> roleIdList);

    @Modifying
    @Query("update MenuFunctionMappingRole u set u.roleId = ?2 where u.roleId =?1")
    @Transactional
    void updateRoleIdByOldRoleId(String oldRoleId, String newRoleId);
}
