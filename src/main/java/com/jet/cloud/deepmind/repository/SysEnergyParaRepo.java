package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysEnergyPara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/29 9:35
 * @desc 系统能源参数表Repo
 */
@Repository
public interface SysEnergyParaRepo extends JpaRepository<SysEnergyPara, Integer>, QuerydslPredicateExecutor<SysEnergyPara> {

    List<SysEnergyPara> findByEnergyTypeIdOrderBySortId(String energyTypeId);

    SysEnergyPara findByEnergyTypeIdAndEnergyParaId(String energyTypeId, String energyParaId);

    @Modifying
    void deleteByEnergyParaId(String energyParaId);

    @Query("select t from SysEnergyPara t where energyTypeId=?2 and (t.energyParaId like ?1 or t.energyParaName like ?1) order by t.sortId ")
    List<SysEnergyPara> queryEnergyParaIdOrEnergyParaName(String key, String energyTypeId);
}
