package com.jet.cloud.deepmind.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhuyicheng
 * @create 2019/10/28 10:19
 * @desc 对象展示结构树明细实体类
 */
@Entity
@Data
@Table(name = "tb_obj_org_tree_detail")
public class OrgTreeDetail extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "org_tree_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "展示结构树标识只能输入字母和数字且不能大于20位")
    private String orgTreeId;

    @Column(name = "node_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "节点标识标识只能输入字母和数字且不能大于20位")
    private String nodeId;

    @Column(name = "node_name", nullable = false)
    @Size(min = 1, max = 30, message = "节点名称长度不超过30")
    private String nodeName;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "排序标识只能输入字母和数字且不能大于10位")
    private String sortId;

    @Column(name = "data_source")
    private String dataSource;

    @Transient
    private Double val;
    @Transient
    private String parentName;

    public OrgTreeDetail() {
    }

    public OrgTreeDetail(String nodeName, String sortId) {
        this.nodeName = nodeName;
        this.sortId = sortId;
        this.objType = "a";
        this.objId = "a";
        this.orgTreeId = "a";
        this.nodeId = "a";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrgTreeDetail)) return false;
        if (!super.equals(o)) return false;

        OrgTreeDetail that = (OrgTreeDetail) o;

        if (!objType.equals(that.objType)) return false;
        if (!objId.equals(that.objId)) return false;
        if (!orgTreeId.equals(that.orgTreeId)) return false;
        if (!nodeId.equals(that.nodeId)) return false;
        return dataSource.equals(that.dataSource);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + objType.hashCode();
        result = 31 * result + objId.hashCode();
        result = 31 * result + orgTreeId.hashCode();
        result = 31 * result + nodeId.hashCode();
        return result;
    }
}
