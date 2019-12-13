package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/10/29 10:45
 */
@Repository
public interface DataSourceRepo extends JpaRepository<DataSource, Integer>, QuerydslPredicateExecutor<DataSource> {

    List<DataSource> findAllByObjTypeAndObjId(String objType, String objId);


    DataSource findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(String objType, String objId, String energyTypeId, String energyParaId);

    List<DataSource> findByObjTypeAndObjIdInAndEnergyTypeIdAndEnergyParaId(String objType, List<String> objIdList, String energyTypeId, String energyParaId);

    List<DataSource> findByObjTypeAndObjIdAndEnergyTypeIdIn(String objType, String objId, List<String> energyTypeIds);

    List<DataSource> findByObjTypeAndEnergyTypeIdAndEnergyParaIdAndObjIdIn(String objType, String energyTypeId, String energyParaId, List<String> objIdList);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(String objType, String objId, String energyTypeId, String energyParaId);
}
