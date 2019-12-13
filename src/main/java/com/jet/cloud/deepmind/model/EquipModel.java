package com.jet.cloud.deepmind.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @author zhuyicheng
 * @create 2019/12/12 09:29
 * @desc 设备信息导入模板
 */
@Data
public class EquipModel extends BaseRowModel {
    /**
     * 设备标识
     */
    @ExcelProperty(index = 0)
    private String equipId;

    /**
     * 设备名称
     */
    @ExcelProperty(index = 1)
    private String equipName;

    /**
     * 设备系统标识
     */
    @ExcelProperty(index = 2)
    private String equipSysId;

    /**
     * 厂家
     */
    @ExcelProperty(index = 3)
    private String manufacturer;

    /**
     * 型号
     */
    @ExcelProperty(index = 4)
    private String model;

    /**
     * 备注
     */
    @ExcelProperty(index = 5)
    private String location;

    /**
     * 生产日期
     */
    @ExcelProperty(index = 6)
    private String productionDate;

    /**
     * 投用日期
     */
    @ExcelProperty(index = 7)
    private String firstUseDate;

    /**
     * 排序标识
     */
    @ExcelProperty(index = 8)
    private String sortId;

    /**
     * 备注
     */
    @ExcelProperty(index = 9)
    private String memo;
}
