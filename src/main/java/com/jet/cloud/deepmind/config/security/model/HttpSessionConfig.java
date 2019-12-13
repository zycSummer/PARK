package com.jet.cloud.deepmind.config.security.model;//package com.jet.cloud.deepmind.config.security.model;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.http.HttpSession;
//import javax.servlet.http.HttpSessionEvent;
//import javax.servlet.http.HttpSessionListener;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author yhy
// * @create 2019-10-16 11:19
// */
//@Configuration
//public class HttpSessionConfig {
//
//    private static final ConcurrentHashMap<String, HttpSession> sessions = new ConcurrentHashMap<>();
//
//    public Map<String, HttpSession> getActiveSessions() {
//        return sessions;
//    }
//
//    @Bean
//    public HttpSessionListener httpSessionListener() {
//        return new HttpSessionListener() {
//            @Override
//            public void sessionCreated(HttpSessionEvent hse) {
//               sessions.put(hse.getSession().getId(), hse.getSession());
//            }
//
//            @Override
//            public void sessionDestroyed(HttpSessionEvent hse) {
//                sessions.remove(hse.getSession().getId());
//            }
//        };
//    }
//}
