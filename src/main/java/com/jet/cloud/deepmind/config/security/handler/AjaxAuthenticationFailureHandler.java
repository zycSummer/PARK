package com.jet.cloud.deepmind.config.security.handler;

import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.config.security.model.PasswordNotMatchException;
import com.jet.cloud.deepmind.model.Response;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.jet.cloud.deepmind.common.HttpConstants.UNAUTHORIZED;

/**
 * @author yhy
 * @create 2019-10-11 11:21
 */
@Component
public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.SESSION_USER_NAME);
            session.removeAttribute(Constants.SESSION_USER_ID);
            session.removeAttribute(Constants.SESSION_LAST_LOGIN_TIME);
            session.removeAttribute(Constants.USER_NAME_LOGIN);
        }

        String result = null;
        if (exception instanceof BadCredentialsException ||
                exception instanceof PasswordNotMatchException) {
            result = "用户名或密码错误";
        } else if (exception instanceof UsernameNotFoundException) {
            result = "系统无此用户";
        } else if (exception instanceof DisabledException) {
            result = "此用户被禁止使用";
        } else if (exception instanceof LockedException) {
            result = "此用户被锁定";
        } else if (exception instanceof AccountExpiredException) {
            result = "此用户已过期";
        } else {
            result = exception.getMessage();
        }
        Response error = Response.error(result);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(Constants.mapper.writeValueAsString(error));
    }
}