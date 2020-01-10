package com.jet.cloud.deepmind.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultType {
    SUCCESS(0, "成功"),

    WRONG_ACCOUNT_OR_PASSWORD(101, "账户或密码错误"),

    REQUEST_HEADER_MISSING_TOKEN(102, "请求头部缺少token"),

    REQUEST_HEADER_MISSING_TIME(103, "请求头部缺少time"),

    REQUEST_HEADER_MISSING_SIGNATURE(104, "请求头部缺少signature"),

    REQUEST_HEADER_TOKEN_INVALID(105, "请求头部token无效"),

    REQUEST_HEADER_TOKEN_OVERDUE(106, "请求头部token过期"),

    REQUEST_HEADER_SIGNATURE_VERIFICATION_FAILED(107, "请求头部signature验证未通过"),

    REQUEST_HEADER_OBJTYPE_INVALID(108, "请求参数中objType值无效"),

    CURRENT_USER_NO_PERMISSION(109, "当前用户无查看此园区或建筑的权限"),

    REQUEST_HEADER_TIMETYPE_INVALID(110, "请求参数中timeType值无效"),

    REQUEST_HEADER_ENERGYTYPE_INVALID(111, "请求参数中energyType值无效"),

    BEGIN_END_100(112, "请求参数中开始时间与结束时间跨度不能超过100天"),

    BEGIN_NOW_100(113, "请求参数中开始时间与当前时间跨度不能超过100天"),

    REQUEST_PARAMETER_IS_MISSING(114, "请求参数缺少必选字段或者必选字段值为空"),

    ACCOUNT_IS_DISABLED(115, "账号已禁用"),

    ACCOUNT_IS_LOCKED(116, "账号已锁定"),

    ACCOUNT_IS_EXPIRED(117, "账号已过期"),

    SERVER_INNER_ERROR(200, "服务器内部错误");


    private final int code;
    private final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ResultType{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
