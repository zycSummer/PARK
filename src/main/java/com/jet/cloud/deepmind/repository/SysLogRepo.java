package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Class SysLogRepo
 *
 * @package
 */
@Repository
public interface SysLogRepo extends JpaRepository<SysLog, Integer>, QuerydslPredicateExecutor<SysLog> {
}
