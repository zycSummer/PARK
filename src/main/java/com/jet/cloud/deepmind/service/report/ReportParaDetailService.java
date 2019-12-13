package com.jet.cloud.deepmind.service.report;

import com.alibaba.fastjson.JSONArray;
import com.jet.cloud.deepmind.entity.ReportParaDetail;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/11/7 13:46
 */
public interface ReportParaDetailService {

    Response query(String objType, String objId, String reportId, JSONArray energyParaIds, String displayName,String energyTypeId);

    @Transactional
    ServiceData addOrEdit(ReportParaDetail reportParaDetail);


    ServiceData delete(String objType, String objId, String reportId, String energyParaId);
}
