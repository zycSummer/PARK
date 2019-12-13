package com.jet.cloud.deepmind.common.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * 短信发送工具类
 *
 * @author yhy
 * @create 2019-11-19 13:19
 */
@Log4j2
public class SmsUtils {

    public SmsUtils(String url, String uid, String key, RestTemplate restTemplate) {
        this.uid = uid;
        this.key = key;
        this.url = url;
        this.restTemplate = restTemplate;
    }

    private String uid;
    private String key;
    private String url;
    private RestTemplate restTemplate;

    public Integer send(String smsMob, String smsText) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf8");
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("Uid", uid);
        postParameters.add("Key", key);
        postParameters.add("smsMob", smsMob);
        postParameters.add("smsText", smsText);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(postParameters, headers);
        ResponseEntity<Object> post = restTemplate.postForEntity(url, entity, Object.class);
        if (Objects.equals(HttpStatus.OK, post.getStatusCode())) {
            Integer count = (Integer) post.getBody();
            return count;
        }
        return -1;
    }
}
