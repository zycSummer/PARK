package com.jet.cloud.deepmind.config.security.handler;

import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.model.Response;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yhy
 * @create 2019-10-11 11:24
 */
@Component
public class AjaxAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        //Response error = Response.error(HttpConstants.UNAUTHORIZED, "Access Denied");
        //response.setContentType("application/json;charset=utf-8");
        //response.getWriter().write(Constants.mapper.writeValueAsString(error));
        if (isAjax) {
            Response error = Response.error(HttpConstants.AJAX_UNAUTHORIZED, "Access Denied ...");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(Constants.mapper.writeValueAsString(error));
        } else {
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
            RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
            dispatcher.forward(request, response);//执行转发
            //response.sendRedirect("/login");
        }


    }
}
