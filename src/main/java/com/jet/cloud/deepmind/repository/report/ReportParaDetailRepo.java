package com.jet.cloud.deepmind.repository.report;

import com.jet.cloud.deepmind.entity.ReportParaDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/6 15:45
 */
@Repository
public interface ReportParaDetailRepo extends JpaRepository<ReportParaDetail, Integer>, QuerydslPredicateExecutor<ReportParaDetail> {

    ReportParaDetail findByObjTypeAndObjIdAndReportIdAndEnergyParaId(String objType, String objId, String reportId, String EnergyParaId);

    List<ReportParaDetail> findByObjTypeAndObjIdAndReportIdOrderBySortId(String objType, String objId, String reportId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndReportIdAndEnergyParaId(String type, String objType, String objId, String reportId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndReportId(String objType, String objId, String reportId);
}
