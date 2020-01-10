package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.GdpMonthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author maohandong
 * @create 2019/10/29 10:45
 */
@Repository
public interface GdpMonthlyRepo extends JpaRepository<GdpMonthly, Integer>, QuerydslPredicateExecutor<GdpMonthly> {

    List<GdpMonthly> findByObjTypeAndObjIdAndYear(String objType, String objId, int year);

    GdpMonthly findByObjTypeAndObjIdAndYearAndMonth(String objType, String objId, Integer year, Integer month);

    List<GdpMonthly> findByObjTypeAndYearAndMonthAndObjIdIn(String objType, Integer year, Integer month, List<String> objIdList);

    @Query(nativeQuery = true, value = "SELECT * FROM tb_obj_gdp_monthly e WHERE e.obj_type=?1 AND e.obj_id = ?2  AND CONCAT(`year`,'-', LPAD(`month`, 2, 0)) >= ?3  AND CONCAT(`year`,'-', LPAD(`month`, 2, 0)) <= ?4 ORDER BY `year` DESC, LPAD(`month`, 2, 0) DESC #{#pageable}")
    Page<GdpMonthly> findData(String objType, String objId, String startDate, String endDate, Pageable pageable);


}
