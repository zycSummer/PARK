package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.BenchmarkingObj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/12/11 9:59
 * @desc 对标对象管理
 */
@Repository
public interface BenchmarkingObjRepo extends JpaRepository<BenchmarkingObj, Integer>, QuerydslPredicateExecutor<BenchmarkingObj> {

    BenchmarkingObj findByObjTypeAndObjIdAndBenchmarkingObjId(String objType, String objId, String benchmarkingObjId);

    List<BenchmarkingObj> findByObjTypeAndObjIdOrderBySortId(String objType, String objId);

    @Transactional
    @Modifying
    void deleteByObjTypeAndObjIdAndBenchmarkingObjId(String objType, String objId, String benchmarkingObjId);
}
