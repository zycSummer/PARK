package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.EquipSys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:33
 * @desc 对象设备系统信息表Repo
 */
@Repository
public interface EquipSysRepo extends JpaRepository<EquipSys, Integer>, QuerydslPredicateExecutor<EquipSys> {

    List<EquipSys> findByObjTypeAndObjIdOrderBySortId(String objType, String objId);

    EquipSys findByObjTypeAndObjIdAndEquipSysId(String objType, String objId, String equipSysId);

    @Modifying
    @Transactional
    @Query("delete from EquipSys where objType=?1 and objId=?2 and equipSysId=?3")
    void deleteEquipSys(String objType, String objId, String equipSysId);
}
