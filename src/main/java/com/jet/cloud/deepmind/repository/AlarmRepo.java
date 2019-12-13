package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/11 13:53
 * @desc 对象报警Repo
 */
@Repository
public interface AlarmRepo extends JpaRepository<Alarm, Integer>, QuerydslPredicateExecutor<Alarm> {
    Alarm findByObjTypeAndObjIdAndAlarmId(String objType, String objId, String alarmId);

    List<Alarm> findByIsUseTrue();

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndAlarmId(String objType, String objId, String alarmId);

}
