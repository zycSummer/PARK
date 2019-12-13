package com.jet.cloud.deepmind.common.util;


import com.jet.cloud.deepmind.model.Triple;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yhy
 * @create 2019-12-03 10:21
 */
public class ExcelUtil {

    public static Triple<HttpServletRequest, HttpServletResponse, ServletOutputStream> setExcelRespAndReqAndGetServletOutputStream(String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAgent = request.getHeader("User-Agent");
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "max-age=0");
        response.setDateHeader("Expires", 0);//设置日期头
        response.setHeader("Content-disposition", "attachment; filename=" + StringUtils.resolvingScrambling(fileName, userAgent));
        return new Triple<>(request, response, outputStream);
    }

}
