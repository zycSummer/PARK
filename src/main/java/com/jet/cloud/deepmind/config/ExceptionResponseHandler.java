package com.jet.cloud.deepmind.config;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.SysLog;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.LogService;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.jet.cloud.deepmind.common.HttpConstants.*;

/**
 * @author yhy
 * @create 2019-11-12 14:43
 */
@RestControllerAdvice
@Log4j2
public class ExceptionResponseHandler {
    @Autowired
    CurrentUser currentUser;
    @Autowired
    private LogService logService;

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response validationBodyException(MethodArgumentNotValidException exception) {

        //按需重新封装需要返回的错误信息
        StringJoiner joiner = new StringJoiner(",");
        //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            joiner.add(error.getDefaultMessage());
        }
        return Response.error(joiner.toString());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response requestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        exception.printStackTrace();
        currentUser.userId();
        SysLog s = new SysLog("用户登出", true);
        s.setUserId(currentUser.userId());
        s.setMemo(currentUser.user().getUserName());
        s.setOperateTime(LocalDateTime.now());
        s.setUrl(currentUser.getRemoteURI());
        s.setMethod(GET);
        s.setMenu("系统");
        s.setFunction("权限校验");
        s.setOperateContent("拒绝访问");
        s.setResult("FAIL");
        logService.save(s);
        return Response.error(AJAX_UNAUTHORIZED, "没有该功能权限");
    }
}
