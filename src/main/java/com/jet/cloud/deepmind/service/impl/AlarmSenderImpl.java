package com.jet.cloud.deepmind.service.impl;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.util.AlarmHttpUtils;
import com.jet.cloud.deepmind.common.util.EmailUtils;
import com.jet.cloud.deepmind.common.util.SmsUtils;
import com.jet.cloud.deepmind.common.util.SolutionHttpUtils;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.model.AlarmMsgVO;
import com.jet.cloud.deepmind.model.AlarmSenderVO;
import com.jet.cloud.deepmind.model.SolutionMsgVO;
import com.jet.cloud.deepmind.service.AlarmSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Log4j2
public class AlarmSenderImpl implements AlarmSender {

    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private AlarmHttpUtils alarmHttpUtils;
    @Autowired
    private SolutionHttpUtils solutionHttpUtils;
    @Value("${huawei.cloud.flag}")
    private Boolean flag;

    @Override
    public void emailSend(Multimap<String, Alarm> emailMap) {

        for (String addr : emailMap.keySet()) {
            AlarmSenderVO senderVO = AlarmSenderVO.getInstance(emailMap.get(addr));
            try {
                emailUtils.sendHtmlTemplate(addr, senderVO.getTitle(), senderVO.getEmailContext(), "templates/alarmTemplate.html");
                log.info("邮件: {} 发送成功:{}", addr, senderVO.getContent());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("邮件:: {} 发送失败:{}", addr, senderVO.getContent());
            }
        }

    }

    @Override
    public void smsSend(Multimap<String, Alarm> phoneMap) {
        for (String addr : phoneMap.keySet()) {
            AlarmSenderVO senderVO = AlarmSenderVO.getInstance(phoneMap.get(addr));
            try {
                Integer count = smsUtils.send(addr, senderVO.getContent());
                log.info("短信: {} ,占用条数：{},发送成功:{}", addr, count, senderVO.getContent());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("短信: {} 发送失败:{}", addr, senderVO.getContent());
            }
        }
    }

    @Override
    public void sendHttpAlarm(List<AlarmMsgVO> alarmMsgVOS) {
        if (flag) {
            if (alarmMsgVOS != null && !alarmMsgVOS.isEmpty()) {
                try {
                    for (AlarmMsgVO alarmMsgVO : alarmMsgVOS) {
                        Boolean send = alarmHttpUtils.send(alarmMsgVO);
                        log.info("调用中软上报报警/恢复信息，成功={}", send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("调用中软上报报警/恢复信息，失败={}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void sendHttpSolution(List<SolutionMsgVO> solutionMsgVOS) {
        if (flag) {
            if (solutionMsgVOS != null && !solutionMsgVOS.isEmpty()) {
                try {
                    for (SolutionMsgVO solutionMsgVO : solutionMsgVOS) {
                        Boolean send = solutionHttpUtils.send(solutionMsgVO);
                        log.info("调用上报优化建议信息，成功={}", send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("调用上报优化建议信息，失败={}", e.getMessage());
                }
            }
        }
    }
}
