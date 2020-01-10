package com.jet.cloud.deepmind.common.util;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.AppResultVO;
import com.jet.cloud.deepmind.model.AppTencentVO;
import com.jet.cloud.deepmind.model.SolutionMsgVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author zhuyicheng
 * @create 2019/12/27 10:52
 * @desc AppSendTencentUtils
 */
@Log4j2
public class AppSendTencentUtils {
    private RestTemplate restTemplate;
    private String url;

    public AppSendTencentUtils(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public Boolean send(AppTencentVO appTencentVO, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf8");
        headers.add("Authorization", "Basic " + authorization);
        headers.add("Host", "openapi.xg.qq.com");
        headers.add("Cache-Control", "no-cache");
        HttpEntity entity = new HttpEntity<>(JSONObject.toJSON(appTencentVO), headers);
        long time = System.currentTimeMillis();
        log.info("腾讯移动推送，开始时间={},entity={}", LocalDateTime.now(), JSONObject.toJSONString(appTencentVO));
        ResponseEntity<JSONObject> post = restTemplate.postForEntity(url, entity, JSONObject.class);
        log.info("腾讯移动推送--响应post={}，结束时间={}", post, DateUtil.localDateTimeToLong(LocalDateTime.now()) - time);
        if (Objects.equals(HttpStatus.OK, post.getStatusCode())) {
            JSONObject body = post.getBody();
            AppResultVO appResultVO = body.toJavaObject(AppResultVO.class);
            if (appResultVO != null) {
                if (appResultVO.getRetCode() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
