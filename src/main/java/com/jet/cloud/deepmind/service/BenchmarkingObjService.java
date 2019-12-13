package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.BenchmarkingObj;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/12/11 9:53
 */
public interface BenchmarkingObjService {
    Response query(String objType, String objId, String benchmarkingObjName);

    Response queryById(Integer id);

    @Transactional
    ServiceData addOrEdit(BenchmarkingObj benchmarkingObj);

    @Transactional
    ServiceData delete(String objType, String objId, String benchmarkingObjId);
}
