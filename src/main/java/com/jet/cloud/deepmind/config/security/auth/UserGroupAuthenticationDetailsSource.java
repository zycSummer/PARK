//package com.jet.cloud.deepmind.config.security.auth;
//
//import com.jet.cloud.deepmind.config.security.model.UserGroupWebAuthenticationDetails;
//import org.springframework.security.authentication.AuthenticationDetailsSource;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author yhy
// * @create 2019-10-11 14:33
// */
//@Component
//public class UserGroupAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {
//    @Override
//    public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
//        return new UserGroupWebAuthenticationDetails(request);
//    }
//}
