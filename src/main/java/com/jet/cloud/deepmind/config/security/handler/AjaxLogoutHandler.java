package com.jet.cloud.deepmind.config.security.handler;

import com.alibaba.fastjson.JSON;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.entity.SysLog;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.LogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.jet.cloud.deepmind.common.Constants.*;
import static com.jet.cloud.deepmind.common.HttpConstants.GET;

/**
 * 注销
 *
 * @author yhy
 * @create 2019-10-11 11:25
 */
@Component
@Log4j2
public class AjaxLogoutHandler implements LogoutHandler {
    @Autowired
    private LogService logService;

    //@Override
    //public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    //
    //}

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession();
        SysUser sysUser = (SysUser) session.getAttribute(SESSION_USER_ID);
        //if (sysUser == null) {
        //    response.sendRedirect("/login");
        //    return;
        //}
        session.removeAttribute(CURRENT_USER_MENUS);
        session.removeAttribute(CURRENT_USER_BUTTONS);
        session.removeAttribute(SESSION_USER_ID);
        session.removeAttribute(USER_NAME_LOGIN);

        SysLog s = new SysLog("用户登出", true);
        s.setUserId(sysUser.getUserId());
        s.setMemo(sysUser.getUserName());
        s.setOperateTime(LocalDateTime.now());
        s.setUrl("/logout");
        s.setMethod(GET);
        s.setMenu("系统");
        s.setFunction("登出");
        s.setOperateContent("系统-登出");
        //s.setOperatorIp(CommonUtil.getIpAddr(request));
        logService.save(s);
        Response ok = Response.ok("注销成功");
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getWriter().write(JSON.toJSONString(ok));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("[{}-{}]已注销登陆", sysUser.getUserId(), sysUser.getUserName());
    }
}
