package com.jet.cloud.deepmind.entity;

import com.jet.cloud.deepmind.annotation.EnumValid;
import com.jet.cloud.deepmind.enums.AlarmEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author zhuyicheng
 * @create 2019/11/11 13:54
 * @desc 对象报警条件设置
 */
@Data
@Entity
@Table(name = "tb_obj_alarm_condition")
public class AlarmCondition extends BaseEntity {
    private static final long serialVersionUID = -4432002367698056217L;
    @Column(name = "obj_type", nullable = false)
    private String objType;
    @Column(name = "obj_id", nullable = false)
    private String objId;
    @Column(name = "alarm_id", nullable = false)
    @NotNull(message = "报警标识不能为空")
    private String alarmId;
    @Pattern(regexp = "^[A-Za-z0-9]{0,10}$", message = "只能输入字母和数字")
    @Column(name = "alarm_condition_id", nullable = false)
    private String alarmConditionId;
    @Column(name = "data_source")
    private String dataSource;
    /**
     * 数值类型
     */
    @Column(name = "value_type", nullable = false)
    @EnumValid(message = "数值类型不存在", target = AlarmEnum.ValueType.class)
    private String valueType;
    /**
     * 条件1操作符
     */
    @Column(name = "condition1_op", nullable = false)
    @EnumValid(message = "条件1操作符类型不存在", target = AlarmEnum.RelationalOperator.class)
    private String condition1Op;
    @Column(name = "condition1_value")
    private Double condition1Value;

    @Column(name = "conditions_logic")
    @EnumValid(message = "多条件间关系只能是AND或OR", target = AlarmEnum.LogicalOperator.class)
    private String conditionsLogic;

    @EnumValid(message = "条件2操作符类型不存在", target = AlarmEnum.RelationalOperator.class)
    @Column(name = "condition2_op")
    private String condition2Op;
    @Column(name = "condition2_value")
    private Double condition2Value;
    @Column(name = "alarm_msg")
    private String alarmMsg;
    @Column(name = "sort_id")
    private String sortId;

    /**
     * 存储报警是实时库查询的值
     */
    @Transient
    private Number val;
}
