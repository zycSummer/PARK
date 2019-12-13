package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-10 14:27
 */
public interface UserGroupRepo extends JpaRepository<UserGroup, Integer>, QuerydslPredicateExecutor<UserGroup> {
    @Query("SELECT new UserGroup(t.userGroupId,t.userGroupName) from UserGroup t")
    List<UserGroup> getAllUserGroups();

    UserGroup findByUserGroupId(String userGroupId);

    @Modifying
    @Transactional
    void deleteByUserGroupId(String userGroupId);
}
