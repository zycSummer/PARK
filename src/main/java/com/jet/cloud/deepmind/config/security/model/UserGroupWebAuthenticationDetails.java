//package com.jet.cloud.deepmind.config.security.model;
//
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 额外信息
// *
// * @author yhy
// * @create 2019-10-11 14:31
// */
//public class UserGroupWebAuthenticationDetails extends WebAuthenticationDetails {
//
//    public String userGroup;
//
//    /**
//     * Records the remote address and will also set the session Id if a session already
//     * exists (it won't create one).
//     *
//     * @param request that the authentication request was received from
//     */
//    public UserGroupWebAuthenticationDetails(HttpServletRequest request) {
//        super(request);
//        this.userGroup = request.getParameter("userGroup");
//    }
//
//    public String getUserGroup() {
//        return userGroup;
//    }
//
//    public void setUserGroup(String userGroup) {
//        this.userGroup = userGroup;
//    }
//}
