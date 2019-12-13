package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Class RoleRepo
 *
 * @package
 */
@Repository
public interface RoleRepo extends JpaRepository<SysRole, Integer>, QuerydslPredicateExecutor<SysRole> {

    SysRole findByRoleId(String roleId);

    @Modifying
    @Transactional
    void deleteAllByRoleIdIn(List<String> roleIdList);

    @Query("SELECT new SysRole(t.roleId,t.roleName) from SysRole t")
    List<SysRole> getAllRoles();
}
