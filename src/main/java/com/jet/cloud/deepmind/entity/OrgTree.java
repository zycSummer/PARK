package com.jet.cloud.deepmind.entity;

import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhuyicheng
 * @create 2019/10/28 10:19
 * @desc 对象展示结构树实体类
 */
@Entity
@Data
@Table(name = "tb_obj_org_tree")
public class OrgTree extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "org_tree_id", nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9]{0,20}$", message = "展示结构树标识只能输入字母和数字且不能大于20位")
    private String orgTreeId;

    @Column(name = "energy_type_id", nullable = false)
    @NotNull(message = "能源种类标识不能为空")
    private String energyTypeId;

    @Column(name = "org_tree_name", nullable = false)
    @Size(min = 1, max = 30, message = "展示结构树名称长度不超过30")
    private String orgTreeName;

    @Column(name = "is_use")
    @Convert(converter = String2BooleanConverter.class)
    private Boolean isUse;

    @Column(name = "sort_id")
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "排序标识只能输入字母和数字且不能大于10位")
    private String sortId;

    @Transient
    private String energyTypeName;
}
