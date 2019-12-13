package com.jet.cloud.deepmind.rtdb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.rtdb.ClientConfig;
import com.jet.cloud.deepmind.rtdb.model.QueryBody;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.script.ScriptException;
import java.time.LocalDateTime;
import java.util.*;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNullOrEmpty;

/**
 * @author yhy
 * @create 2019-11-04 09:49
 */
@Service
@Slf4j
public class KairosdbClientImpl implements KairosdbClient {

    @Autowired
    private ClientConfig clientConfig;
    @Autowired
    @Qualifier(value = "alarmRestTemplate")
    private RestTemplate restTemplate;
    @Autowired
    private Map<String, Double> getEnergy;

    @Override
    public SampleDataResponse queryLast(String pointId, Long timeOutMillisecond) {
        if (isNullOrEmpty(pointId)) return null;
        List<SampleDataResponse> data = queryLast(Lists.newArrayList(pointId), timeOutMillisecond);
        return data == null || data.size() == 0 ? null : data.get(0);
    }

    @Override
    public List<SampleDataResponse> queryLast(List<String> pointIds, Long timeOutMillisecond) {
        if (isNullOrEmpty(pointIds)) return new ArrayList<>();

        //请求
        ParseQueryBody parseQueryBody = new ParseQueryBody(pointIds).invoke();
        List<MetricMap> metricMapList = parseQueryBody.getMetricMapList();
        List<String> queryList = parseQueryBody.getQueryList();

        QueryBody body = new QueryBody(queryList);
        HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
        JSONArray jsonArray = restTemplate.postForEntity(clientConfig.getQueryLastSampleDataUrl(), httpEntity, JSONArray.class).getBody();
        //响应
        List<SampleDataResponse> javaList = jsonArray.toJavaList(SampleDataResponse.class);
        List<SampleDataResponse> result = new ArrayList<>();
        Map<String, SampleDataResponse> dataMap = new HashMap<>();
        for (SampleDataResponse response : javaList) {
            dataMap.put(response.getPoint(), response);
        }

        for (MetricMap map : metricMapList) {
            int partExpiredCount = 0;
            int totalCount = 0;
            //是公式
            if (map.isWithFormula()) {

                for (String point : map.getQueryMetric()) {
                    SampleDataResponse r = dataMap.get(point);
                    totalCount++;
                    //超时时间处理
                    if (timeOutMillisecond != null && r.getTimestamp() != null) {

                        long diffTime = System.currentTimeMillis() - r.getTimestamp();
                        if (diffTime > timeOutMillisecond) {
                            map.setValues(null);
                            partExpiredCount++;
                        } else {
                            map.setValues(r.getValue());
                        }
                    } else {
                        map.setValues(r.getValue());
                    }

                    if (map.getTimestamp() == null)
                        map.setTimestamp(r.getTimestamp());

                    // 只要有一个为空,就算是部分有值的一种
                    if (r.getValue() == null) map.setPartValues(true);
                }

                if (partExpiredCount == 0) {
                    map.setExpired(false);
                    map.setPartExpired(false);
                } else if (partExpiredCount < totalCount) {
                    map.setExpired(false);
                    map.setPartExpired(true);
                } else {
                    map.setPartExpired(true);
                    map.setExpired(true);
                }

                if (map.isWithRatio()) {
                    //1.有系数
                    if (map.getValues() == null
                            || map.getRatioList() == null || map.getValues().size() != map.getRatioList().size()) {
                        System.err.println("测点公式或系数配置错误");
                    }

                    int count = 0;
                    List<Double> temp = new ArrayList<>();
                    for (Double value : map.getValues()) {
                        Double radio = map.getRatioList().get(count);
                        if (value == null || radio == null) {
                            temp.add(null);
                        } else {
                            temp.add(value * radio);
                        }
                        count++;
                    }
                    map.setValueList(temp);
                } else {
                    //2.没有系数
                    ;
                }
                List<Double> doubleList = fillList(map.getValues());
                String value = null;
                if (isEmpty(doubleList)) {
                    //result.add(new SampleDataResponse(map.getMetric(), map.getTimestamp(), null));
                    result.add(SampleDataResponse.getExpiredInstance(map.getMetric(), map.getTimestamp()));

                } else {
                    // 替换公式中的?号
                    String expression = StringUtils.replaceEach(map.getFormula(), doubleList);
                    // 计算公式
                    try {
                        value = MathUtil.cal(expression);
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                    result.add(new SampleDataResponse(map.getMetric(), map.getTimestamp()
                            , value == null ? null : Double.parseDouble(value), map.isExpired(), map.isPartExpired(), map.isPartValues()));
                }

            } else {
                //普通测点
                SampleDataResponse r = dataMap.get(map.getQueryMetric().get(0));


                //boolean isExpired = timeOutMillisecond != null && r != null && r.getTimestamp() != null &&  System.currentTimeMillis() - r.getTimestamp() > timeOutMillisecond;

                Double val = null;
                if (map.isWithRatio()) {
                    //带系数
                    Double radio = map.getRatioList().get(0);
                    if (radio != null && r.getValue() != null)
                        val = radio * r.getValue();
                } else {
                    //不带系数
                    val = r.getValue();
                }

                //如果存在超时
                if (timeOutMillisecond != null && r.getTimestamp() != null) {
                    long diffTime = System.currentTimeMillis() - r.getTimestamp();
                    if (diffTime > timeOutMillisecond) {
                        map.setValues(null);
                        val = null;
                        map.setExpired(true);
                        map.setPartExpired(true);
                    } else {
                        map.setValues(val);
                        map.setExpired(false);
                        map.setPartExpired(false);
                    }
                }
                result.add(new SampleDataResponse(map.getMetric(), r.getTimestamp(), val, map.isExpired(), map.isPartExpired(), false));
            }
        }
        return result;
    }

    @Override
    public SampleData4KairosResp queryHis(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
        if (isNullOrEmpty(pointId, startTime, endTime, interval, unit)) return null;
        AggregatorDataResponse data = basicQuery(Lists.newArrayList(pointId), startTime, endTime, interval, unit
                , null, null, "His");
        if (data == null) return new SampleData4KairosResp(pointId, false);
        return parse(data);
    }

    @Override
    public SampleData4KairosResp queryHis(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit) {
        return queryHis(pointId, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit);
    }

    private SampleData4KairosResp parse(AggregatorDataResponse data) {
        DataPointResult value = data.getValues().get(0);
        return new SampleData4KairosResp(value.getMetricName(), value.getOpResult(), value.getMsg(), data.getTimestamps(), value.getValues());
    }

    @Override
    public AggregatorDataResponse queryHis(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
        return basicQuery(pointIds, startTime, endTime, interval, unit, null, null, "His");
    }

    @Override
    public AggregatorDataResponse queryHis(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit) {
        return queryHis(pointIds, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit);
    }

    @Override
    public SampleData4KairosResp queryMax(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return queryMax(pointId, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit, aggregationInterval, aggregationUnit);
    }

    @Override
    public SampleData4KairosResp queryMax(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        if (isNullOrEmpty(pointId, startTime, endTime, interval, unit)) return null;
        AggregatorDataResponse data = basicQuery(Lists.newArrayList(pointId), startTime, endTime, interval, unit
                , null, null, "Max");
        if (data == null) return new SampleData4KairosResp(pointId, false);
        return parse(data);
    }

    @Override
    public AggregatorDataResponse queryMax(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return basicQuery(pointIds, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit, "Max");

    }

    @Override
    public AggregatorDataResponse queryMax(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return queryMax(pointIds, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit, aggregationInterval, aggregationUnit);
    }

    @Override
    public SampleData4KairosResp queryMin(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return queryMin(pointId, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit, aggregationInterval, aggregationUnit);

    }

    @Override
    public SampleData4KairosResp queryMin(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        if (isNullOrEmpty(pointId, startTime, endTime, interval, unit)) return null;
        AggregatorDataResponse data = basicQuery(Lists.newArrayList(pointId), startTime, endTime, interval, unit
                , null, null, "Min");
        if (data == null) return new SampleData4KairosResp(pointId, false);
        return parse(data);
    }

    @Override
    public AggregatorDataResponse queryMin(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return basicQuery(pointIds, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit, "Min");
    }

    @Override
    public AggregatorDataResponse queryMin(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return queryMin(pointIds, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit, aggregationInterval, aggregationUnit);
    }

    @Override
    public SampleData4KairosResp queryAvg(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        if (isNullOrEmpty(pointId, startTime, endTime, interval, unit)) return null;
        AggregatorDataResponse data = basicQuery(Lists.newArrayList(pointId), startTime, endTime, interval, unit
                , null, null, "Avg");
        if (data == null) return new SampleData4KairosResp(pointId, false);
        return parse(data);
    }

    @Override
    public SampleData4KairosResp queryAvg(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return queryAvg(pointId, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit, aggregationInterval, aggregationUnit);
    }

    @Override
    public AggregatorDataResponse queryAvg(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return basicQuery(pointIds, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit, "Avg");
    }

    @Override
    public AggregatorDataResponse queryAvg(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
        return queryAvg(pointIds, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit, aggregationInterval, aggregationUnit);
    }

    @Override
    public SampleData4KairosResp queryDiff(String pointId, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit) {
        return queryDiff(pointId, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit);
    }

    @Override
    public SampleData4KairosResp queryDiff(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
        if (isNullOrEmpty(pointId, startTime, endTime, interval, unit)) return null;
        AggregatorDataResponse data = basicQuery(Lists.newArrayList(pointId), startTime, endTime, interval, unit
                , null, null, "Diff");
        if (data == null) return new SampleData4KairosResp(pointId, false);
        return parse(data);
    }

    @Override
    public AggregatorDataResponse queryDiff(List<String> pointIds, LocalDateTime startTime, LocalDateTime endTime, Integer interval, TimeUnit unit) {
        return queryDiff(pointIds, DateUtil.localDateTimeToLong(startTime), DateUtil.localDateTimeToLong(endTime), interval, unit);
    }

    @Override
    public AggregatorDataResponse queryDiff(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
        return basicQuery(pointIds, startTime, endTime, interval, unit, null, null, "Diff");
    }


    private AggregatorDataResponse basicQuery(List<String> pointIds, Long startTime, Long endTime
            , Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit, String queryType) {
        if (isNullOrEmpty(pointIds) || isNullOrEmpty(startTime, endTime, interval, unit)) return null;

        //请求
        ParseQueryBody parseQueryBody = new ParseQueryBody(pointIds).invoke();
        List<MetricMap> metricMapList = parseQueryBody.getMetricMapList();
        List<String> queryList = parseQueryBody.getQueryList();

        QueryBody body = new QueryBody(queryList, startTime, endTime, interval, unit);
        body.setAggregationInterval(aggregationInterval);
        body.setAggregationUnit(aggregationUnit);
        HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);

        String queryUrl = "";

        switch (queryType) {
            case "Max":
                queryUrl = clientConfig.getQueryMaxDataUrl();
                break;
            case "Min":
                queryUrl = clientConfig.getQueryMinDataUrl();
                break;
            case "Avg":
                queryUrl = clientConfig.getQueryAvgDataUrl();
                break;
            case "Diff":
                queryUrl = clientConfig.getQueryDiffDataUrl();
                break;
            case "His":
                queryUrl = clientConfig.getQueryHisDataUrl();
                break;
        }

        JSONObject jsonObject = restTemplate.postForEntity(queryUrl, httpEntity, JSONObject.class).getBody();
        JSONArray values = jsonObject.getJSONArray("values");
        JSONArray timestamps = jsonObject.getJSONArray("timestamps");
        List<Long> timestampList = timestamps.toJavaList(Long.class);
        List<DataPointResult> dataPointResults = values.toJavaList(DataPointResult.class);

        Map<String, DataPointResult> dataMap = new HashMap<>();
        for (DataPointResult result : dataPointResults) {
            dataMap.put(result.getMetricName(), result);
        }

        AggregatorDataResponse result = new AggregatorDataResponse();
        result.setTimestamps(timestampList);
        for (MetricMap map : metricMapList) {

            if (map.isWithFormula()) {
                Multimap<Long, Double> tempMap = ArrayListMultimap.create();
                //是公式
                for (String point : map.getQueryMetric()) {
                    List<Double> list = dataMap.get(point).getValues();
                    int count = 0;
                    for (Long timestamp : timestampList) {
                        tempMap.put(timestamp, list == null || list.size() == 0 ? null : list.get(count));
                        count++;
                    }
                }

                Multimap<Long, Double> tempMapSort = LinkedListMultimap.create();
                for (Long t : timestampList) {
                    tempMapSort.putAll(t, tempMap.get(t));
                }


                if (map.isWithRatio()) {
                    List<List<Double>> lists = new ArrayList<>();
                    //1.有系数
                    for (Long t : tempMapSort.keySet()) {
                        List<Double> doubleList = (List<Double>) tempMapSort.get(t);
                        List<Double> temp = new ArrayList<>();
                        if (!isNullOrEmpty(tempMapSort.get(t))) {
                            int count = 0;
                            for (Double v : doubleList) {
                                Double radio = map.getRatioList().get(count);
                                if (v == null || radio == null) {
                                    temp.add(null);
                                } else {
                                    temp.add(v * radio);
                                }
                                count++;
                            }
                        }
                        lists.add(temp);
                    }
                    map.setValueLists(lists);
                } else {
                    //2.没有系数
                    List<List<Double>> lists = new ArrayList<>();
                    for (Long t : tempMapSort.keySet()) {
                        lists.add((List<Double>) tempMapSort.get(t));
                    }
                    map.setValueLists(lists);
                }
                // 替换公式中的?号
                for (List<Double> doubleList : map.getValueLists()) {
                    String value = null;
                    doubleList = fillList(doubleList);
                    if (isEmpty(doubleList)) {
                        ;
                    } else {
                        String expression = StringUtils.replaceEach(map.getFormula(), doubleList);
                        try {
                            value = MathUtil.cal(expression);
                        } catch (Exception e) {
                            log.error("配置错误,map={}", map);
                            e.printStackTrace();
                        }
                    }
                    map.setValues(value == null ? null : Double.parseDouble(value));
                }

                //for (Long t : tempMapSort.keySet()) {
                //    String value = null;
                //    if (!StringUtils.isNullOrEmpty(tempMapSort.get(t))) {
                //        List<Double> doubleList = (List<Double>) tempMapSort.get(t);
                //        doubleList = fillList(doubleList);
                //        if (isEmpty(doubleList)) {
                //            ;
                //        } else {
                //            String expression = StringUtils.replaceEach(map.getFormula(), doubleList);
                //            try {
                //                value = MathUtil.cal(expression);
                //            } catch (ScriptException e) {
                //                e.printStackTrace();
                //            }
                //        }
                //    }
                //    map.setValues(value == null ? null : Double.parseDouble(value));
                //}

                result.setValues(new DataPointResult(map.getMetric(), map.getValues()));

            } else {
                //单侧点 但带系数"[" 20191128
                if (map.isWithRatio()) {
                    DataPointResult pointResult = dataPointResults.get(0);
                    Double ratio = map.getRatioList().get(0);
                    for (Double v : pointResult.getValues()) {
                        if (v == null || ratio == null) {
                            map.setValues(null);
                        } else {
                            map.setValues(v * ratio);
                        }
                    }
                    result.setValues(new DataPointResult(map.getMetric(), map.getValues()));
                } else {
                    //普通测点
                    result.setValues(dataMap.get(map.getMetric()));
                }

            }
        }
        return result;
    }

