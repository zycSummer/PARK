package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.common.VResult;

/**
 * @author zhuyicheng
 * @create 2019/11/25 10:10
 * @desc api服务
 */
public interface ApiService {
    VResult queryMeter(String parkId, String meterId);

    VResult queryMeterLastValue(String parkId, String meterId);

    VResult queryMeterHisValue(String parkId, String meterId, String paraId, Long startTime, Long endTime, Integer interval);

    VResult queryMeterUsageValue(String parkId, String meterId, String date);

    VResult queryMeterNextMaxLoadValue(String parkId, String meterId);
}
