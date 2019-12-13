package com.jet.cloud.deepmind.service;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.model.AlarmMsgVO;
import com.jet.cloud.deepmind.model.SolutionMsgVO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;


public interface AlarmSender {

    @Async
    void emailSend(Multimap<String, Alarm> emailMap);

    @Async
    void smsSend(Multimap<String, Alarm> phoneMap);

    @Async
    void sendHttpAlarm(List<AlarmMsgVO> alarmMsgVOS);

    @Async
    void sendHttpSolution(List<SolutionMsgVO> solutionMsgVOS);
}
