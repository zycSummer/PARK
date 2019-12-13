package com.jet.cloud.deepmind.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @author maohandong
 * @create 2019/11/18 15:17
 * @desc 报表对象明细导入模板
 */
@Data
public class ReportObjDetailModel extends BaseRowModel {
    /**
     * 节点id
     */
    @ExcelProperty(value = "node_id",index = 0)
    private String nodeId;

    /**
     * 节点名称
     */
    @ExcelProperty(value = "node_name",index = 1)
    private String nodeName;

    /**
     * 父节点id
     */
    @ExcelProperty(value = "parent_id",index = 2)
    private String parentId;

    /**
     * 排序标识
     */
    @ExcelProperty(value = "sort_id",index = 3)
    private String sortId;

    /**
     * 数据源
     */
    @ExcelProperty(value = "data_source",index = 4)
    private String dataSource;

    /**
     * 备注
     */
    @ExcelProperty(value = "memo",index = 5)
    private String memo;
}
