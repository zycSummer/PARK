package com.jet.cloud.deepmind.repository.report;

import com.jet.cloud.deepmind.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/4 11:24
 */
@Repository
public interface ReportManageRepo extends JpaRepository<Report, Integer>, QuerydslPredicateExecutor<Report> {
    @Modifying
    @Transactional
    @Query("update Report set isUse = ?3 where objType = ?1 and objId = ?2 and reportId = ?4")
    void updateReportState(String objType, String objId, String isUse, String reportId);

    Report findByObjTypeAndObjIdAndReportId(String objType, String objId, String reportId);

    List<Report> findByObjTypeAndObjIdAndIsUseOrderBySortId(String objType, String objId, String isUse);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndReportId(String objType, String objId, String reportId);
}
