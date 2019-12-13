package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.Response;

/**
 * @author maohandong
 * @create 2019/12/11 11:51
 */
public interface BenchmarkingRankingService {

    Response queryObj(String objType, String objId, String benchmarkingType, Long timestamp, String timeUnit);
}
