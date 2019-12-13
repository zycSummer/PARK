package com.jet.cloud.deepmind.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import lombok.Data;

import javax.persistence.Convert;

/**
 * @author maohandong
 * @create 2019/11/20 16:29
 * @desc 报表对象明细导入模板
 */
@Data
public class MeterModel extends BaseRowModel {
    /**
     * 仪表标识
     */
    @ExcelProperty(index = 0)
    private String meterId;

    /**
     * 仪表名称
     */
    @ExcelProperty(index = 1)
    private String meterName;

    /**
     * 能源种类标识
     */
    @ExcelProperty(index = 2)
    private String energyTypeId;

    /**
     * 排序标识
     */
    @ExcelProperty(index = 3)
    private String sortId;

    /**
     * 是否参与负荷排名
     */
    @ExcelProperty(index = 4)
    private String isRanking;

    /**
     * 备注
     */
    @ExcelProperty(index = 5)
    private String memo;
}
