package com.jet.cloud.deepmind.service.report.impl;


import com.alibaba.fastjson.JSONArray;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.QReportParaDetail;
import com.jet.cloud.deepmind.entity.ReportParaDetail;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.SysEnergyParaRepo;
import com.jet.cloud.deepmind.repository.report.ReportParaDetailRepo;
import com.jet.cloud.deepmind.service.report.ReportParaDetailService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/7 13:47
 */
@Service
public class ReportParaDetailServiceImpl implements ReportParaDetailService {

    @Autowired
    private ReportParaDetailRepo reportParaDetailRepo;

    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;

    @Autowired
    private CurrentUser currentUser;


    @Override
    public Response query(String objType, String objId, String reportId, JSONArray energyParaIds, String displayName, String energyTypeId) {
        Sort sort = new Sort(Sort.Direction.ASC, "sortId");
        QReportParaDetail obj = QReportParaDetail.reportParaDetail;
        Predicate pre = obj.isNotNull();
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        pre = ExpressionUtils.and(pre, obj.reportId.containsIgnoreCase(reportId));
        if (displayName != null && !displayName.equals("")) {
            pre = ExpressionUtils.and(pre, obj.displayName.containsIgnoreCase(displayName));
        }
        List<String> energyParaIdList = energyParaIds.toJavaList(String.class);
        if (energyParaIdList != null && energyParaIdList.size() > 0) {
            pre = ExpressionUtils.and(pre, obj.energyParaId.in(energyParaIdList));
        }
        List<ReportParaDetail> reportParaDetailList = (List<ReportParaDetail>) reportParaDetailRepo.findAll(pre, sort);
        HashMap<String, String> map = new HashMap<>();
        List<SysEnergyPara> sysEnergyParaList = sysEnergyParaRepo.findByEnergyTypeIdOrderBySortId(energyTypeId);
        for (SysEnergyPara sysEnergyPara : sysEnergyParaList) {
            map.put(energyTypeId + sysEnergyPara.getEnergyParaId(), sysEnergyPara.getEnergyParaName());
        }
        for (ReportParaDetail reportParaDetail : reportParaDetailList) {
            String energyParaId = reportParaDetail.getEnergyParaId();
            String energyParaName = map.get(energyTypeId + energyParaId);
            reportParaDetail.setEnergyParaName(energyParaName);
        }
        Response ok = Response.ok(reportParaDetailList);
        ok.setQueryPara(objType, objId, reportId, energyParaIds, displayName, energyTypeId);
        return ok;
    }

    @Override
    @Transactional
    public ServiceData addOrEdit(ReportParaDetail reportParaDetail) {
        String objType = reportParaDetail.getObjType();
        String objId = reportParaDetail.getObjId();
        String energyParaId = reportParaDetail.getEnergyParaId();
        String reportId = reportParaDetail.getReportId();
        try {
            ReportParaDetail old = reportParaDetailRepo.findByObjTypeAndObjIdAndReportIdAndEnergyParaId(objType, objId, reportId, energyParaId);
            if (reportParaDetail.getId() == null) {
                if (old != null) {
                    return ServiceData.error("报表参数明细对象不唯一", currentUser);
                }
                reportParaDetail.setCreateNow();
                reportParaDetail.setCreateUserId(currentUser.userId());
                reportParaDetailRepo.save(reportParaDetail);
            } else {
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setDisplayName(reportParaDetail.getDisplayName());
                old.setAvgValue(reportParaDetail.getAvgValue());
                old.setDiffValue(reportParaDetail.getDiffValue());
                old.setMaxValue(reportParaDetail.getMaxValue());
                old.setMinValue(reportParaDetail.getMinValue());
                old.setTimeValue(reportParaDetail.getTimeValue());
                old.setMemo(reportParaDetail.getMemo());
                old.setSortId(reportParaDetail.getSortId());
                reportParaDetailRepo.save(old);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改报表明细失败", e, currentUser);
        }
        return ServiceData.success("新增或修改报表参数明细成功", currentUser);
    }

    @Override
    @Transactional
    public ServiceData delete(String objType, String objId, String reportId, String energyParaId) {
        try {
            reportParaDetailRepo.deleteByObjTypeAndObjIdAndReportIdAndEnergyParaId(objType, objId, reportId, energyParaId);
            return ServiceData.success("删除报表明细成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除报表明细失败", e, currentUser);
        }
    }
}
