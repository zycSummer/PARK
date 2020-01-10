package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysEnergyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/28 10:03
 * @desc 系统能源种类表Repo
 */
@Repository
public interface SysEnergyTypeRepo extends JpaRepository<SysEnergyType, Integer>, QuerydslPredicateExecutor<SysEnergyType> {

    List<SysEnergyType> findAllByOrderBySortId();

    List<SysEnergyType> findAllByEnergyTypeIdNot(String stdCoal);

    List<SysEnergyType> findAllByEnergyTypeIdNotOrderBySortId(String stdCoal);

    SysEnergyType findByEnergyTypeId(String energyTypeId);

    SysEnergyType findByEnergyTypeIdOrderBySortId(String energyTypeId);

    @Modifying
    void deleteByEnergyTypeId(String energyTypeId);

    List<SysEnergyType> findByEnergyTypeIdIn(List<String> energyTypeIds);
}