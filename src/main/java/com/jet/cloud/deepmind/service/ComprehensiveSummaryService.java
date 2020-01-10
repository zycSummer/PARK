package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.Response;

/**
 * @author maohandong
 * @create 2019/12/27 14:39
 */
public interface ComprehensiveSummaryService {
    Response query(String objType, String objId, String benchmarkingType);

    Response queryPosition(String objType, String objId);

    Response queryAllEnergy(String objType, String objId);

    Response queryInfo(String objType, String objId);
}
