package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/10/28 15:34
 */
@Repository
public interface ParkRepo extends JpaRepository<Park, Integer>, QuerydslPredicateExecutor<Park> {

    Park findByParkId(String objId);

    Park findFirstByOrderByParkIdAsc();

    @Query(value = "SELECT * FROM tb_park LIMIT 1", nativeQuery = true)
    Park findFirstPark();

    @Modifying
    @Transactional
    void deleteByParkId(String objId);
}
