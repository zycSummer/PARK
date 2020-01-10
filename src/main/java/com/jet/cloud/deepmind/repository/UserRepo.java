package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-10 09:43
 */
@Repository
public interface UserRepo extends JpaRepository<SysUser, Integer>, QuerydslPredicateExecutor<SysUser> {

    ///**
    // * @param userGroupCode 租户标识
    // * @param userName      用户名
    // * @return
    // */
    //Optional<SysUser> findByUserGroupCodeAndUserName(String userGroupCode, String userName);

    SysUser findByUserId(String userId);


    @Modifying
    @Transactional
    @Query("update SysUser u set u.lastLoginTime = ?2,u.lastLoginIp=?3 where u.id =?1")
    void updateLastLoginTime(Integer id, LocalDateTime lastLoginTime, String ip);
    //SysUser findByUserId(String userId);

    @Modifying
    @Transactional
    @Query("update SysUser u set u.lastLoginTime = ?2,u.lastLoginIp=?3 where u.userId =?1")
    void updateLastLoginTimeAndIp(String userId, LocalDateTime lastLoginTime, String ip);

    @Modifying
    @Transactional
    void deleteAllByUserIdIn(List<String> userIdList);

}
