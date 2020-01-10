package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.annotation.EnumValid;
import com.jet.cloud.deepmind.common.converter.EmailConverter;
import com.jet.cloud.deepmind.common.converter.MobileConverter;
import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.enums.AlarmEnum;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author zhuyicheng
 * @create 2019/11/11 13:49
 * @desc 对象报警
 */
@Data
@Entity
@Table(name = "tb_obj_alarm")
public class Alarm extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    @NotNull(message = "对象类型不能为空")
    private String objType;

    @Column(name = "obj_id", nullable = false)
    @NotNull(message = "对象标识不能为空")
    private String objId;

    @Column(name = "alarm_id", nullable = false)
    @NotNull(message = "报警标识不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{0,10}$", message = "只能输入字母、数字、下划线组合且不能大于10位")
    private String alarmId;

    @Column(name = "alarm_name", nullable = false)
    @NotNull(message = "报警名称不能为空")
    @Size(min = 1, max = 30, message = "报警名称长度不超过30")
    private String alarmName;

    @Column(name = "alarm_type", nullable = false)
    @NotNull(message = "报警类型不能为空")
    @Size(min = 1, max = 30, message = "报警类型长度不超过30")
    private String alarmType;

    @Column(name = "is_use", nullable = false)
    @Convert(converter = String2BooleanConverter.class)
    private Boolean isUse;

    @EnumValid(message = "多条件间关系只能是AND或OR", target = AlarmEnum.RelationalOperator.class)
    @Column(name = "multi_conditions_logic", nullable = false)
    @NotNull(message = "多条件间关系不能为空")
    private String multiConditionsLogic;

    @Column(name = "msg_recv")
    @Convert(converter = MobileConverter.class)
    private List<String> msgRecv;

    @Column(name = "mail_recv")
    @Convert(converter = EmailConverter.class)
    private List<String> mailRecv;


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "obj_id", referencedColumnName = "obj_id", insertable = false, updatable = false),
            @JoinColumn(name = "obj_type", referencedColumnName = "obj_type", insertable = false, updatable = false),
            @JoinColumn(name = "alarm_id", referencedColumnName = "alarm_id", insertable = false, updatable = false)
    })
    @JsonIgnore
    private Set<AlarmCondition> alarmConditionSet;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "obj_id", referencedColumnName = "obj_id", insertable = false, updatable = false),
            @JoinColumn(name = "obj_type", referencedColumnName = "obj_type", insertable = false, updatable = false),
            @JoinColumn(name = "alarm_id", referencedColumnName = "alarm_id", insertable = false, updatable = false)
    })
    @JsonIgnore
    private Set<AlarmMsg> alarmMsgSet;
    @Transient
    private LocalDateTime alarmTime;

    /**
     * 满足报警的报警条件
     */
    @Transient
    private List<AlarmCondition> needAlarmConditionList;

    public void setNeedAlarmConditionList(AlarmCondition condition) {
        if (this.needAlarmConditionList == null) this.needAlarmConditionList = new ArrayList<>();
        this.needAlarmConditionList.add(condition);
    }

    public String getAlarmMsg() {
        if (StringUtils.isNullOrEmpty(this.needAlarmConditionList)) return null;
        StringJoiner joiner = new StringJoiner(";");
        for (AlarmCondition condition : needAlarmConditionList) {
            joiner.add(replace(condition.getVal(), condition.getAlarmMsg()));
        }
        return joiner.toString();
    }

    private String replace(Object replacement, String msg) {
        if (StringUtils.isNullOrEmpty(msg)) return null;
        String str = replacement == null ? "null" : replacement.toString();
        return msg.replaceAll("\\{this}", str);
    }

    public String toAlarmString() {
        if (StringUtils.isNullOrEmpty(this.objType, this.objId, this.alarmType)) {
            return null;
        }
        return new StringJoiner(",")
                .add("对象类型：" + this.objType)
                .add("对象标识：" + this.objId)
                .add("报警标识：" + this.alarmId)
                .add("报警类型：" + this.alarmType)
                .add("报警时间：" + DateUtil.localDateTimeToString(this.alarmTime == null ? LocalDateTime.now() : this.alarmTime))
                .add("报警内容：" + getAlarmMsg()).toString();
    }
}
