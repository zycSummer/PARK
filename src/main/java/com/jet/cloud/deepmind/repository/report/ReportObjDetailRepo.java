package com.jet.cloud.deepmind.repository.report;

import com.jet.cloud.deepmind.entity.ReportObjDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/7 14:56
 */
@Repository
public interface ReportObjDetailRepo extends JpaRepository<ReportObjDetail, Integer>, QuerydslPredicateExecutor<ReportObjDetail> {

    List<ReportObjDetail> findByObjTypeAndObjIdAndReportIdOrderBySortIdAsc(String objType, String objId, String reportId);

    ReportObjDetail findByObjTypeAndObjIdAndReportIdAndNodeId(String objType, String objId, String reportId, String nodeId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndReportId(String objType, String objId, String reportId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndReportIdAndNodeId(String objType, String objId, String reportId, String nodeId);

    List<ReportObjDetail> findByObjTypeAndObjIdAndReportIdAndParentId(String objType, String objId, String reportId, String parentId);
}
