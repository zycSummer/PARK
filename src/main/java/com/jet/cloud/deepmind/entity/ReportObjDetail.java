package com.jet.cloud.deepmind.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/7 14:57
 * @desc 对象报表对象明细
 */
@Entity
@Data
@Table(name = "tb_obj_report_obj_detail")
public class ReportObjDetail extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "report_id", nullable = false)
    @NotNull(message = "报表标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String reportId;

    @Column(name = "node_id", nullable = false)
    @NotNull(message = "节点标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "只能输入字母和数字且不能大于20位")
    private String nodeId;

    @Column(name = "node_name", nullable = false)
    @NotNull(message = "节点名称不能为空")
    @Size(min = 1, max = 30, message = "节点名称不超过30")
    private String nodeName;

    @Column(name = "parent_id", nullable = false)
    @NotNull(message = "上级节点标识不能为空")
    private String parentId;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字且不能大于10位")
    private String sortId;

    @Column(name = "data_source")
    private String dataSource;

    @Transient
    private String parentName;



}
