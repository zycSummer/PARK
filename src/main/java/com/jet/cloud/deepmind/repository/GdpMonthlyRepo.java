package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.GdpMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
