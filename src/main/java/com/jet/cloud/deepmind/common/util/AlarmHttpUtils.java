package com.jet.cloud.deepmind.common.util;

import com.jet.cloud.deepmind.model.AlarmMsgVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

@Log4j2
public class AlarmHttpUtils {
    private RestTemplate restTemplate;
    private String url;

    public AlarmHttpUtils(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public Boolean send(AlarmMsgVO alarmMsgVO) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf8");

        HttpEntity<AlarmMsgVO> entity = new HttpEntity<>(alarmMsgVO, headers);
        long time = System.currentTimeMillis();
        log.info("调用中软上报报警/恢复信息，开始时间={}", LocalDateTime.now());
        ResponseEntity<Object> post = restTemplate.postForEntity(url, entity, Object.class);
        log.info("调用中软上报报警/恢复信息--响应post={}，结束时间={}", post, DateUtil.localDateTimeToLong(LocalDateTime.now()) - time);
        if (Objects.equals(HttpStatus.OK, post.getStatusCode())) {
            return true;
        }
        return false;
    }
}
