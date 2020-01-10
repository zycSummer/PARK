package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author yhy
 * @create 2019-11-07 10:57
 */
@Repository
public interface MeterRepo extends JpaRepository<Meter, Integer>, QuerydslPredicateExecutor<Meter> {

    List<Meter> findAllByObjTypeAndObjIdAndEnergyTypeIdInAndIsRankingTrue(String objType, String objId, List<String> energyTypeIdList);

    List<Meter> findAllByObjTypeAndObjIdAndEnergyTypeIdAndIsRankingTrue(String objType, String objId, String energyTypeId);

    @Query("select t from Meter t where t.objType = ?1 and t.objId = ?2 and (t.meterName like ?3 or t.meterId like ?3) order by t.sortId asc")
    List<Meter> findAllByObjTypeAndObjIdAndKeyOrderBySortIdAsc(String objType, String objId, String key);

    @Query("select t from Meter t where t.objType = ?1 and t.objId = ?2 and t.energyTypeId = ?3 and (t.meterName like ?4 or t.meterId like ?4) order by t.sortId asc")
    List<Meter> findAllByObjTypeAndObjIdAndEnergyTypeIdAndKeyOrderBySortIdAsc(String objType, String objId, String energyTypeId, String key);

    List<Meter> findAllByObjTypeAndObjIdOrderBySortIdAsc(String objType, String objId);

    List<Meter> findAllByObjTypeAndObjIdAndEnergyTypeIdOrderBySortId(String objType, String objId, String energyTypeId);

    List<Meter> findAllByObjTypeAndObjIdAndEnergyTypeIdAndIsRanking(String objType, String objId, String energyTypeId, Boolean isRanking);

    List<Meter> findAllByObjTypeAndObjIdAndIsRanking(String objType, String objId, Boolean isRanking);

    Meter findByObjTypeAndObjIdAndMeterIdOrderBySortId(String objType, String objId, String meterId);

    @Modifying
    @Transactional
    void deleteAllByIdIn(List<Integer> idList);

    @Modifying
    @Transactional
    void deleteAllByObjTypeAndObjId(String objType, String objId);

}
