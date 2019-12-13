package com.jet.cloud.deepmind.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.entity.SysLog;
import lombok.Data;

/**
 * @author yhy
 * @create 2019-12-10 13:15
 */
@Data
public class SysLogRowModel extends BaseRowModel {


    @ExcelProperty(index = 0, value = "用户标识")
    private String userId;

    @ExcelProperty(index = 1, value = "操作时间")
    private String operateTime;

    /**
     * 操作菜单（[菜单标识]菜单名称）
     */
    @ExcelProperty(index = 2, value = "操作菜单")
    private String menu;
    /**
     * 操作菜单功能（[菜单功能标识]菜单功能名称）
     */
    @ExcelProperty(index = 3, value = "操作菜单功能")
    private String function;

    /**
     * 1：post
     * 2:get
     * 3:delete
     * 4:put
     */
    @ExcelProperty(index = 4, value = "请求方式")
    private String method;
    @ExcelProperty(index = 5, value = "接口地址")
    private String url;
    @ExcelProperty(index = 6, value = "操作内容")
    private String operateContent;

    /**
     * enum 'SUCCESS','FAIL'
     */
    @ExcelProperty(index = 7, value = "操作结果")
    private String result;
    @ExcelProperty(index = 8, value = "备注")
    private String memo;

    public SysLogRowModel(SysLog sysLog) {
        this.userId = sysLog.getUserId();
        this.operateTime = DateUtil.localDateTimeToString(sysLog.getOperateTime());
        this.menu = sysLog.getMenu();
        this.function = sysLog.getFunction();
        this.method = sysLog.getMethod();
        this.url = sysLog.getUrl();
        this.operateContent = sysLog.getOperateContent();
        this.result = sysLog.getResult();
        this.menu = sysLog.getMenu();

    }
}
