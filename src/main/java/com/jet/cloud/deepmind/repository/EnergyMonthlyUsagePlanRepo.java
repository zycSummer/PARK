package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.EnergyMonthlyUsagePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/8 16:26
 * @desc
 */
@Repository
public interface EnergyMonthlyUsagePlanRepo extends JpaRepository<EnergyMonthlyUsagePlan, Integer>, QuerydslPredicateExecutor<EnergyMonthlyUsagePlan> {
    EnergyMonthlyUsagePlan findByObjTypeAndObjIdAndEnergyTypeIdAndYearAndMonth(String objType, String objId, String energyTypeId, Integer year, Integer month);

    @Query(nativeQuery = true, value = "SELECT * FROM tb_obj_energy_monthly_usage_plan e WHERE e.obj_type=?1 AND e.obj_id = ?2 AND e.energy_type_id IN (?3) AND CONCAT(`year`,'-', LPAD(`month`, 2, 0)) >= ?4  AND CONCAT(`year`,'-', LPAD(`month`, 2, 0)) <= ?5 ORDER BY `year` DESC, LPAD(`month`, 2, 0) DESC #{#pageable}")
    Page<EnergyMonthlyUsagePlan> findData(String objType, String objId, List<String> energyParaIdList, String startDate, String endDate, Pageable pageable);
}
