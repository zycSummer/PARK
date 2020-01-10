package com.jet.cloud.deepmind.config;


import com.jet.cloud.deepmind.common.util.*;
import io.github.biezhi.ome.OhMyEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableAsync
@EnableCaching
public class AppConfig {

    @Value("${file.path}")
    private String filePath;
    @Value("${http.connect.timeout.millisecond}")
    private Integer httpConnectTimeout;
    @Value("${http.read.timeout.millisecond}")
    private Integer httpReadTimeout;
    @Value("${alarm.mail.username}")
    private String mailUsername;
    @Value("${alarm.mail.password}")
    private String mailPassword;
    @Value("${alarm.mail.smtp.server}")
    private String mailSmtpServer;
    @Value("${alarm.mail.smtp.auth}")
    private String mailAuth;
    @Value("${alarm.mail.smtp.protocol}")
    private String mailProtocol;
    @Value("${sms.uid}")
    private String smsUid;
    @Value("${sms.key}")
    private String smsKey;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${alarm.scheduler}")
    private String alarmCron;
    @Value("${huawei.cloud.Api.sendAlarm}")
    private String alarmUrl;
    @Value("${huawei.cloud.Api.sendSolution}")
    private String solutionUrl;
    @Value("${app.url}")
    private String appTencentUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * 异步任务执行默认线程池
     *
     * @return
     */
    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(8);
        scheduler.setThreadNamePrefix("jetScheduler");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public RestTemplate alarmRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(httpConnectTimeout);
        requestFactory.setReadTimeout(httpReadTimeout);
        RestTemplate template = new RestTemplate(requestFactory);
        template.getMessageConverters().add(new MyMappingJackson2HttpMessageConverter());
        return template;
    }

    @Bean
    public EmailUtils emailUtils() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailSmtpServer);
        properties.put("mail.smtp.auth", mailAuth);
        properties.put("mail.transport.protocol", mailProtocol);
        properties.put("username", mailUsername);
        properties.put("password", AES.decrypt(mailPassword));
        OhMyEmail.config(properties);
        return new EmailUtils();
    }

    @Bean
    public SmsUtils smsUtils() {
        return new SmsUtils(smsUrl, smsUid, AES.decrypt(smsKey), alarmRestTemplate());
    }

    @Bean
    public AlarmHttpUtils alarmHttpUtils() {
        return new AlarmHttpUtils(alarmRestTemplate(), alarmUrl);
    }

    @Bean
    public SolutionHttpUtils solutionHttpUtils() {
        return new SolutionHttpUtils(alarmRestTemplate(), solutionUrl);
    }

    @Bean
    public AppSendTencentUtils appSendTencentUtils() {
        return new AppSendTencentUtils(alarmRestTemplate(), appTencentUrl);
    }

    public String getAlarmCron() {
        return alarmCron;
    }

    public class MyMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public MyMappingJackson2HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            mediaTypes.add(MediaType.TEXT_HTML);  //加入text/html类型的支持
            setSupportedMediaTypes(mediaTypes);
        }
    }

    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    public String getFilePath() {
        return filePath + File.separator + "doc" + File.separator;
    }

    public String getImagePath() {
        return filePath + File.separator + "img" + File.separator;
    }

    public String getEquipImagePath() {
        return filePath + File.separator + "equip_img" + File.separator;
    }
}
