//package com.jet.cloud.deepmind.rtdb;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Multimap;
//import com.jet.cloud.deepmind.common.util.MathUtil;
//import com.jet.cloud.deepmind.common.util.StringUtils;
//import com.jet.cloud.deepmind.entity.SysEnergyType;
//import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
//import com.jet.cloud.deepmind.rtdb.model.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.PostConstruct;
//import javax.script.ScriptException;
//import java.util.*;
//
///**
// * @author zhuyicheng
// * @create 2019/11/1 16:11
// * @desc
// */
//@Service
//@Slf4j
//public class KairosdbClientService {
//    private static String FORMULA = "#";
//    private static char RATIO = '[';
//
//    private Map<String, Double> mapEnergy;
//    @Autowired
//    private SysEnergyTypeRepo sysEnergyTypeRepo;
//
//    @Autowired
//    ClientConfig clientConfig;
//    @Autowired
//    private RestTemplate restTemplate;
//
//
//    private String urlPrefix() {
//        return clientConfig.getHost() + clientConfig.getUrlPrefix();
//    }
//
//    /**
//     * todo
//     * 对tb_sys_energy_type进行CRUD记得更新缓存
//     */
//    @PostConstruct
//    public void getEnergy() {
//        List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAll();
//        mapEnergy = new HashMap<>();
//        if (sysEnergyTypes != null && !sysEnergyTypes.isEmpty()) {
//            for (SysEnergyType sysEnergyType : sysEnergyTypes) {
//                mapEnergy.put(sysEnergyType.getEnergyTypeId(), sysEnergyType.getStdCoalCoeff());
//            }
//        }
//    }
//
//    /**
//     * @param pointId 支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @return
//     * @apiNote 查询测点最新值
//     */
//    public SampleDataResponse getPointLastData(String pointId) {
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleDataResponse> sampleDataResponses = getLastSimplePointData(body, dataSources);
//                        List<Double> resultValues = new ArrayList<>();
//                        List<Double> resultValuesResult = new ArrayList<Double>();
//                        List<Double> results = new ArrayList<Double>();
//                        if (sampleDataResponses != null && !sampleDataResponses.isEmpty()) {
//                            if (judgeListIsAllNull(sampleDataResponses, resultValues, resultValuesResult))
//                                return new SampleDataResponse();
//                            for (int i = 0; i < sampleDataResponses.size(); i++) {
//                                Double stdCoalCoeff = mapEnergy.get(typeIds.get(i));
//                                Double value = resultValuesResult.get(i);
//                                double result = stdCoalCoeff * value;
//                                results.add(result);
//                            }
//                            // 替换公式中的?号
//                            String expression = StringUtils.replaceEach(formula, results);
//                            // 计算公式
//                            String value = MathUtil.cal(expression);
//                            SampleDataResponse sampleDataResponse = new SampleDataResponse(pointId, sampleDataResponses.get(0).getTimestamp(), Double.valueOf(value));
//                            return sampleDataResponse;
//                        }
//                    } catch (ScriptException e) {
//                        log.error("查询最新值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleDataResponse> sampleDataResponses = getLastSimplePointData(body, points);
//                        String formula = pointId.split("#")[1];
//
//                        List<Double> resultValues = new ArrayList<>();
//                        List<Double> resultValuesResult = new ArrayList<Double>();
//                        if (sampleDataResponses != null && !sampleDataResponses.isEmpty()) {
//                            if (judgeListIsAllNull(sampleDataResponses, resultValues, resultValuesResult))
//                                return new SampleDataResponse();
//
//                            // 替换公式中的?号
//                            String expression = StringUtils.replaceEach(formula, resultValuesResult);
//                            // 计算公式
//                            String value = MathUtil.cal(expression);
//                            SampleDataResponse sampleDataResponse = new SampleDataResponse(pointId, sampleDataResponses.get(0).getTimestamp(), Double.valueOf(value));
//                            return sampleDataResponse;
//                        }
//                    } catch (ScriptException e) {
//                        log.error("查询最新值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleDataResponse> sampleDataResponses = getLastSimplePointData(body, Lists.newArrayList(dataSource));
//                    if (sampleDataResponses != null && !sampleDataResponses.isEmpty()) {
//                        SampleDataResponse sampleDataResponse = sampleDataResponses.get(0);
//                        Double value = sampleDataResponse.getValue();
//                        if (value != null) {
//                            Double result = value * stdCoalCoeff;
//                            sampleDataResponse.setValue(result);
//                        }
//                        return sampleDataResponse;
//                    }
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleDataResponse> sampleDataResponses = getLastSimplePointData(body, Lists.newArrayList(pointId));
//                    if (sampleDataResponses != null && !sampleDataResponses.isEmpty()) {
//                        return sampleDataResponses.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new SampleDataResponse(pointId, e.getMessage(), "FAIL", null);
//        }
//        return new SampleDataResponse();
//    }
//
//    /**
//     * @param pointIds 支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @return
//     * @apiNote 查询测点集合最新值
//     */
//    public List<SampleDataResponse> getPointLastData(List<String> pointIds) {
//        List<SampleDataResponse> sampleDataResponses = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleDataResponse pointLastData = getPointLastData(pointId);
//            sampleDataResponses.add(pointLastData);
//        }
//        return sampleDataResponses;
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
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, dataSources, startTime, endTime, interval, unit);
//                        if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//                            SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(sampleData4KairosResps);
//                            if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                        }
//                        getSampleData4KairosRespGSXS(sampleData, typeIds, formula, sampleData4KairosResps);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, points, startTime, endTime, interval, unit);
//                        if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//                            SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(sampleData4KairosResps);
//                            if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                        }
//
//                        String formula = pointId.split("#")[1];
//                        getSampleData4KairosRespGS(sampleData, sampleData4KairosResps, formula);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, Lists.newArrayList(dataSource), startTime, endTime, interval, unit);
//                    if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//                        SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(sampleData4KairosResps);
//                        if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                    }
//
//                    SampleData4KairosResp sampleData4KairosResp = getSampleData4KairosResp(stdCoalCoeff, sampleData4KairosResps);
//                    if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleData4KairosResp> hisSimplePointData = getHisSimplePointData(body, Lists.newArrayList(pointId), startTime, endTime, interval, unit);
//                    if (hisSimplePointData != null && !hisSimplePointData.isEmpty()) {
//                        SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(hisSimplePointData);
//                        if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                        return hisSimplePointData.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new SampleData4KairosResp(pointId, e.getMessage(), "FAIL", null, null);
//        }
//        return new SampleData4KairosResp();
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
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleData4KairosResp sampleData4KairosResp = queryHis(pointId, startTime, endTime, interval, unit);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//        }
//        return sampleData4KairosResps;
//    }
//
//    private void getSampleData4KairosRespGSXS(SampleData4KairosResp sampleData, List<String> typeIds, String formula, List<SampleData4KairosResp> sampleData4KairosResps) throws ScriptException {
//        Multimap<Long, Double> multimap = ArrayListMultimap.create();
//        if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//            List<Long> timestamps = sampleData4KairosResps.get(0).getTimestamps();
//            sampleData.setTimestamps(timestamps);
//            for (int i = 0; i < timestamps.size(); i++) {
//                for (int j = 0; j < sampleData4KairosResps.size(); j++) {
//                    List<Double> values = sampleData4KairosResps.get(j).getValues();
//                    Double stdCoalCoeff = mapEnergy.get(typeIds.get(j));
//                    Double v = null;
//                    if (values.get(i) != null) {
//                        v = values.get(i) * stdCoalCoeff;
//                    }
//                    multimap.put(timestamps.get(i), v);
//                }
//            }
//            log.info("multimap={}", multimap);
//            Map<Long, Double> map = new HashMap<>();
//            for (Long time : multimap.keySet()) {
//                List<Double> resultValuesResult = new ArrayList<>();
//                List<Double> resultValues = (List<Double>) multimap.get(time);
//                boolean flag = judge2(resultValues, resultValuesResult);
//                if (flag) {
//                    // true 为resultValues全部都是null
//                    map.put(time, null);
//                } else {
//                    // 替换公式中的?号
//                    String expression = StringUtils.replaceEach(formula, resultValuesResult);
//                    // 计算公式
//                    String value = MathUtil.cal(expression);
//                    map.put(time, Double.valueOf(value));
//                }
//            }
//            List<Double> valueAll = new ArrayList<>();
//            for (Long time : timestamps) {
//                Double value = map.get(time);
//                valueAll.add(value);
//            }
//            sampleData.setValues(valueAll);
//        }
//    }
//
//    private void getSampleData4KairosResp(SampleData4KairosResp sampleData, List<SampleData4KairosResp> sampleData4KairosResps, String formula) throws ScriptException {
//        Multimap<Long, Double> multimap = ArrayListMultimap.create();
//        if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//            List<Long> timestamps = sampleData4KairosResps.get(0).getTimestamps();
//            sampleData.setTimestamps(timestamps);
//            for (int i = 0; i < timestamps.size(); i++) {
//                for (SampleData4KairosResp sampleData4KairosResp : sampleData4KairosResps) {
//                    List<Double> values = sampleData4KairosResp.getValues();
//                    multimap.put(timestamps.get(i), values.get(i));
//                }
//            }
//            log.info("multimap={}", multimap);
//            Map<Long, Double> map = new HashMap<>();
//            for (Long time : multimap.keySet()) {
//                List<Double> resultValuesResult = new ArrayList<>();
//                List<Double> resultValues = (List<Double>) multimap.get(time);
//                boolean flag = judge2(resultValues, resultValuesResult);
//                if (flag) {
//                    // true 为resultValues全部都是null
//                    map.put(time, null);
//                } else {
//                    // 替换公式中的?号
//                    String expression = StringUtils.replaceEach(formula, resultValuesResult);
//                    // 计算公式
//                    String value = MathUtil.cal(expression);
//                    map.put(time, Double.valueOf(value));
//                }
//            }
//            List<Double> valueAll = new ArrayList<>();
//            for (Long time : timestamps) {
//                Double value = map.get(time);
//                valueAll.add(value);
//            }
//            sampleData.setValues(valueAll);
//        }
//    }
//
//    private void getSampleData4KairosRespGS(SampleData4KairosResp sampleData, List<SampleData4KairosResp> sampleData4KairosResps, String formula) throws ScriptException {
//        Multimap<Long, Double> multimap = ArrayListMultimap.create();
//        if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//            List<Long> timestamps = sampleData4KairosResps.get(0).getTimestamps();
//            sampleData.setTimestamps(timestamps);
//            for (int i = 0; i < timestamps.size(); i++) {
//                for (SampleData4KairosResp sampleData4KairosResp : sampleData4KairosResps) {
//                    List<Double> values = sampleData4KairosResp.getValues();
//                    multimap.put(timestamps.get(i), values.get(i));
//                }
//            }
//            log.info("multimap={}", multimap);
//            Map<Long, Double> map = new HashMap<>();
//            for (Long time : multimap.keySet()) {
//                List<Double> resultValuesResult = new ArrayList<>();
//                List<Double> resultValues = (List<Double>) multimap.get(time);
//                boolean flag = judge2(resultValues, resultValuesResult);
//                if (flag) {
//                    // true 为resultValues全部都是null
//                    map.put(time, null);
//                } else {
//                    // 替换公式中的?号
//                    String expression = StringUtils.replaceEach(formula, resultValuesResult);
//                    // 计算公式
//                    String value = MathUtil.cal(expression);
//                    map.put(time, Double.valueOf(value));
//                }
//            }
//            List<Double> valueAll = new ArrayList<>();
//            for (Long time : timestamps) {
//                Double value = map.get(time);
//                valueAll.add(value);
//            }
//            sampleData.setValues(valueAll);
//        }
//    }
//
//    private SampleData4KairosResp getSampleData4KairosResp(Double stdCoalCoeff, List<SampleData4KairosResp> sampleData4KairosResps) {
//        if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//            SampleData4KairosResp sampleData4KairosResp = sampleData4KairosResps.get(0);
//            List<Double> values = sampleData4KairosResp.getValues();
//            List<Double> valuesNew = new ArrayList<>();
//            if (values != null && !values.isEmpty()) {
//                for (Double value : values) {
//                    Double result = null;
//                    if (value != null) {
//                        result = value * stdCoalCoeff;
//                    }
//                    valuesNew.add(result);
//                }
//                sampleData4KairosResp.setValues(valuesNew);
//            }
//            return sampleData4KairosResp;
//        }
//        return null;
//    }
//
//
//    /**
//     * @param sampleDataResponses
//     * @param resultValues
//     * @param resultValuesResult
//     * @return
//     * @apiNote 判断查询出来的list里面的值是不是都为null，如果都为null则返回null，否则null转换为0.00
//     */
//    private boolean judgeListIsAllNull(List<SampleDataResponse> sampleDataResponses, List<Double> resultValues, List<Double> resultValuesResult) {
//        for (SampleDataResponse sampleDataResponse : sampleDataResponses) {
//            Double value = sampleDataResponse.getValue();
//            resultValues.add(value);
//        }
//        return judge2(resultValues, resultValuesResult);
//    }
//
//    private boolean judge2(List<Double> resultValues, List<Double> resultValuesResult) {
//        Boolean flag = true;
//        for (Double resultValue : resultValues) {
//            if (resultValue != null) {
//                flag = false;
//            }
//        }
//        if (flag) {
//            return true;
//        } else {
//            for (Double resultValue : resultValues) {
//                if (resultValue == null) {
//                    resultValuesResult.add(0.00);
//                } else {
//                    resultValuesResult.add(resultValue);
//                }
//            }
//        }
//        return false;
//    }
//
//    private List<SampleData4KairosResp> getHisSimplePointData(QueryBody body, List<String> points, Long start, Long end) {
//        body.setPoints(points);
//        body.setStartTime(start);
//        body.setEndTime(end);
//        HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//        JSONArray jsonArray = restTemplate.postForEntity(urlPrefix() + "/querySampleData", httpEntity, JSONArray.class).getBody();
//        return jsonArray.toJavaList(SampleData4KairosResp.class);
//    }
//
//
//    private List<SampleDataResponse> getLastSimplePointData(QueryBody body, List<String> points) {
//        body.setPoints(points);
//        HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//        JSONArray jsonArray = restTemplate.postForEntity(urlPrefix() + "/queryLastSampleData", httpEntity, JSONArray.class).getBody();
//        return jsonArray.toJavaList(SampleDataResponse.class);
//    }
//
//    private List<SampleData4KairosResp> getHisSimplePointData(QueryBody body, List<String> points, Long start, Long end, Integer interval, TimeUnit unit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        body.setPoints(points);
//        body.setStartTime(start);
//        body.setEndTime(end);
//        body.setInterval(interval);
//        body.setUnit(unit.toString());
//        try {
//            HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//            JSONObject jsonObject = restTemplate.postForEntity(urlPrefix() + "/queryHisData", httpEntity, JSONObject.class).getBody();
//            JSONArray values = jsonObject.getJSONArray("values");
//            JSONArray timestamps = jsonObject.getJSONArray("timestamps");
//            List<Long> longs = timestamps.toJavaList(Long.class);
//            List<DataPointResult> dataPointResults = values.toJavaList(DataPointResult.class);
//            for (DataPointResult dataPointResult : dataPointResults) {
//                SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//                sampleData4KairosResp.setTimestamps(longs);
//                sampleData4KairosResp.setMsg("SUCCESS");
//                sampleData4KairosResp.setPoint(dataPointResult.getMetricName());
//                sampleData4KairosResp.setValues(dataPointResult.getValues());
//                sampleData4KairosResps.add(sampleData4KairosResp);
//            }
//        } catch (RestClientException e) {
//            SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//            sampleData4KairosResp.setTimestamps(null);
//            sampleData4KairosResp.setMsg(e.getMessage());
//            sampleData4KairosResp.setOpResult("FAIL");
//            sampleData4KairosResp.setPoint(JSONArray.toJSONString(points));
//            sampleData4KairosResp.setValues(null);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//            e.printStackTrace();
//        }
//        return sampleData4KairosResps;
//    }
//
//    public SampleData4KairosResp queryMax(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleData4KairosResp> maxSimplePointData = getMaxSimplePointData(body, dataSources, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                        if (maxSimplePointData != null && !maxSimplePointData.isEmpty()) {
//                            SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(maxSimplePointData);
//                            if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                        }
//                        getSampleData4KairosRespGSXS(sampleData, typeIds, formula, maxSimplePointData);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleData4KairosResp> maxSimplePointData = getMaxSimplePointData(body, points, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                        if (maxSimplePointData != null && !maxSimplePointData.isEmpty()) {
//                            SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(maxSimplePointData);
//                            if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                        }
//                        String formula = pointId.split("#")[1];
//                        getSampleData4KairosRespGS(sampleData, maxSimplePointData, formula);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleData4KairosResp> maxSimplePointData = getMaxSimplePointData(body, Lists.newArrayList(dataSource), startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                    if (maxSimplePointData != null && !maxSimplePointData.isEmpty()) {
//                        SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(maxSimplePointData);
//                        if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                    }
//                    SampleData4KairosResp sampleData4KairosResp = getSampleData4KairosResp(stdCoalCoeff, maxSimplePointData);
//                    if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleData4KairosResp> maxSimplePointData = getMaxSimplePointData(body, Lists.newArrayList(pointId), startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                    if (maxSimplePointData != null && !maxSimplePointData.isEmpty()) {
//                        SampleData4KairosResp sampleData4KairosResp = judgeIsSuccess(maxSimplePointData);
//                        if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                        return maxSimplePointData.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new SampleData4KairosResp(pointId, e.getMessage(), "FAIL", null, null);
//        }
//        return new SampleData4KairosResp();
//    }
//
//    public List<SampleData4KairosResp> queryMax(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleData4KairosResp sampleData4KairosResp = queryMax(pointId, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//        }
//        return sampleData4KairosResps;
//    }
//
//    private List<SampleData4KairosResp> getMaxSimplePointData(QueryBody body, List<String> points, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        body.setPoints(points);
//        body.setStartTime(startTime);
//        body.setEndTime(endTime);
//        body.setInterval(interval);
//        body.setUnit(unit.toString());
//        body.setAggregationInterval(aggregationInterval);
//        body.setAggregationUnit(aggregationUnit.toString());
//        try {
//            HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//            JSONObject jsonObject = restTemplate.postForEntity(urlPrefix() + "/queryMaxData", httpEntity, JSONObject.class).getBody();
//            JSONArray values = jsonObject.getJSONArray("values");
//            JSONArray timestamps = jsonObject.getJSONArray("timestamps");
//            List<Long> longs = timestamps.toJavaList(Long.class);
//            List<DataPointResult> dataPointResults = values.toJavaList(DataPointResult.class);
//            for (DataPointResult dataPointResult : dataPointResults) {
//                SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//                sampleData4KairosResp.setTimestamps(longs);
//                sampleData4KairosResp.setMsg("查询成功");
//                sampleData4KairosResp.setOpResult("SUCCESS");
//                sampleData4KairosResp.setPoint(dataPointResult.getMetricName());
//                sampleData4KairosResp.setValues(dataPointResult.getValues());
//                sampleData4KairosResps.add(sampleData4KairosResp);
//            }
//        } catch (RestClientException e) {
//            SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//            sampleData4KairosResp.setTimestamps(null);
//            sampleData4KairosResp.setMsg(e.getMessage());
//            sampleData4KairosResp.setOpResult("FAIL");
//            sampleData4KairosResp.setPoint(JSONArray.toJSONString(points));
//            sampleData4KairosResp.setValues(null);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//            e.printStackTrace();
//        }
//        return sampleData4KairosResps;
//    }
//
//    public SampleData4KairosResp queryMin(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleData4KairosResp> minSimplePointData = getMinSimplePointData(body, dataSources, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                        if (minSimplePointData != null && !minSimplePointData.isEmpty()) {
//                            SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(minSimplePointData);
//                            if (minSimplePointDatum != null) return minSimplePointDatum;
//                        }
//                        getSampleData4KairosRespGSXS(sampleData, typeIds, formula, minSimplePointData);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleData4KairosResp> minSimplePointData = getMinSimplePointData(body, points, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                        if (minSimplePointData != null && !minSimplePointData.isEmpty()) {
//                            SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(minSimplePointData);
//                            if (minSimplePointDatum != null) return minSimplePointDatum;
//                        }
//                        String formula = pointId.split("#")[1];
//                        getSampleData4KairosRespGS(sampleData, minSimplePointData, formula);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleData4KairosResp> minSimplePointData = getMinSimplePointData(body, Lists.newArrayList(dataSource), startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                    if (minSimplePointData != null && !minSimplePointData.isEmpty()) {
//                        SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(minSimplePointData);
//                        if (minSimplePointDatum != null) return minSimplePointDatum;
//                    }
//                    SampleData4KairosResp sampleData4KairosResp = getSampleData4KairosResp(stdCoalCoeff, minSimplePointData);
//                    if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleData4KairosResp> minSimplePointData = getMinSimplePointData(body, Lists.newArrayList(pointId), startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                    if (minSimplePointData != null && !minSimplePointData.isEmpty()) {
//                        SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(minSimplePointData);
//                        if (minSimplePointDatum != null) return minSimplePointDatum;
//                        return minSimplePointData.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new SampleData4KairosResp(pointId, e.getMessage(), "FAIL", null, null);
//        }
//        return new SampleData4KairosResp();
//    }
//
//    private SampleData4KairosResp judgeIsSuccess(List<SampleData4KairosResp> minSimplePointData) {
//        for (SampleData4KairosResp minSimplePointDatum : minSimplePointData) {
//            if (Objects.equals(minSimplePointDatum.getOpResult(), "FAIL")) {
//                return minSimplePointDatum;
//            }
//        }
//        return null;
//    }
//
//    public List<SampleData4KairosResp> queryMin(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleData4KairosResp sampleData4KairosResp = queryMin(pointId, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//        }
//        return sampleData4KairosResps;
//    }
//
//    private List<SampleData4KairosResp> getMinSimplePointData(QueryBody body, List<String> points, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        body.setPoints(points);
//        body.setStartTime(startTime);
//        body.setEndTime(endTime);
//        body.setInterval(interval);
//        body.setUnit(unit.toString());
//        body.setAggregationInterval(aggregationInterval);
//        body.setAggregationUnit(aggregationUnit.toString());
//        try {
//            HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//            JSONObject jsonObject = restTemplate.postForEntity(urlPrefix() + "/queryMinData", httpEntity, JSONObject.class).getBody();
//            JSONArray values = jsonObject.getJSONArray("values");
//            JSONArray timestamps = jsonObject.getJSONArray("timestamps");
//            List<Long> longs = timestamps.toJavaList(Long.class);
//            List<DataPointResult> dataPointResults = values.toJavaList(DataPointResult.class);
//            for (DataPointResult dataPointResult : dataPointResults) {
//                SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//                sampleData4KairosResp.setTimestamps(longs);
//                sampleData4KairosResp.setMsg("查询成功");
//                sampleData4KairosResp.setOpResult("SUCCESS");
//                sampleData4KairosResp.setPoint(dataPointResult.getMetricName());
//                sampleData4KairosResp.setValues(dataPointResult.getValues());
//                sampleData4KairosResps.add(sampleData4KairosResp);
//            }
//        } catch (Exception e) {
//            SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//            sampleData4KairosResp.setTimestamps(null);
//            sampleData4KairosResp.setMsg(e.getMessage());
//            sampleData4KairosResp.setOpResult("FAIL");
//            sampleData4KairosResp.setPoint(JSONArray.toJSONString(points));
//            sampleData4KairosResp.setValues(null);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//            e.printStackTrace();
//        }
//        return sampleData4KairosResps;
//    }
//
//    public SampleData4KairosResp queryAvg(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleData4KairosResp> avgSimplePointData = getAvgSimplePointData(body, dataSources, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                        if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                            SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                            if (minSimplePointDatum != null) return minSimplePointDatum;
//                        }
//                        getSampleData4KairosRespGSXS(sampleData, typeIds, formula, avgSimplePointData);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleData4KairosResp> avgSimplePointData = getAvgSimplePointData(body, points, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                        if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                            SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                            if (minSimplePointDatum != null) return minSimplePointDatum;
//                        }
//                        String formula = pointId.split("#")[1];
//                        getSampleData4KairosRespGS(sampleData, avgSimplePointData, formula);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleData4KairosResp> avgSimplePointData = getAvgSimplePointData(body, Lists.newArrayList(dataSource), startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                    if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                        SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                        if (minSimplePointDatum != null) return minSimplePointDatum;
//                    }
//                    SampleData4KairosResp sampleData4KairosResp = getSampleData4KairosResp(stdCoalCoeff, avgSimplePointData);
//                    if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleData4KairosResp> avgSimplePointData = getAvgSimplePointData(body, Lists.newArrayList(pointId), startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//                    if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                        SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                        if (minSimplePointDatum != null) return minSimplePointDatum;
//                        return avgSimplePointData.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new SampleData4KairosResp(pointId, e.getMessage(), "FAIL", null, null);
//        }
//        return new SampleData4KairosResp();
//    }
//
//    public List<SampleData4KairosResp> queryAvg(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleData4KairosResp sampleData4KairosResp = queryAvg(pointId, startTime, endTime, interval, unit, aggregationInterval, aggregationUnit);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//        }
//        return sampleData4KairosResps;
//    }
//
//    private List<SampleData4KairosResp> getAvgSimplePointData(QueryBody body, List<String> points, Long startTime, Long endTime, Integer interval, TimeUnit unit, Integer aggregationInterval, TimeUnit aggregationUnit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        body.setPoints(points);
//        body.setStartTime(startTime);
//        body.setEndTime(endTime);
//        body.setInterval(interval);
//        body.setUnit(unit.toString());
//        body.setAggregationInterval(aggregationInterval);
//        body.setAggregationUnit(aggregationUnit.toString());
//        try {
//            HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//            JSONObject jsonObject = restTemplate.postForEntity(urlPrefix() + "/queryAvgData", httpEntity, JSONObject.class).getBody();
//            JSONArray values = jsonObject.getJSONArray("values");
//            JSONArray timestamps = jsonObject.getJSONArray("timestamps");
//            List<Long> longs = timestamps.toJavaList(Long.class);
//            List<DataPointResult> dataPointResults = values.toJavaList(DataPointResult.class);
//            for (DataPointResult dataPointResult : dataPointResults) {
//                SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//                sampleData4KairosResp.setTimestamps(longs);
//                sampleData4KairosResp.setMsg("查询成功");
//                sampleData4KairosResp.setOpResult("SUCCESS");
//                sampleData4KairosResp.setPoint(dataPointResult.getMetricName());
//                sampleData4KairosResp.setValues(dataPointResult.getValues());
//                sampleData4KairosResps.add(sampleData4KairosResp);
//            }
//        } catch (Exception e) {
//            SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//            sampleData4KairosResp.setTimestamps(null);
//            sampleData4KairosResp.setMsg(e.getMessage());
//            sampleData4KairosResp.setOpResult("FAIL");
//            sampleData4KairosResp.setPoint(JSONArray.toJSONString(points));
//            sampleData4KairosResp.setValues(null);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//            e.printStackTrace();
//        }
//        return sampleData4KairosResps;
//    }
//
//    public SampleData4KairosResp queryDiff(String pointId, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleData4KairosResp> avgSimplePointData = getDiffSimplePointData(body, dataSources, startTime, endTime, interval, unit);
//                        if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                            SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                            if (minSimplePointDatum != null) return minSimplePointDatum;
//                        }
//                        getSampleData4KairosRespGSXS(sampleData, typeIds, formula, avgSimplePointData);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleData4KairosResp> avgSimplePointData = getDiffSimplePointData(body, points, startTime, endTime, interval, unit);
//                        if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                            SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                            if (minSimplePointDatum != null) return minSimplePointDatum;
//                        }
//                        String formula = pointId.split("#")[1];
//                        getSampleData4KairosRespGS(sampleData, avgSimplePointData, formula);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleData4KairosResp> avgSimplePointData = getDiffSimplePointData(body, Lists.newArrayList(dataSource), startTime, endTime, interval, unit);
//                    if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                        SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                        if (minSimplePointDatum != null) return minSimplePointDatum;
//                    }
//                    SampleData4KairosResp sampleData4KairosResp = getSampleData4KairosResp(stdCoalCoeff, avgSimplePointData);
//                    if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleData4KairosResp> avgSimplePointData = getDiffSimplePointData(body, Lists.newArrayList(pointId), startTime, endTime, interval, unit);
//                    if (avgSimplePointData != null && !avgSimplePointData.isEmpty()) {
//                        SampleData4KairosResp minSimplePointDatum = judgeIsSuccess(avgSimplePointData);
//                        if (minSimplePointDatum != null) return minSimplePointDatum;
//                        return avgSimplePointData.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new SampleData4KairosResp(pointId, e.getMessage(), "FAIL", null, null);
//        }
//        return new SampleData4KairosResp();
//    }
//
//    public List<SampleData4KairosResp> queryDiff(List<String> pointIds, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleData4KairosResp sampleData4KairosResp = queryDiff(pointId, startTime, endTime, interval, unit);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//        }
//        return sampleData4KairosResps;
//    }
//
//    private List<SampleData4KairosResp> getDiffSimplePointData(QueryBody body, List<String> points, Long startTime, Long endTime, Integer interval, TimeUnit unit) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        body.setPoints(points);
//        body.setStartTime(startTime);
//        body.setEndTime(endTime);
//        body.setInterval(interval);
//        body.setUnit(unit.toString());
//        try {
//            HttpEntity<QueryBody> httpEntity = new HttpEntity<>(body);
//            JSONObject jsonObject = restTemplate.postForEntity(urlPrefix() + "/queryDiffData", httpEntity, JSONObject.class).getBody();
//            JSONArray values = jsonObject.getJSONArray("values");
//            JSONArray timestamps = jsonObject.getJSONArray("timestamps");
//            List<Long> longs = timestamps.toJavaList(Long.class);
//            List<DataPointResult> dataPointResults = values.toJavaList(DataPointResult.class);
//            for (DataPointResult dataPointResult : dataPointResults) {
//                SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//                sampleData4KairosResp.setTimestamps(longs);
//                sampleData4KairosResp.setMsg("查询成功");
//                sampleData4KairosResp.setOpResult("SUCCESS");
//                sampleData4KairosResp.setPoint(dataPointResult.getMetricName());
//                sampleData4KairosResp.setValues(dataPointResult.getValues());
//                sampleData4KairosResps.add(sampleData4KairosResp);
//            }
//        } catch (Exception e) {
//            SampleData4KairosResp sampleData4KairosResp = new SampleData4KairosResp();
//            sampleData4KairosResp.setTimestamps(null);
//            sampleData4KairosResp.setMsg(e.getMessage());
//            sampleData4KairosResp.setOpResult("FAIL");
//            sampleData4KairosResp.setPoint(JSONArray.toJSONString(points));
//            sampleData4KairosResp.setValues(null);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//            e.printStackTrace();
//        }
//        return sampleData4KairosResps;
//    }
//
//
//    /**
//     * @param pointId   支持普通测点、公式、带系数的普通测点，带系数的公式
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @apiNote 查询单测点在同一时间段内的样本数据
//     */
//    public SampleData4KairosResp queryHis(String pointId, Long startTime, Long endTime) {
//        try {
//            QueryBody body = new QueryBody();
//            // 公式
//            if (pointId.contains(FORMULA)) {
//                if (pointId.indexOf(RATIO) > 0) {
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        // 带系数的公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity],LYGSHCYJD.RTHGCC.S2-2.Flowrate_work[water]#?+?"
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<String> dataSources = new ArrayList<>();
//                        List<String> typeIds = new ArrayList<>();
//                        String formula = pointId.split("#")[1];
//                        for (String point : points) {
//                            String[] source = point.split("\\[");
//                            String dataSource = source[0];
//                            String typeId = source[1].split("\\]")[0];
//                            dataSources.add(dataSource);
//                            typeIds.add(typeId);
//                        }
//                        List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, dataSources, startTime, endTime);
//                        getSampleData4KairosRespGSXS(sampleData, typeIds, formula, sampleData4KairosResps);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值带系数的公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                } else {
//                    // 普通公式 "LYGSHCYJD.DGWSCL.E1-1-18.Ua,LYGSHCYJD.RTHGCC.S2-2.Flowrate_work#?+?"
//                    SampleData4KairosResp sampleData = new SampleData4KairosResp();
//                    sampleData.setPoint(pointId);
//                    try {
//                        List<String> points = Arrays.asList(pointId.split("#")[0].split(","));
//                        List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, points, startTime, endTime);
//                        String formula = pointId.split("#")[1];
//                        getSampleData4KairosResp(sampleData, sampleData4KairosResps, formula);
//                        sampleData.setMsg("SUCCESS");
//                        return sampleData;
//                    } catch (ScriptException e) {
//                        log.error("查询历史值公式配置错误={},e={}", pointId, e.getMessage());
//                        throw e;
//                    }
//                }
//            } else {
//                // 带系数的普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua[electricity]
//                if (pointId.indexOf(RATIO) > 0) {
//                    String[] source = pointId.split("\\[");
//                    String dataSource = source[0];
//                    String typeId = source[1].split("\\]")[0];
//                    Double stdCoalCoeff = mapEnergy.get(typeId);
//
//                    List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, Lists.newArrayList(dataSource), startTime, endTime);
//                    SampleData4KairosResp sampleData4KairosResp = getSampleData4KairosResp(stdCoalCoeff, sampleData4KairosResps);
//                    if (sampleData4KairosResp != null) return sampleData4KairosResp;
//                } else {
//                    // 普通测点 LYGSHCYJD.DGWSCL.E1-1-18.Ua
//                    List<SampleData4KairosResp> sampleData4KairosResps = getHisSimplePointData(body, Lists.newArrayList(pointId), startTime, endTime);
//                    if (sampleData4KairosResps != null && !sampleData4KairosResps.isEmpty()) {
//                        return sampleData4KairosResps.get(0);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new SampleData4KairosResp();
//    }
//
//    /**
//     * @param pointIds  支持普通测点集合、公式集合、带系数的普通测点集合，带系数的公式集合
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @return
//     * @apiNote 查询多测点在同一时间段内的样本数据
//     */
//    public List<SampleData4KairosResp> queryHis(List<String> pointIds, Long startTime, Long endTime) {
//        List<SampleData4KairosResp> sampleData4KairosResps = new ArrayList<>();
//        for (String pointId : pointIds) {
//            SampleData4KairosResp sampleData4KairosResp = queryHis(pointId, startTime, endTime);
//            sampleData4KairosResps.add(sampleData4KairosResp);
//        }
//        return sampleData4KairosResps;
//    }
//
//}