package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jet.cloud.deepmind.common.converter.String2BooleanConverter;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.StringJoiner;

/**
 * @author zhuyicheng
 * @create 2019/11/11 13:54
 * @desc 对象报警信息
 */
@Data
@Entity
@Table(name = "tb_obj_alarm_msg")
public class AlarmMsg extends BaseEntity {
    @Column(name = "obj_type", nullable = false)
    private String objType;
    @Column(name = "obj_id", nullable = false)
    private String objId;
    @Column(name = "alarm_id", nullable = false)
    private String alarmId;
    @Column(name = "alarm_name", nullable = false)
    private String alarmName;
    @Column(name = "alarm_type", nullable = false)
    private String alarmType;
    @Column(name = "alarm_msg")
    private String msg;
    @Column(name = "alarm_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;
    @Column(name = "recovery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recoveryTime;
    @Column(name = "is_ack", nullable = false)
    @Convert(converter = String2BooleanConverter.class)
    private Boolean isAck;

    public String toAlarmMsgString() {
        if (StringUtils.isNullOrEmpty(this.objType, this.objId, this.alarmType)) {
            return null;
        }
        return new StringJoiner(",")
                .add("对象类型：" + this.objType)
                .add("对象标识：" + this.objId)
                .add("报警标识：" + this.alarmId)
                .toString();
    }
}
