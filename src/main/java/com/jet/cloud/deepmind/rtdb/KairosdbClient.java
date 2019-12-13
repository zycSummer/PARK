//package com.jet.cloud.deepmind.rtdb;
//
//import com.jet.cloud.deepmind.rtdb.model.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.*;
//
///**
// * @author zhuyicheng
// * @create 2019-10-31 13:56
// */
//@RestController
//@Slf4j
//public class KairosdbClient {
//    @Autowired
//    KairosdbClientService kairosdbClientService;
//
//    /**
//     * @param pointId 支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @return
//     * @apiNote 查询单测点最新值
//     */
//    public SampleDataResponse getPointLastData(String pointId) {
//        return kairosdbClientService.getPointLastData(pointId);
//    }
//
//    /**
//     * @param pointIds 支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @return
//     * @apiNote 查询多测点集合最新值
//     */
//    public List<SampleDataResponse> getPointLastData(List<String> pointIds) {
//        return kairosdbClientService.getPointLastData(pointIds);
//    }
//
//    /**
//     * @param pointId   支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @param interval  时间间隔
//     * @param unit      单位
//     * @return
//     * @apiNote 查询单测点在同一时间段内按指定间隔的时刻值数据
//     */
//    public SampleData4KairosResp queryHis(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        return kairosdbClientService.queryHis(pointId, startTime, endTime, interval, unit);
//    }
//
//    /**
//     * @param pointIds  支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @param interval  时间间隔
//     * @param unit      单位
//     * @return
//     * @apiNote 查询单测点在同一时间段内按指定间隔的时刻值数据
//     */
//    public List<SampleData4KairosResp> queryHis(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        return kairosdbClientService.queryHis(pointIds, startTime, endTime, interval, unit);
//    }
//
//    /**
//     * @param pointId             支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @param startTime           开始时间
//     * @param endTime             结束时间
//     * @param interval            时间间隔
//     * @param unit                单位
//     * @param aggregationInterval 时间间隔
//     * @param aggregationUnit     单位
//     * @return
//     * @apiNote 查询单个测点在同一时间段内按指定间隔的最大值数据
//     */
//    public SampleData4KairosResp queryMax(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        return kairosdbClientService.queryMax(pointId, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//    }
//
//    /**
//     * @param pointIds            支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @param startTime           开始时间
//     * @param endTime             结束时间
//     * @param interval            时间间隔
//     * @param unit                单位
//     * @param aggregationInterval 时间间隔
//     * @param aggregationUnit     单位
//     * @return
//     * @apiNote 查询多测点在同一时间段内按指定间隔的最大值数据
//     */
//    public List<SampleData4KairosResp> queryMax(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        return kairosdbClientService.queryMax(pointIds, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//    }
//
//
//    /**
//     * @param pointId             支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @param startTime           开始时间
//     * @param endTime             结束时间
//     * @param interval            时间间隔
//     * @param unit                单位
//     * @param aggregationInterval 时间间隔
//     * @param aggregationUnit     单位
//     * @return
//     * @apiNote 查询单个测点在同一时间段内按指定间隔的最小值数据
//     */
//    public SampleData4KairosResp queryMin(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        return kairosdbClientService.queryMin(pointId, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//    }
//
//    /**
//     * @param pointIds            支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @param startTime           开始时间
//     * @param endTime             结束时间
//     * @param interval            时间间隔
//     * @param unit                单位
//     * @param aggregationInterval 时间间隔
//     * @param aggregationUnit     单位
//     * @return
//     * @apiNote 查询多测点在同一时间段内按指定间隔的最小值数据
//     */
//    public List<SampleData4KairosResp> queryMin(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        return kairosdbClientService.queryMin(pointIds, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//    }
//
//    /**
//     * @param pointId             支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @param startTime           开始时间
//     * @param endTime             结束时间
//     * @param interval            时间间隔
//     * @param unit                单位
//     * @param aggregationInterval 时间间隔
//     * @param aggregationUnit     单位
//     * @return
//     * @apiNote 查询单个测点在同一时间段内按指定间隔的平均值数据
//     */
//    public SampleData4KairosResp queryAvg(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        return kairosdbClientService.queryAvg(pointId, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//    }
//
//    /**
//     * @param pointIds            支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @param startTime           开始时间
//     * @param endTime             结束时间
//     * @param interval            时间间隔
//     * @param unit                单位
//     * @param aggregationInterval 时间间隔
//     * @param aggregationUnit     单位
//     * @return
//     * @apiNote 查询多测点在同一时间段内按指定间隔的平均值数据
//     */
//    public List<SampleData4KairosResp> queryAvg(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        return kairosdbClientService.queryAvg(pointIds, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//    }
//
//    /**
//     * @param pointId   支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @param interval  时间间隔
//     * @param unit      单位
//     * @return
//     * @apiNote 查询单个测点在同一时间段内按指定间隔的差值数据
//     */
//    public SampleData4KairosResp queryDiff(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        return kairosdbClientService.queryDiff(pointId, startTime, endTime, interval, unit);
//    }
//
//    /**
//     * @param pointIds  支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @param interval  时间间隔
//     * @param unit      单位
//     * @return
//     * @apiNote 查询多测点在同一时间段内按指定间隔的差值数据
//     */
//    public List<SampleData4KairosResp> queryDiff(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        return kairosdbClientService.queryDiff(pointIds, startTime, endTime, interval, unit);
//    }
//
//    public static void main(String[] args) {
//        String s = "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]";
//        System.out.println(s.indexOf('['));
//    }
//}
