package com.jet.cloud.deepmind.common;

/**
 * @author yhy
 * @create 2019-10-14 10:39
 */
public interface HttpConstants {
    // 成功
    int SUCCESS = 0;
    /**
     * 未授权
     */
    int UNAUTHORIZED = 401;

    int AJAX_UNAUTHORIZED = 402;

    int IMPORTEXCELFAIL = -1;

    String POST = "POST";
    String GET = "GET";
    String PUT = "PUT";
    String DELETE = "DELETE";
}
