package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.FileMgr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author maohandong
 * @create 2019/10/30 10:38
 */
@Repository
public interface DocumentManagementRepo extends JpaRepository<FileMgr,Integer>,QuerydslPredicateExecutor<FileMgr> {
}
