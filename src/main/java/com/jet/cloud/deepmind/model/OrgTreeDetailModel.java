package com.jet.cloud.deepmind.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @author maohandong
 * @create 2019/11/21 16:43
 */
@Data
public class OrgTreeDetailModel extends BaseRowModel {
    /**
     * 节点id
     */
    @ExcelProperty(index = 0)
    private String nodeId;

    /**
     * 节点名称
     */
    @ExcelProperty(index = 1)
    private String nodeName;

    /**
     * 父节点id
     */
    @ExcelProperty(index = 2)
    private String parentId;

    /**
     * 排序标识
     */
    @ExcelProperty(index = 3)
    private String sortId;

    /**
     * 数据源
     */
    @ExcelProperty(index = 4)
    private String dataSource;

    /**
     * 备注
     */
    @ExcelProperty(index = 5)
    private String memo;
}
