package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.SysEnergyGrade;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author yhy
 * @create 2019-11-06 13:54
 */
@Repository
public interface SysEnergyGradeRepo extends JpaRepository<SysEnergyGrade, Integer> {

    List<SysEnergyGrade> findAllByEnergyGradeId(String energyGradeId, Sort sort);

    SysEnergyGrade findByEnergyGradeId(String energyGradeId);
    @Transactional
    @Modifying
    void deleteByEnergyGradeId(String energyGradeId);
}
