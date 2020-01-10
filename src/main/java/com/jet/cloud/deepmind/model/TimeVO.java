package com.jet.cloud.deepmind.model;

import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhuyicheng
 * @create 2019/12/25 10:24
 * @desc TimeVO
 */
@Data
public class TimeVO implements Serializable {
    private static final long serialVersionUID = 3766680458691839524L;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TimeUnit timeUnit;
}
