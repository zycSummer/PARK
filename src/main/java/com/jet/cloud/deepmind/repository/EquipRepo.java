package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.Equip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:33
 * @desc 对象设备信息表Repo
 */
@Repository
public interface EquipRepo extends JpaRepository<Equip, Integer>, QuerydslPredicateExecutor<Equip> {
    Equip findByObjTypeAndObjIdAndEquipId(String objType, String objId, String equipId);

    @Modifying
    @Transactional
    @Query("delete from Equip where objType=?1 and objId=?2 and equipId=?3")
    void deleteEquip(String objType, String objId, String equipId);

    @Modifying
    @Transactional
    void deleteAllByObjTypeAndObjId(String objType, String objId);
}
