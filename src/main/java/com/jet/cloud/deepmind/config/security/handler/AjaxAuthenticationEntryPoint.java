package com.jet.cloud.deepmind.config.security.handler;

import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.model.Response;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录
 *
 * @author yhy
 * @create 2019-10-11 09:26
 */
@Component
public class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        Response error = Response.error(HttpConstants.UNAUTHORIZED, e.getMessage());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(Constants.mapper.writeValueAsString(error));
    }
}