    private static List<Double> fillList(List<Double> doubleList) {

        List<Double> res = new ArrayList<>();
        boolean f = false;
        for (Double v : doubleList) {
            if (v != null) {
                f = true;
            }
        }
        if (f) {
            for (Double v : doubleList) {
                if (v == null) {
                    res.add(0d);
                } else {
                    res.add(v);
                }
            }
        } else {
            return doubleList;
        }
        return res;
    }


    private static <T> boolean isEmpty(List<T> data) {
        for (T t : data) {
            if (t != null) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        List<Double> list = fillList(Lists.newArrayList(1d, null));
        System.out.println(list);
        List<Double> list1 = fillList(Lists.newArrayList(null, null)); //1,0
        System.out.println(isEmpty(list));
        System.out.println(list1);
        List<Double> list2 = fillList(Lists.newArrayList(3d, null, 1d, null));
        System.out.println(list2);
    }

    private class ParseQueryBody {
        private List<String> pointIds;
        private List<MetricMap> metricMapList;
        private List<String> queryList;

        public ParseQueryBody(List<String> pointIds) {
            this.pointIds = pointIds;
        }

        public List<MetricMap> getMetricMapList() {
            return metricMapList;
        }

        public List<String> getQueryList() {
            return queryList;
        }

        public ParseQueryBody invoke() {
            metricMapList = new ArrayList<>();
            queryList = new ArrayList<>();
            for (String pointId : pointIds) {
                MetricMap metricMap = new MetricMap(pointId.trim(), getEnergy);
                metricMapList.add(metricMap);
                queryList.addAll(metricMap.getQueryMetric());
            }
            return this;
        }
    }
}
