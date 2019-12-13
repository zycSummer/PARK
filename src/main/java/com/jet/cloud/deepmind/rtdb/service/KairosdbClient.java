package com.jet.cloud.deepmind.rtdb.service;

import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.SampleDataResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class KairosdbClient
 *
 * @package
 */
public interface KairosdbClient {


    /**
     * @param pointId 支持普通测点、公式、带系数的普通测点，带系数的公式
     * @return
     * @apiNote 查询测点最新值
     */
    SampleDataResponse queryLast(String pointId, Long timeOutMillisecond);

    /**
     * @param pointIds 支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
     * @return
     * @apiNote 查询测点集合最新值
     */
    List<SampleDataResponse> queryLast(List<String> pointIds, Long timeOutMillisecond);

    /**
     * @apiNote 查询单测点在同一时间段内按指定间隔的时刻值数据
     */
    SampleData4KairosResp queryHis(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit);

    SampleData4KairosResp queryHis(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit);

    /**
     * @param pointIds  支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔
     * @param unit      单位
     * @return
     * @apiNote 查询集合在同一时间段内按指定间隔的时刻值数据
     */
    AggregatorDataResponse queryHis(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit);

    AggregatorDataResponse queryHis(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit);

    SampleData4KairosResp queryMax(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    SampleData4KairosResp queryMax(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    AggregatorDataResponse queryMax(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    AggregatorDataResponse queryMax(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    SampleData4KairosResp queryMin(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    SampleData4KairosResp queryMin(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    AggregatorDataResponse queryMin(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    AggregatorDataResponse queryMin(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    SampleData4KairosResp queryAvg(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    SampleData4KairosResp queryAvg(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    AggregatorDataResponse queryAvg(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    AggregatorDataResponse queryAvg(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit
            , Integer aggregationInterval, TimeUnit aggregationUnit);

    SampleData4KairosResp queryDiff(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit);

    SampleData4KairosResp queryDiff(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit);

    AggregatorDataResponse queryDiff(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit);

    AggregatorDataResponse queryDiff(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit);

}
