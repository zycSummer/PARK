package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.AlarmCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author zhuyicheng
 * @create 2019/11/11 14:01
 * @desc 对象报警条件设置
 */
@Repository
public interface AlarmConditionRepo extends JpaRepository<AlarmCondition, Integer> {
    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndAlarmId(String objType, String objId, String alarmId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndAlarmIdAndAlarmConditionId(String objType, String objId, String alarmId, String alarmConditionId);

    AlarmCondition findByObjTypeAndObjIdAndAlarmIdAndAlarmConditionId(String objType, String objId, String alarmId, String alarmConditionId);

    Page<AlarmCondition> findAllByObjTypeAndObjIdAndAlarmIdAndDataSourceContains(String objType, String objId, String alarmId, String datasource, Pageable pageable);

    Page<AlarmCondition> findAllByObjTypeAndObjIdAndAlarmId(String objType, String objId, String alarmId, Pageable pageable);
}
