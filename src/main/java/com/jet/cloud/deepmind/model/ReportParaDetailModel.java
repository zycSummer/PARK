package com.jet.cloud.deepmind.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @author maohandong
 * @create 2019/11/19 10:32
 * @desc 报表参数明细导入模板
 */
@Data
public class ReportParaDetailModel extends BaseRowModel {
    /**
     * 能源参数标识
     */
    @ExcelProperty(value = "energy_para_id",index = 0)
    private String energyParaId;

    /**
     * 展示名称
     */
    @ExcelProperty(value = "display_name",index = 1)
    private String displayName;

    /**
     * 时刻值
     */
    @ExcelProperty(value = "time_value",index = 2)
    private String timeValue;

    /**
     * 最大值
     */
    @ExcelProperty(value = "max_value",index = 3)
    private String maxValue;

    /**
     * 最小值
     */
    @ExcelProperty(value = "min_value",index = 4)
    private String minValue;

    /**
     * 平均值
     */
    @ExcelProperty(value = "avg_value",index = 5)
    private String avgValue;

    /**
     * 差值
     */
    @ExcelProperty(value = "diff_value",index = 6)
    private String diffValue;

    /**
     * 排序标识
     */
    @ExcelProperty(value = "sort_id",index = 7)
    private String sortId;

    /**
     * 备注
     */
    @ExcelProperty(value = "memo",index = 8)
    private String memo;
}
