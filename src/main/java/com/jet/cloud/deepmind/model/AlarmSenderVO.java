package com.jet.cloud.deepmind.model;

import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.Alarm;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author yhy
 * @create 2019-11-15 16:32
 */
@Data
public class AlarmSenderVO implements Serializable {
    private static final long serialVersionUID = 4041990444361893801L;
    private String addr;

    private String content;

    private String title;

    public AlarmSenderVO(Collection<Alarm> alarms) {

        StringJoiner content = new StringJoiner(";");
        StringJoiner title = new StringJoiner("],[");
        for (Alarm alarm : alarms) {
            content.add(alarm.toAlarmString());
            title.add("[" + alarm.getObjType() + "-" + alarm.getObjId() + "-" + alarm.getAlarmId() + "：" + alarm.getAlarmName());
        }
        this.title = title.toString() + "]报警";
        this.content = content.toString();
    }

    public AlarmSenderVO() {
    }

    public static AlarmSenderVO getInstance(Collection<Alarm> alarms) {
        if (StringUtils.isNullOrEmpty(alarms)) return new AlarmSenderVO();
        return new AlarmSenderVO(alarms);
    }

    public Map<String, Object> getEmailContext() {
        Map<String, Object> contents = new HashMap<>();
        contents.put("title", this.title);
        contents.put("content", this.content);
        return contents;
    }

}
