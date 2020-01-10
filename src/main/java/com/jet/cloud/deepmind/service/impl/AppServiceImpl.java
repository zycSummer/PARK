package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.AppResult;
import com.jet.cloud.deepmind.common.ResultType;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.MathUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.DataPointResult;
import com.jet.cloud.deepmind.rtdb.model.SampleData4KairosResp;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.AppService;
import com.jet.cloud.deepmind.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhuyicheng
 * @create 2019/12/23 10:40
 * @desc AppServiceImpl
 */
@Service
public class AppServiceImpl implements AppService {
    private static final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

    @Autowired
    @Qualifier(value = "alarmRestTemplate")
    private RestTemplate restTemplate;
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.session.timeout}")
    private String timeout;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private LogService logService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserGroupMappingObjRepo userGroupMappingObjRepo;
    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private SiteRepo siteRepo;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;
    @Autowired
    private DataSourceRepo dataSourceRepo;
    @Autowired
    private KairosdbClient kairosdbClient;
    @Autowired
    private MeterRepo meterRepo;
    @Autowired
    private NoticeRepo noticeRepo;
    @Autowired
    private AlarmMsgRepo alarmMsgRepo;

    private Map<String, AppVO> appVOMap = new ConcurrentHashMap<>();

    @Override
    public AppResult login(String userId, String password, HttpServletRequest req) {
        AppResult result = null;
        try {
            SysLog log = new SysLog();
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isNullOrEmpty(userId, password)) {
                log.setResult("FAIL");
                result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
            } else {
                HttpHeaders headers = new HttpHeaders();
                // 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("username", userId);
                params.add("password", password);
                params.add("userGroup", "jet");

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
                        params, headers);
                //发送请求，设置请求返回数据格式为String
                ResponseEntity<Response> response = restTemplate.postForEntity("http://localhost:" + port + "/login", request, Response.class);
                int statusCodeValue = response.getStatusCodeValue();
                if (statusCodeValue == 200) {
                    Response body = response.getBody();
                    int code = body.getCode();
                    String msg = body.getMsg();
                    if (code == 0) {
                        UUID userKey = UUID.randomUUID();
                        long time = System.currentTimeMillis();
                        String s = userId + time + userKey;
                        String hex = DigestUtils.md5DigestAsHex(s.getBytes());
                        String token = StringUtils.encode(hex.getBytes());

                        AppVO appVO = new AppVO();
                        appVO.setUserId(userId);
                        appVO.setUserKey(userKey.toString());
                        appVO.setToken(token);
                        String minute = timeout.substring(0, timeout.length() - 1);
                        appVO.setTime(time + Integer.valueOf(minute) * 60 * 1000);
                        appVOMap.put(token, appVO);

                        map.put("userKey", userKey);
                        map.put("token", token);
                        userRepo.updateLastLoginTimeAndIp(userId, LocalDateTime.now(), CommonUtil.getIpAddr(req));
                        log.setResult("SUCCESS");
                        logger.info("App----userKey={},token={}", userKey, token);
                        result = new AppResult(ResultType.SUCCESS.getCode(), null, map);
                    } else if (code == 1 && ("用户名或密码错误".equals(msg) || "系统无此用户".equals(msg))) {
                        log.setResult("FAIL");
                        result = new AppResult(ResultType.WRONG_ACCOUNT_OR_PASSWORD.getCode(), ResultType.WRONG_ACCOUNT_OR_PASSWORD.getMsg(), null);
                    } else if (code == 1 && "此用户被禁止使用".equals(msg)) {
                        log.setResult("FAIL");
                        result = new AppResult(ResultType.ACCOUNT_IS_DISABLED.getCode(), ResultType.ACCOUNT_IS_DISABLED.getMsg(), null);
                    } else if (code == 1 && "此用户被锁定".equals(msg)) {
                        log.setResult("FAIL");
                        result = new AppResult(ResultType.ACCOUNT_IS_LOCKED.getCode(), ResultType.ACCOUNT_IS_LOCKED.getMsg(), null);
                    } else if (code == 1 && "此用户已过期".equals(msg)) {
                        log.setResult("FAIL");
                        result = new AppResult(ResultType.ACCOUNT_IS_EXPIRED.getCode(), ResultType.ACCOUNT_IS_EXPIRED.getMsg(), null);
                    }
                }
            }
            log.setUserId(userId);
            log.setOperateIp(CommonUtil.getIpAddr(req));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(req.getRequestURI());
            log.setMethod(req.getMethod());
            log.setMenu("[APP]登录");
            log.setFunction("登录");
            log.setOperateContent(userId + "_" + password);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult logout(HttpServletRequest request) {
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            SysLog log = new SysLog();
            if (result.getCode() == 0) {
                appVOMap.remove(token);
                log.setResult("SUCCESS");
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]退出");
            log.setFunction("退出");
            log.setOperateContent(null);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    private void judgeCode(AppResult result, SysLog log) {
        if (result.getCode() == 102) {
            log.setResult("FAIL");
        }
        if (result.getCode() == 103) {
            log.setResult("FAIL");
        }
        if (result.getCode() == 104) {
            log.setResult("FAIL");
        }
        if (result.getCode() == 105) {
            log.setResult("FAIL");
        }
        if (result.getCode() == 106) {
            log.setResult("FAIL");
        }
        if (result.getCode() == 107) {
            log.setResult("FAIL");
        }
    }

    @Override
    public AppResult changePwd(String userId, String oldPassword, String newPassword, HttpServletRequest request) {
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            SysLog log = new SysLog();
            if (result.getCode() == 0) {
                if (StringUtils.isNullOrEmpty(userId, oldPassword, newPassword)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else {
                    SysUser user = userRepo.findByUserId(userId);
                    if (user == null) {
                        result = new AppResult(ResultType.WRONG_ACCOUNT_OR_PASSWORD.getCode(), ResultType.WRONG_ACCOUNT_OR_PASSWORD.getMsg(), null);
                    } else {
                        boolean enabled = user.isEnabled();// Y启用,N禁用
                        boolean locked = user.isLocked();  // Y锁定,N解锁
                        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                            log.setResult("FAIL");
                            result = new AppResult(ResultType.WRONG_ACCOUNT_OR_PASSWORD.getCode(), ResultType.WRONG_ACCOUNT_OR_PASSWORD.getMsg(), null);
                        } else if (!enabled) {
                            log.setResult("FAIL");
                            result = new AppResult(ResultType.ACCOUNT_IS_DISABLED.getCode(), ResultType.ACCOUNT_IS_DISABLED.getMsg(), null);
                        } else if (locked) {
                            log.setResult("FAIL");
                            result = new AppResult(ResultType.ACCOUNT_IS_LOCKED.getCode(), ResultType.ACCOUNT_IS_LOCKED.getMsg(), null);
                        } else {
                            user.setPassword(passwordEncoder.encode(newPassword));
                            user.setUpdateNow();
                            user.setUpdateUserId(userId);
                            userRepo.save(user);
                            log.setResult("SUCCESS");
                            result = new AppResult(ResultType.SUCCESS.getCode(), null, null);
                        }
                    }
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]修改密码");
            log.setFunction("修改密码");
            log.setOperateContent(userId + "_" + oldPassword + "_" + newPassword);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult objList(HttpServletRequest request) {
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            SysLog log = new SysLog();
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            AppObjListVO appObjListVO = new AppObjListVO();
            if (result.getCode() == 0) {
                String userId = appVO.getUserId();
                SysUser sysUser = userRepo.findByUserId(userId);
                String userGroupId = sysUser.getUserGroupId();
                List<UserGroupMappingObj> userGroupMappingObjs = userGroupMappingObjRepo.findByUserGroupId(userGroupId);
                if (StringUtils.isNotNullAndEmpty(userGroupMappingObjs)) {
                    List<Park> parks = parkRepo.findAll();
                    Map<String, String> mapPark = new HashMap<>();
                    if (StringUtils.isNotNullAndEmpty(parks)) {
                        for (Park park : parks) {
                            mapPark.put(park.getParkId(), park.getParkName());
                        }
                    }
                    List<Site> sites = siteRepo.findAll();
                    Map<String, String> mapSite = new HashMap<>();
                    if (StringUtils.isNotNullAndEmpty(parks)) {
                        for (Site site : sites) {
                            mapSite.put(site.getSiteId(), site.getSiteName());
                        }
                    }
                    AppParkVO parkVO = new AppParkVO();
                    List<AppSiteVO> siteVOS = new ArrayList<>();
                    for (UserGroupMappingObj userGroupMappingObj : userGroupMappingObjs) {
                        String objType = userGroupMappingObj.getObjType();
                        String objId = userGroupMappingObj.getObjId();
                        if ("PARK".equals(objType)) {
                            parkVO.setParkId(objId);
                            if (mapPark != null) {
                                parkVO.setParkName(mapPark.get(objId));
                            }
                        }
                        if ("SITE".equals(objType)) {
                            AppSiteVO siteVO = new AppSiteVO();
                            siteVO.setSiteId(objId);
                            if (mapSite != null) {
                                siteVO.setSiteName(mapSite.get(objId));
                            }
                            siteVOS.add(siteVO);
                        }
                    }
                    appObjListVO.setPark(parkVO);
                    appObjListVO.setSite(siteVOS);
                }
                log.setResult("SUCCESS");
                result = new AppResult(ResultType.SUCCESS.getCode(), null, appObjListVO);
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]获取对象列表");
            log.setFunction("获取对象列表");
            log.setOperateContent(null);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult objEnergySummary(String objType, String objId, String timeType, Long timeValue, HttpServletRequest request) {
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            SysLog log = new SysLog();
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            AppEnergySummaryVO appEnergySummaryVO = new AppEnergySummaryVO();
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);

                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId, timeType, timeType, timeValue)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (!Objects.equals("DAY", timeType) && !Objects.equals("MONTH", timeType) && !Objects.equals("YEAR", timeType)) {
                    // 检查timeType的值是否为DAY、MONTH、YEAR中的一种，否则返回响应代码110
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_TIMETYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_TIMETYPE_INVALID.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    TimeVO timeVO = time(timeType, timeValue);
                    /**
                     * 3.1 找出系统上所配置的 标煤 对应的用量参数标识和用量参数标识单位（单位在tb_sys_energy_para表中取）
                     * SELECT energy_type_id, energy_type_name, std_coal_coeff, energy_usage_para_id FROM tb_sys_energy_type
                     * WHERE energy_type_id='std_coal' ORDER BY sort_id;
                     */
                    SysEnergyType sysEnergyType = sysEnergyTypeRepo.findByEnergyTypeIdOrderBySortId("std_coal");
                    TotalVO totalVO = new TotalVO();
                    totalVO.setName("总能耗");
                    if (sysEnergyType != null) {
                        queryTotal(objType, objId, timeVO, sysEnergyType, totalVO);
                    }
                    appEnergySummaryVO.setTotalVO(totalVO);

                    /**
                     * 4.1 找出系统上所配置的能源种类（排除标煤）及对应的用量参数标识和用量参数名称和单位（单位在tb_sys_energy_para表中取）
                     * SELECT energy_type_id, energy_type_name, std_coal_coeff, energy_usage_para_id FROM tb_sys_energy_type WHERE
                     * energy_type_id<>'std_coal' ORDER BY sort_id;
                     */
                    List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAllByEnergyTypeIdNot("std_coal");
                    List<ItemVO> itemVOS = new ArrayList<>();
                    Map<String, String> pointMap = new HashMap<>();
                    Map<String, Double> stdMap = new HashMap<>();
                    if (StringUtils.isNotNullAndEmpty(sysEnergyTypes)) {
                        for (SysEnergyType energyType : sysEnergyTypes) {
                            String energyTypeId = energyType.getEnergyTypeId();
                            String energyUsageParaId = energyType.getEnergyUsageParaId();
                            Double stdCoalCoeff = energyType.getStdCoalCoeff();
                            stdMap.put(energyTypeId, stdCoalCoeff);
                            DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, energyTypeId, energyUsageParaId);
                            if (dataSource != null) {
                                String pointId = dataSource.getDataSource();
                                if (StringUtils.isNotNullAndEmpty(pointId)) {
                                    pointMap.put(objType + objId + energyTypeId + energyUsageParaId, pointId);
                                }
                            }
                        }
                        Collection<String> values = pointMap.values();
                        List<String> pointIds = new ArrayList<>(values);
                        AggregatorDataResponse queryDiff = kairosdbClient.queryDiff(pointIds, timeVO.getStartTime(), timeVO.getEndTime(), 1, timeVO.getTimeUnit());
                        List<DataPointResult> dataPointResults = queryDiff.getValues();
                        Map<String, Double> stdCoalValueMap = new HashMap<>();
                        Map<String, Double> stdCoalPpercentMap = new HashMap<>();
                        Map<String, Double> valueMap = new HashMap<>();
                        Double sum = 0d;
                        for (DataPointResult dataPointResult : dataPointResults) {
                            List<Double> resultValues = dataPointResult.getValues();
                            if (resultValues != null && !resultValues.isEmpty()) {
                                Double value = resultValues.get(0);
                                String metricName = dataPointResult.getMetricName();
                                for (SysEnergyType energyType : sysEnergyTypes) {
                                    String energyTypeId = energyType.getEnergyTypeId();
                                    String energyUsageParaId = energyType.getEnergyUsageParaId();
                                    String pointId = pointMap.get(objType + objId + energyTypeId + energyUsageParaId);
                                    if (Objects.equals(pointId, metricName)) {
                                        valueMap.put(objType + objId + energyTypeId + energyUsageParaId + "@@", value);
                                        Double std = stdMap.get(energyTypeId);
                                        if (std != null && value != null) {
                                            Double stdCoalValue = value * std;
                                            sum += stdCoalValue;
                                            stdCoalValueMap.put(objType + objId + energyTypeId + energyUsageParaId + "##", stdCoalValue);
                                        } else {
                                            stdCoalValueMap.put(objType + objId + energyTypeId + energyUsageParaId + "##", null);
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                        for (String pointId : stdCoalValueMap.keySet()) {
                            Double stdCoalPpercent = null;
                            Double value = stdCoalValueMap.get(pointId);
                            if (value != null && sum != 0d) {
                                stdCoalPpercent = value / sum * 100;
                            }
                            stdCoalPpercentMap.put(pointId + "@", stdCoalPpercent);
                        }
                        for (SysEnergyType energyType : sysEnergyTypes) {
                            ItemVO itemVO = new ItemVO();
                            String energyTypeId = energyType.getEnergyTypeId();
                            String energyTypeName = energyType.getEnergyTypeName();
                            String energyUsageParaId = energyType.getEnergyUsageParaId();
                            SysEnergyPara sysEnergyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyUsageParaId);
                            String unit = sysEnergyPara.getUnit();
                            itemVO.setUnit(unit);
                            itemVO.setName(energyTypeName);
                            Double value = valueMap.get(objType + objId + energyTypeId + energyUsageParaId + "@@");
                            if (value != null) {
                                Double valueOf = Double.valueOf(MathUtil.double2String(value));
                                itemVO.setValue(valueOf);
                            } else {
                                itemVO.setValue(null);
                            }
                            Double stdCoalValue = stdCoalValueMap.get(objType + objId + energyTypeId + energyUsageParaId + "##");
                            if (stdCoalValue != null) {
                                Double valueOf = Double.valueOf(MathUtil.double2String(stdCoalValue));
                                itemVO.setStdCoalValue(valueOf);
                            } else {
                                itemVO.setStdCoalValue(null);
                            }
                            Double stdCoalPpercent = stdCoalPpercentMap.get(objType + objId + energyTypeId + energyUsageParaId + "##@");
                            if (stdCoalPpercent != null) {
                                Double valueOf = Double.valueOf(MathUtil.double2String(stdCoalPpercent));
                                itemVO.setStdCoalPpercent(valueOf);
                            } else {
                                itemVO.setStdCoalPpercent(null);
                            }
                            itemVOS.add(itemVO);
                        }
                    }
                    appEnergySummaryVO.setItem(itemVOS);
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, appEnergySummaryVO);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]能耗概况");
            log.setFunction("获取对象能耗概况数据");
            log.setOperateContent(objType + "_" + objId + "_" + timeType + "_" + timeValue);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult objEnergyRank(String objType, String objId, String energyType, String timeType, Long timeValue, HttpServletRequest request) {
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            SysLog log = new SysLog();
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            EnergyRankVO energyRankVO = new EnergyRankVO();
            List<RankVO> rankVOS = new ArrayList<>();
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);

                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId, energyType, timeType, timeValue)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (!Objects.equals("DAY", timeType) && !Objects.equals("MONTH", timeType) && !Objects.equals("YEAR", timeType)) {
                    // 检查timeType的值是否为DAY、MONTH、YEAR中的一种，否则返回响应代码110
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_TIMETYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_TIMETYPE_INVALID.getMsg(), null);
                } else if (!Objects.equals("STD_COAL", energyType) && !Objects.equals("ELECTRICITY", energyType) && !Objects.equals("WATER", energyType)
                        && !Objects.equals("STEAM", energyType) && !Objects.equals("COAL", energyType) && !Objects.equals("OIL ", energyType)) {
                    // 检查energyType的值是否为STD_COAL、ELECTRICITY、WATER、STEAM、COAL、OIL 中的一种，否则返回响应代码111；
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_ENERGYTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_ENERGYTYPE_INVALID.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    TimeVO timeVO = time(timeType, timeValue);
                    String energyTypeId = energyType.toLowerCase();
//                    SysEnergyType sysEnergyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
                    List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAll();
                    if (StringUtils.isNotNullAndEmpty(sysEnergyTypes)) {
                        Map<String, String> mapEnergyType = new HashMap<>();
                        for (SysEnergyType type : sysEnergyTypes) {
                            mapEnergyType.put(type.getEnergyTypeId(), type.getEnergyUsageParaId());
                        }
                        String energyUsageParaId = mapEnergyType.get(energyTypeId);
                        SysEnergyPara sysEnergyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyUsageParaId);
                        String unit = sysEnergyPara.getUnit();
                        energyRankVO.setUnit(unit);
                        if (Objects.equals("SITE", objType)) {
                            Park park = parkRepo.findFirstPark();
                            Site site = siteRepo.findBySiteId(objId);
                            if (!Objects.equals("STD_COAL", energyType)) {
                                // 当前所传能源种类不等于标煤：
                                List<Meter> meters = meterRepo.findAllByObjTypeAndObjIdAndEnergyTypeIdAndIsRanking(objType, objId, energyTypeId, true);
                                querySite(objType, objId, energyType, rankVOS, timeVO, energyUsageParaId, park, site, meters, energyTypeId, mapEnergyType);
                            } else {
                                // 当前所传能源种类等于标煤：
                                List<Meter> meters = meterRepo.findAllByObjTypeAndObjIdAndIsRanking(objType, objId, true);
                                querySite(objType, objId, energyType, rankVOS, timeVO, energyUsageParaId, park, site, meters, energyTypeId, mapEnergyType);
                            }
                        } else if (Objects.equals("PARK", objType)) {
                            List<UserGroupMappingObj> userGroupMappingObjs = userGroupMappingObjRepo.findAllByUserGroupIdAndObjType(userGroupId, "SITE");
                            List<String> siteIds = new ArrayList<>();
                            for (UserGroupMappingObj userGroupMappingObj : userGroupMappingObjs) {
                                String siteId = userGroupMappingObj.getObjId();
                                siteIds.add(siteId);
                            }
                            Map<String, String> siteMap = new HashMap<>();
                            List<Site> sites = siteRepo.findBySiteIdIn(siteIds);
                            if (StringUtils.isNotNullAndEmpty(sites)) {
                                for (Site site : sites) {
                                    siteMap.put(site.getSiteId(), site.getSiteName());
                                }
                            }
                            List<DataSource> dataSources = dataSourceRepo.findByObjTypeAndEnergyTypeIdAndEnergyParaIdAndObjIdIn("SITE", energyTypeId, energyUsageParaId, siteIds);
                            Map<String, String> pointIdMap = new HashMap<>();
                            Map<String, Double> valueMap = new HashMap<>();
                            if (StringUtils.isNotNullAndEmpty(dataSources)) {
                                for (DataSource dataSource : dataSources) {
                                    pointIdMap.put("SITE" + dataSource.getObjId() + dataSource.getEnergyTypeId() + dataSource.getEnergyParaId(), dataSource.getDataSource());
                                }
                                if (pointIdMap != null) {
                                    Collection<String> values = pointIdMap.values();
                                    List<String> pointIds = new ArrayList<>(values);
                                    AggregatorDataResponse queryDiff = kairosdbClient.queryDiff(pointIds, timeVO.getStartTime(), timeVO.getEndTime(), 1, timeVO.getTimeUnit());
                                    List<DataPointResult> dataPointResults = queryDiff.getValues();
                                    if (StringUtils.isNotNullAndEmpty(dataPointResults)) {
                                        for (DataPointResult dataPointResult : dataPointResults) {
                                            List<Double> resultValues = dataPointResult.getValues();
                                            String metricName = dataPointResult.getMetricName();
                                            if (StringUtils.isNotNullAndEmpty(resultValues)) {
                                                Double value = resultValues.get(0);
                                                valueMap.put(metricName, value);
                                            }
                                        }
                                        Map<String, Double> map = new HashMap<>();
                                        if (siteMap != null) {
                                            for (DataSource dataSource : dataSources) {
                                                String id = dataSource.getObjId();
                                                String pointId = pointIdMap.get("SITE" + dataSource.getObjId() + dataSource.getEnergyTypeId() + dataSource.getEnergyParaId());
                                                Double value = valueMap.get(pointId);
                                                map.put(id, value);
                                            }
                                            for (String id : siteMap.keySet()) {
                                                Double value = map.get(id);
                                                String name = siteMap.get(id);
                                                RankVO rankVO = new RankVO();
                                                rankVO.setName(name);
                                                if (value != null) {
                                                    value = Double.valueOf(MathUtil.double2String(value));
                                                }
                                                rankVO.setValue(value);
                                                rankVOS.add(rankVO);
                                            }
                                            sortDesc(rankVOS);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    energyRankVO.setRank(rankVOS);
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, energyRankVO);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]能耗排名");
            log.setFunction("获取对象能耗排名数据");
            log.setOperateContent(objType + "_" + objId + "_" + energyType + "_" + timeType + "_" + timeValue);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    private void querySite(String objType, String objId, String energyType, List<RankVO> rankVOS, TimeVO timeVO, String energyUsageParaId, Park park, Site site, List<Meter> meters, String energyTypeId, Map<String, String> mapEnergyType) {
        if (StringUtils.isNotNullAndEmpty(meters)) {
            /**
             * 然后根据如下规则组装去实时库查询的每个仪表的测点名称：
             * tb_park表中第1条记录的rtdb_tenant_id字段 + “.” + 当前企业在tb_site表中的rtdb_project_id字段 + “.” + meter_id + “.” + 仪表对应能源种类对应的用量参数标识
             * 再结合上面确定的时间范围查询出差值，然后还需要乘以仪表所属能源种类的std_coal_coeff系数，最后按结果降序排列
             */
            if (park != null) {
                String rtdbTenantId = park.getRtdbTenantId();
                if (site != null) {
                    String rtdbProjectId = site.getRtdbProjectId();
                    Map<String, String> pointIdMaps = getPointIds(objType, objId, energyUsageParaId, meters, rtdbTenantId, rtdbProjectId, energyType, energyTypeId, mapEnergyType);
                    if (pointIdMaps != null) {
                        queryRankVO(objType, objId, rankVOS, timeVO, meters, pointIdMaps);
                    }
                }
            }
        }
    }

    private void queryRankVO(String objType, String objId, List<RankVO> rankVOS, TimeVO timeVO, List<Meter> meters, Map<String, String> pointIdMaps) {
        Collection<String> values = pointIdMaps.values();
        List<String> pointIds = new ArrayList<>(values);
        AggregatorDataResponse queryDiff = kairosdbClient.queryDiff(pointIds, timeVO.getStartTime(), timeVO.getEndTime(), 1, timeVO.getTimeUnit());
        List<DataPointResult> dataPointResults = queryDiff.getValues();
        if (StringUtils.isNotNullAndEmpty(dataPointResults)) {
            Map<String, Double> valueMap = new HashMap<>();
            for (DataPointResult dataPointResult : dataPointResults) {
                List<Double> resultValues = dataPointResult.getValues();
                String metricName = dataPointResult.getMetricName();
                if (resultValues != null && !resultValues.isEmpty()) {
                    Double value = resultValues.get(0);
                    valueMap.put(metricName, value);
                }
            }
            if (valueMap != null) {
                for (Meter meter : meters) {
                    RankVO rankVO = new RankVO();
                    String meterName = meter.getMeterName();
                    String pointId = pointIdMaps.get(objType + objId + meter.getMeterId());
                    Double value = valueMap.get(pointId);
                    if (value != null) {
                        value = Double.valueOf(MathUtil.double2String(value));
                    }
                    rankVO.setName(meterName);
                    rankVO.setValue(value);
                    rankVOS.add(rankVO);
                }
                sortDesc(rankVOS);
            }
        }
    }

    private Map<String, String> getPointIds(String objType, String objId, String energyUsageParaId, List<Meter> meters, String rtdbTenantId, String rtdbProjectId, String energyType, String energyTypeId, Map<String, String> mapEnergyType) {
        Map<String, String> pointIdMaps = new HashMap<>();
        for (Meter meter : meters) {
            String meterId = meter.getMeterId();
            if (StringUtils.isNotNullAndEmpty(meterId)) {
                String pointId = null;
                if (!Objects.equals("STD_COAL", energyType)) {
                    pointId = rtdbTenantId + "." + rtdbProjectId + "." + meter.getMeterId() + "." + energyUsageParaId;
                } else {
                    String metId = meter.getMeterId();
                    String typeId = meter.getEnergyTypeId();
                    pointId = rtdbTenantId + "." + rtdbProjectId + "." + metId + "." + mapEnergyType.get(typeId) + "[" + typeId + "]";
                }
                pointIdMaps.put(objType + objId + meterId, pointId);
            }
        }
        return pointIdMaps;
    }

    private static void sortDesc(List<RankVO> rankVOS) {
        rankVOS.sort(Comparator.comparing(key -> key.getValue()
                , Comparator.nullsFirst(Double::compareTo).reversed()));
    }

    private void queryTotal(String objType, String objId, TimeVO timeVO, SysEnergyType sysEnergyType, TotalVO totalVO) {
        String energyTypeId = sysEnergyType.getEnergyTypeId();
        String energyUsageParaId = sysEnergyType.getEnergyUsageParaId();
        SysEnergyPara sysEnergyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyUsageParaId);
        String unit = sysEnergyPara.getUnit();
        totalVO.setUnit(unit);
        /**
         * 在tb_obj_data_source表中找出所查对象 标煤 的 用量参数 所配置的数据源（可能是公式，也可能单个测点），然后去实时库查询上面确定的时间范围内的每个测点的差值，
         * 然后将数据源中每个测点的差值还需要乘以对应能源种类的std_coal_coeff系数，如果数据源是公式最后还有按照公式计算。
         */
        DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, energyTypeId, energyUsageParaId);
        if (dataSource != null) {
            String pointId = dataSource.getDataSource();
            if (StringUtils.isNotNullAndEmpty(pointId)) {
                SampleData4KairosResp sampleData4KairosResp = kairosdbClient.queryDiff(pointId, timeVO.getStartTime(), timeVO.getEndTime(), 1, timeVO.getTimeUnit());
                if (sampleData4KairosResp != null) {
                    List<Double> values = sampleData4KairosResp.getValues();
                    if (values != null && !values.isEmpty()) {
                        Double value = values.get(0);
                        if (value != null) {
                            totalVO.setValue(Double.valueOf(MathUtil.double2String(value)));
                        } else {
                            totalVO.setValue(null);
                        }
                    } else {
                        totalVO.setValue(null);
                    }
                } else {
                    totalVO.setValue(null);
                }
            }
        }
    }

    private TimeVO time(String timeType, Long timeValue) {
        TimeVO timeVO = new TimeVO();
        if (Objects.equals("DAY", timeType)) {
            timeVO.setStartTime(DateUtil.longToLocalTime(timeValue));
            timeVO.setEndTime(DateUtil.longToLocalTime(timeValue).plusDays(1));
            timeVO.setTimeUnit(TimeUnit.DAYS);
        }
        if (Objects.equals("MONTH", timeType)) {
            timeVO.setStartTime(DateUtil.longToLocalTime(timeValue));
            timeVO.setEndTime(DateUtil.longToLocalTime(timeValue).plusMonths(1));
            timeVO.setTimeUnit(TimeUnit.MONTHS);
        }
        if (Objects.equals("YEAR", timeType)) {
            timeVO.setStartTime(DateUtil.longToLocalTime(timeValue));
            timeVO.setEndTime(DateUtil.longToLocalTime(timeValue).plusYears(1));
            timeVO.setTimeUnit(TimeUnit.YEARS);
        }
        return timeVO;
    }

    public static void main(String[] args) {
        String userKey = "2a103650-c0f7-47ad-9449-0dd82b9e4eb7";
        String s = userKey + "OTdjYzEzNDRmZjlmYmJiODgxODU5NDc1NTNhNWJkM2M=" + "1546272000000";
        String hex = DigestUtils.md5DigestAsHex(s.getBytes());
        System.out.println(hex);

        Long time1 = 1121323213L;
        Long time2 = 2212121212L;
        System.out.println((time2 - time1) / (24 * 3600 * 1000.0) > 100);
    }

    @Override
    public AppResult objEnergyAnalysis(String objType, String objId, String energyType, String timeType, Long timeValue, HttpServletRequest request) {
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            SysLog log = new SysLog();
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            AppEnergyAnalysisVO analysisVO = new AppEnergyAnalysisVO();
            List<RankVO> rankVOS = new ArrayList<>();
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);

                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId, energyType, timeType, timeValue)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (!Objects.equals("DAY", timeType) && !Objects.equals("MONTH", timeType) && !Objects.equals("YEAR", timeType)) {
                    // 检查timeType的值是否为DAY、MONTH、YEAR中的一种，否则返回响应代码110
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_TIMETYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_TIMETYPE_INVALID.getMsg(), null);
                } else if (!Objects.equals("STD_COAL", energyType) && !Objects.equals("ELECTRICITY", energyType) && !Objects.equals("WATER", energyType)
                        && !Objects.equals("STEAM", energyType) && !Objects.equals("COAL", energyType) && !Objects.equals("OIL ", energyType)) {
                    // 检查energyType的值是否为STD_COAL、ELECTRICITY、WATER、STEAM、COAL、OIL 中的一种，否则返回响应代码111；
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_ENERGYTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_ENERGYTYPE_INVALID.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    TimeVO timeNow = new TimeVO();
                    TimeVO timePre = new TimeVO();
                    TimeVO timePreTQ = new TimeVO();
                    time1(timeValue, timeNow, timePre, timePreTQ, timeType);
                    String energyTypeId = energyType.toLowerCase();
                    SysEnergyType sysEnergyType = sysEnergyTypeRepo.findByEnergyTypeId(energyTypeId);
                    if (sysEnergyType != null) {
                        String energyUsageParaId = sysEnergyType.getEnergyUsageParaId();
                        SysEnergyPara sysEnergyPara = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyTypeId, energyUsageParaId);
                        String unit = sysEnergyPara.getUnit();
                        analysisVO.setUnit(unit);
                        DataSource dataSource = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, energyTypeId, energyUsageParaId);
                        if (dataSource != null) {
                            String source = dataSource.getDataSource();
                            if (StringUtils.isNotNullAndEmpty(source)) {
                                if ("DAY".equals(timeType)) {
                                    SampleData4KairosResp thisDetail = kairosdbClient.queryDiff(source, timeNow.getStartTime(), timeNow.getEndTime(), 1, TimeUnit.HOURS);
                                    SampleData4KairosResp lastDetail = kairosdbClient.queryDiff(source, timePre.getStartTime(), timePre.getEndTime(), 1, TimeUnit.HOURS);
                                    SampleData4KairosResp lastSamePeriodDetail = kairosdbClient.queryDiff(source, timePreTQ.getStartTime(), timePreTQ.getEndTime(), 1, TimeUnit.HOURS);

                                    analysisVO = queryAnalysis(analysisVO, thisDetail, lastDetail, lastSamePeriodDetail);
                                } else if ("MONTH".equals(timeType)) {
                                    SampleData4KairosResp thisDetail = kairosdbClient.queryDiff(source, timeNow.getStartTime(), timeNow.getEndTime(), 1, TimeUnit.DAYS);
                                    SampleData4KairosResp lastDetail = kairosdbClient.queryDiff(source, timePre.getStartTime(), timePre.getEndTime(), 1, TimeUnit.DAYS);
                                    SampleData4KairosResp lastSamePeriodDetail = kairosdbClient.queryDiff(source, timePreTQ.getStartTime(), timePreTQ.getEndTime(), 1, TimeUnit.DAYS);

                                    analysisVO = queryAnalysis(analysisVO, thisDetail, lastDetail, lastSamePeriodDetail);
                                } else if ("YEAR".equals(timeType)) {
                                    SampleData4KairosResp thisDetail = kairosdbClient.queryDiff(source, timeNow.getStartTime(), timeNow.getEndTime(), 1, TimeUnit.MONTHS);
                                    SampleData4KairosResp lastDetail = kairosdbClient.queryDiff(source, timePre.getStartTime(), timePre.getEndTime(), 1, TimeUnit.MONTHS);
                                    SampleData4KairosResp lastSamePeriodDetail = kairosdbClient.queryDiff(source, timePreTQ.getStartTime(), timePreTQ.getEndTime(), 1, TimeUnit.MONTHS);

                                    analysisVO = queryAnalysis(analysisVO, thisDetail, lastDetail, lastSamePeriodDetail);
                                }
                            }
                        }
                    }
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, analysisVO);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]能耗分析");
            log.setFunction("获取对象能耗分析数据");
            log.setOperateContent(objType + "_" + objId + "_" + energyType + "_" + timeType + "_" + timeValue);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    private AppEnergyAnalysisVO queryAnalysis(AppEnergyAnalysisVO analysisVO, SampleData4KairosResp thisDetail, SampleData4KairosResp lastDetail, SampleData4KairosResp lastSamePeriodDetail) {
        List<Double> thisDetailValues = thisDetail.getValues();
        List<Double> lastDetailValues = lastDetail.getValues();
        List<Double> lastSamePeriodDetailValues = lastSamePeriodDetail.getValues();

        List<Double> thisDetailValuesNew = new ArrayList<>();
        List<Double> lastDetailValuesNew = new ArrayList<>();

        Double thisTotal = 0d;
        if (StringUtils.isNotNullAndEmpty(thisDetailValues)) {
            for (Double thisDetailValue : thisDetailValues) {
                if (thisDetailValue != null) {
                    thisTotal += thisDetailValue;
                    thisDetailValuesNew.add(Double.valueOf(MathUtil.double2String(thisDetailValue)));
                }
            }
        }
        Double lastTotal = null;
        if (StringUtils.isNotNullAndEmpty(lastDetailValues)) {
            for (Double lastDetailValue : lastDetailValues) {
                if (lastDetailValue != null) {
                    if (lastTotal == null) {
                        lastTotal = lastDetailValue;
                    }
                    lastTotal += lastDetailValue;
                    lastDetailValuesNew.add(Double.valueOf(MathUtil.double2String(lastDetailValue)));
                }
            }
            if (lastTotal != null) {
                lastTotal = Double.valueOf(MathUtil.double2String(lastTotal));
            }
        }
        Double lastSamePeriodTotal = null;
        if (StringUtils.isNotNullAndEmpty(lastSamePeriodDetailValues)) {
            for (Double lastSamePeriodDetailValue : lastSamePeriodDetailValues) {
                if (lastSamePeriodDetailValue != null) {
                    if (lastSamePeriodTotal == null) {
                        lastSamePeriodTotal = lastSamePeriodDetailValue;
                    }
                    lastSamePeriodTotal += lastSamePeriodDetailValue;
                }
            }
        }
        Double samePeriodRatio = null;
        if (lastSamePeriodTotal != null && lastSamePeriodTotal != 0d) {
            Double value = (thisTotal - lastSamePeriodTotal) / lastSamePeriodTotal * 100;
            samePeriodRatio = Double.valueOf(MathUtil.double2String(value));
        }
        analysisVO.setThisTotal(thisDetailValuesNew);
        analysisVO.setLastDetail(lastDetailValuesNew);
        analysisVO.setLastTotal(lastTotal);
        analysisVO.setSamePeriodRatio(samePeriodRatio);
        return analysisVO;
    }

    private void time1(Long timeValue, TimeVO timeNow, TimeVO timePre, TimeVO timePreTQ, String timeType) {
        LocalDateTime startNow = DateUtil.longToLocalTime(timeValue).withNano(0);
        LocalDateTime endNow = null;
        if ("DAY".equals(timeType)) {
            // 当期时间范围
            endNow = DateUtil.longToLocalTime(timeValue).plusDays(1).withNano(0);
            Long end = DateUtil.localDateTimeToLong(endNow);
            timeNow.setStartTime(startNow);
            timeNow.setEndTime(endNow);

            // 上期时间范围
            LocalDateTime startPre = startNow.minusDays(1).withNano(0);
            LocalDateTime endPre = startNow;
            timePre.setStartTime(startPre);
            timePre.setEndTime(endPre);

            // 上期同期范围
            LocalDateTime startPreTQ = startPre;
            LocalDateTime endPreTQ = null;
            /**
             * 如果当期时间范围的结束时间小于服务器当前时间，则上期同期时间范围的结束时间和上期时间范围的结束时间完全一样
             */
            Long currentTimeMillis = System.currentTimeMillis();
            if (end < currentTimeMillis) {
                endPreTQ = endPre;
            } else {
                /**
                 * 否则上期同期时间范围的结束时间为 上期同期时间范围的开始时间 + （服务器当前时间 - 当期时间范围的开始时间）。上期同期时间数组同样按天分隔。
                 */
                endPreTQ = DateUtil.longToLocalTime(DateUtil.localDateTimeToLong(startPreTQ) + (currentTimeMillis - DateUtil.localDateTimeToLong(startNow)));
            }
            timePreTQ.setStartTime(startPreTQ);
            timePreTQ.setEndTime(endPreTQ);
        } else if ("MONTH".equals(timeType)) {
            // 当期时间范围
            endNow = DateUtil.longToLocalTime(timeValue).plusMonths(1).withNano(0);
            Long end = DateUtil.localDateTimeToLong(endNow);
            timeNow.setStartTime(startNow);
            timeNow.setEndTime(endNow);

            // 上期时间范围
            LocalDateTime startPre = startNow.minusMonths(1).withNano(0);
            LocalDateTime endPre = startNow;
            timePre.setStartTime(startPre);
            timePre.setEndTime(endPre);

            // 上期同期范围
            LocalDateTime startPreTQ = startPre;
            LocalDateTime endPreTQ = null;
            Long currentTimeMillis = System.currentTimeMillis();
            if (end < currentTimeMillis) {
                endPreTQ = endPre;
            } else {
                endPreTQ = DateUtil.longToLocalTime(DateUtil.localDateTimeToLong(startPreTQ) + (currentTimeMillis - DateUtil.localDateTimeToLong(startNow)));
            }
            timePreTQ.setStartTime(startPreTQ);
            timePreTQ.setEndTime(endPreTQ);
        } else if ("YEAR".equals(timeType)) {
            // 当期时间范围
            endNow = DateUtil.longToLocalTime(timeValue).plusYears(1).withNano(0);
            Long end = DateUtil.localDateTimeToLong(endNow);
            timeNow.setStartTime(startNow);
            timeNow.setEndTime(endNow);

            // 上期时间范围
            LocalDateTime startPre = startNow.minusYears(1).withNano(0);
            LocalDateTime endPre = startNow;
            timePre.setStartTime(startPre);
            timePre.setEndTime(endPre);

            // 上期同期范围
            LocalDateTime startPreTQ = startPre;
            LocalDateTime endPreTQ = null;
            Long currentTimeMillis = System.currentTimeMillis();
            if (end < currentTimeMillis) {
                endPreTQ = endPre;
            } else {
                endPreTQ = DateUtil.longToLocalTime(DateUtil.localDateTimeToLong(startPreTQ) + (currentTimeMillis - DateUtil.localDateTimeToLong(startNow)));
            }
            timePreTQ.setStartTime(startPreTQ);
            timePreTQ.setEndTime(endPreTQ);
        }
    }

    @Override
    public AppResult objNoticeInfo(String objType, String objId, Long noticeTime, HttpServletRequest request) {
        AppNoticeVO appNoticeVO = new AppNoticeVO();
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            SysLog log = new SysLog();
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);
                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId, noticeTime)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    LocalDateTime createTime = DateUtil.longToLocalTime(noticeTime);
                    Notice notice = noticeRepo.findByObjTypeAndObjIdAndCreateTime(objType, objId, createTime);
                    if (StringUtils.isNotNullAndEmpty(notice)) {
                        appNoticeVO.setNoticeTitle(notice.getNoticeTitle());
                        appNoticeVO.setNoticeContent(notice.getNoticeContent());
                        appNoticeVO.setNoticeTime(noticeTime);
                    }
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, appNoticeVO);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]报警&交互");
            log.setFunction("获取对象公告信息明细");
            log.setOperateContent(objType + "_" + objId + "_" + noticeTime);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult objNoticeList(String objType, String objId, HttpServletRequest request) {
        List<AppNoticeListVO> noticeList = new ArrayList<>();
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            SysLog log = new SysLog();
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);
                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    List<Notice> notices = noticeRepo.findNoticelist(objType, objId);
                    if (StringUtils.isNotNullAndEmpty(notices)) {
                        for (Notice notice : notices) {
                            AppNoticeListVO appNoticeListVO = new AppNoticeListVO();
                            LocalDateTime createTime = notice.getCreateTime();
                            if (createTime != null) {
                                appNoticeListVO.setNoticeTime(DateUtil.localDateTimeToLong(createTime));
                            }
                            appNoticeListVO.setNoticeTitle(notice.getNoticeTitle());
                            noticeList.add(appNoticeListVO);
                        }
                    }
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, noticeList);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]报警&交互");
            log.setFunction("获取对象公告信息列表");
            log.setOperateContent(objType + "_" + objId);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult objAlarmInfo(String objType, String objId, String alarmId, Long alarmTime, HttpServletRequest request) {
        AppAlarmMsgVO appAlarmMsgVO = new AppAlarmMsgVO();
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            SysLog log = new SysLog();
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);
                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId, alarmId, alarmTime)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    LocalDateTime time = DateUtil.longToLocalTime(alarmTime);
                    AlarmMsg alarmMsg = alarmMsgRepo.findByObjTypeAndObjIdAndAlarmIdAndAlarmTime(objType, objId, alarmId, time);
                    if (StringUtils.isNotNullAndEmpty(alarmMsg)) {
                        appAlarmMsgVO.setAlarmId(alarmMsg.getAlarmId());
                        appAlarmMsgVO.setAlarmName(alarmMsg.getAlarmName());
                        appAlarmMsgVO.setAlarmType(alarmMsg.getAlarmType());
                        appAlarmMsgVO.setAlarmMsg(alarmMsg.getMsg());
                        appAlarmMsgVO.setAlarmTime(DateUtil.localDateTimeToLong(alarmMsg.getAlarmTime()));
                    }
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, appAlarmMsgVO);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]报警&交互");
            log.setFunction("获取对象报警信息明细");
            log.setOperateContent(objType + "_" + objId + "_" + alarmId + "_" + alarmTime);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    @Override
    public AppResult objAlarmList(String objType, String objId, Long startTime, Long endTime, HttpServletRequest request) {
        List<AppAlarmListVO> appAlarmListVOS = new ArrayList<>();
        AppResult result = null;
        try {
            result = checkHeader(request, appVOMap, timeout);
            String token = request.getHeader("token");
            AppVO appVO = appVOMap.get(token);
            SysLog log = new SysLog();
            if (result.getCode() == 0) {
                String userGroupId = userRepo.findByUserId(appVO.getUserId()).getUserGroupId();
                UserGroupMappingObj groupMappingObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, objType, objId);
                long now = System.currentTimeMillis();
                // 检查客户端传来的BODY参数中必选字段是否都有，且都有值（值不为空也不为NULL），否则返回响应代码114
                if (StringUtils.isNullOrEmpty(objType, objId, startTime)) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_PARAMETER_IS_MISSING.getCode(), ResultType.REQUEST_PARAMETER_IS_MISSING.getMsg(), null);
                } else if (!Objects.equals("PARK", objType) && !Objects.equals("SITE", objType)) {
                    // 检查objType的值是否为PARK、SITE中的一种，否则返回响应代码108
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getCode(), ResultType.REQUEST_HEADER_OBJTYPE_INVALID.getMsg(), null);
                } else if (endTime == null && ((now - startTime) / (1000 * 3600 * 24.0)) > 100) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.BEGIN_NOW_100.getCode(), ResultType.BEGIN_NOW_100.getMsg(), null);
                } else if (endTime != null && ((endTime - startTime) / (1000 * 3600 * 24.0)) > 100) {
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.BEGIN_END_100.getCode(), ResultType.BEGIN_END_100.getMsg(), null);
                } else if (groupMappingObj == null) {
                    // 根据objType、objId检查是否在当前用户所属的用户组可见范围内，如果不在，返回响应码109
                    log.setResult("FAIL");
                    result = new AppResult(ResultType.CURRENT_USER_NO_PERMISSION.getCode(), ResultType.CURRENT_USER_NO_PERMISSION.getMsg(), null);
                } else {
                    LocalDateTime start = DateUtil.longToLocalTime(startTime);
                    LocalDateTime end = DateUtil.longToLocalTime(endTime);
                    List<AlarmMsg> alarmMsg;
                    if (end != null) {
                        alarmMsg = alarmMsgRepo.findAlarmMsg(objType, objId, start, end);
                    } else {
                        alarmMsg = alarmMsgRepo.findAlarmMsgNoEnd(objType, objId, start);
                    }
                    if (StringUtils.isNotNullAndEmpty(alarmMsg)) {
                        for (AlarmMsg msg : alarmMsg) {
                            AppAlarmListVO appAlarmListVO = new AppAlarmListVO();
                            appAlarmListVO.setAlarmId(msg.getAlarmId());
                            appAlarmListVO.setAlarmName(msg.getAlarmName());
                            LocalDateTime alarmTime = msg.getAlarmTime();
                            if (alarmTime != null) {
                                appAlarmListVO.setAlarmTime(DateUtil.localDateTimeToLong(alarmTime));
                            }
                            appAlarmListVOS.add(appAlarmListVO);
                        }
                    }
                    result = new AppResult(ResultType.SUCCESS.getCode(), null, appAlarmListVOS);
                    log.setResult("SUCCESS");
                }
            } else {
                judgeCode(result, log);
            }
            if (appVO != null) {
                log.setUserId(appVO.getUserId());
            }
            log.setOperateIp(CommonUtil.getIpAddr(request));
            log.setOperateTime(LocalDateTime.now());
            log.setUrl(request.getRequestURI());
            log.setMethod(request.getMethod());
            log.setMenu("[APP]报警&交互");
            log.setFunction("获取对象报警信息列表");
            log.setOperateContent(objType + "_" + objId + "_" + startTime + "_" + endTime);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            result = new AppResult(ResultType.SERVER_INNER_ERROR.getCode(), ResultType.SERVER_INNER_ERROR.getMsg(), null);
        }
        return result;
    }

    private static AppResult checkHeader(HttpServletRequest request, Map<String, AppVO> appVOMap, String timeout) {
        String token = request.getHeader("token");
        String time = request.getHeader("time");
        String signature = request.getHeader("signature");
        if (StringUtils.isNullOrEmpty(token)) {
            return new AppResult(ResultType.REQUEST_HEADER_MISSING_TOKEN.getCode(), ResultType.REQUEST_HEADER_MISSING_TOKEN.getMsg(), null);
        }
        if (StringUtils.isNullOrEmpty(time)) {
            return new AppResult(ResultType.REQUEST_HEADER_MISSING_TIME.getCode(), ResultType.REQUEST_HEADER_MISSING_TIME.getMsg(), null);
        }
        if (StringUtils.isNullOrEmpty(signature)) {
            return new AppResult(ResultType.REQUEST_HEADER_MISSING_SIGNATURE.getCode(), ResultType.REQUEST_HEADER_MISSING_SIGNATURE.getMsg(), null);
        }

        Long now = System.currentTimeMillis();
        AppVO appVO = appVOMap.get(token);
        if (appVO == null) {
            return new AppResult(ResultType.REQUEST_HEADER_TOKEN_INVALID.getCode(), ResultType.REQUEST_HEADER_TOKEN_INVALID.getMsg(), null);
        }
        Long timeMap = appVO.getTime();
        if (now > timeMap) {
            appVOMap.remove(token);
            return new AppResult(ResultType.REQUEST_HEADER_TOKEN_OVERDUE.getCode(), ResultType.REQUEST_HEADER_TOKEN_OVERDUE.getMsg(), null);
        }

        String userKey = appVO.getUserKey();
        String s = userKey + token + time;
        String hex = DigestUtils.md5DigestAsHex(s.getBytes());
        if (!Objects.equals(signature, hex)) {
            return new AppResult(ResultType.REQUEST_HEADER_SIGNATURE_VERIFICATION_FAILED.getCode(), ResultType.REQUEST_HEADER_SIGNATURE_VERIFICATION_FAILED.getMsg(), null);
        }
        String minute = timeout.substring(0, timeout.length() - 1);
        appVO.setTime(now + Integer.valueOf(minute) * 60 * 1000);
        appVOMap.put(token, appVO);
        return new AppResult(ResultType.SUCCESS.getCode(), null, null);
    }
}
