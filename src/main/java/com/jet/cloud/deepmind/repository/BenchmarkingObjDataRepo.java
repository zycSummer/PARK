package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.BenchmarkingObjData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/12/11 10:35
 */
@Repository
public interface BenchmarkingObjDataRepo extends JpaRepository<BenchmarkingObjData, Integer>, QuerydslPredicateExecutor<BenchmarkingObjData> {

    BenchmarkingObjData findByObjTypeAndObjIdAndBenchmarkingObjIdAndYear(String objType, String objId, String benchmarkingObjId, Integer year);

    List<BenchmarkingObjData> findAllByObjTypeAndObjIdAndYear(String objType, String objId, int year);

    @Modifying
    @Transactional
    void deleteAllByObjTypeAndObjIdAndBenchmarkingObjId(String objType, String objId, String benchmarkingObjId);
}
