package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.BenchmarkingObjData;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/12/11 10:34
 */
public interface BenchmarkingObjDataService {
    Response query(QueryVO vo);

    Response queryById(Integer id);

    @Transactional
    ServiceData addOrEdit(BenchmarkingObjData benchmarkingObjData);

    ServiceData delete(Integer id);
}
