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
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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

    public String getFilePath() {
        return filePath;
    }

    public String getImagePath() {
        return filePath + File.separator + "img" + File.separator;
    }

    public String getEquipImagePath() {
        return filePath + File.separator + "equip_img" + File.separator;
    }

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
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
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
        return new EmailUtils("连云港石化产业基地能源管理中心平台");
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
}
