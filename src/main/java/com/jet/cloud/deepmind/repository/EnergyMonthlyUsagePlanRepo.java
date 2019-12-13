package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.EnergyMonthlyUsagePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author zhuyicheng
 * @create 2019/11/8 16:26
 * @desc
 */
@Repository
public interface EnergyMonthlyUsagePlanRepo extends JpaRepository<EnergyMonthlyUsagePlan, Integer>, QuerydslPredicateExecutor<EnergyMonthlyUsagePlan> {
    EnergyMonthlyUsagePlan findByObjTypeAndObjIdAndEnergyTypeIdAndYearAndMonth(String objType, String objId, String energyTypeId, Integer year, Integer month);

/*    @Modifying
    @Query(value = "INSERT INTO tb_obj_energy_monthly_usage_plan  (obj_type,obj_id,energy_type_id,`year`,`month`,`usage`,memo,create_user_id,create_time) VALUES(?1,?2,?3,?4,?5,?6,?7,?8,?9)", nativeQuery = true)
    void saveEnergyMonthlyUsagePlan(String objType, String objId, String energyTypeId, Integer year, Integer month, Double usage, String memo, String userId, Timestamp createTime);*/
}
