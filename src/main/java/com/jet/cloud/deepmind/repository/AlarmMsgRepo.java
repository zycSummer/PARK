package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.AlarmMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author zhuyicheng
 * @create 2019/11/11 14:02
 * @desc 对象报警信息
 */
@Repository
public interface AlarmMsgRepo extends JpaRepository<AlarmMsg, Integer>, QuerydslPredicateExecutor<AlarmMsg> {

}
