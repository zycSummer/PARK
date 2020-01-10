package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.AlarmMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/11 14:02
 * @desc 对象报警信息
 */
@Repository
public interface AlarmMsgRepo extends JpaRepository<AlarmMsg, Integer>, QuerydslPredicateExecutor<AlarmMsg> {
    AlarmMsg findByObjTypeAndObjIdAndAlarmIdAndAlarmTime(String objType, String objId, String alarmId, LocalDateTime time);

    @Query(value = "SELECT * FROM `tb_obj_alarm_msg` WHERE obj_type=?1 AND obj_id=?2 AND alarm_time >= ?3 AND alarm_time <= ?4 ORDER BY alarm_time DESC", nativeQuery = true)
    List<AlarmMsg> findAlarmMsg(String objType, String objId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT & FROM `tb_obj_alarm_msg` WHERE obj_type=?1 AND obj_id=?2 AND alarm_time >= ?3  ORDER BY alarm_time DESC", nativeQuery = true)
    List<AlarmMsg> findAlarmMsgNoEnd(String objType, String objId, LocalDateTime start);

}
