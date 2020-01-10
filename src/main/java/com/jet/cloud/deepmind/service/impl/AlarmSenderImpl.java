package com.jet.cloud.deepmind.service.impl;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.util.*;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.model.AlarmMsgVO;
import com.jet.cloud.deepmind.model.AlarmSenderVO;
import com.jet.cloud.deepmind.model.AppTencentVO;
import com.jet.cloud.deepmind.model.SolutionMsgVO;
import com.jet.cloud.deepmind.service.AlarmSender;
import com.jet.cloud.deepmind.service.application.SysParameterBean;
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
    @Autowired
    private AppSendTencentUtils appSendTencentUtils;
    @Value("${huawei.cloud.flag}")
    private Boolean flag;
    @Value("${app.cloud.flag}")
    private Boolean appFlag;
    @Value("${app.appid}")
    private String appId;
    @Value("${app.secretkey}")
    private String secretKey;
    @Autowired
    private SysParameterBean sysParameterBean;

    public EmailUtils getEmailUtils() {
        emailUtils.setFrom(sysParameterBean.currentLangPlateTitle("PlatformName"));
        return emailUtils;
    }

    @Override
    public void emailSend(Multimap<String, Alarm> emailMap) {

        for (String addr : emailMap.keySet()) {
            AlarmSenderVO senderVO = AlarmSenderVO.getInstance(emailMap.get(addr));
            try {
                getEmailUtils().sendHtmlTemplate(addr, senderVO.getTitle(), senderVO.getEmailContext(), "templates/alarmTemplate.html");
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

    @Override
    public void sendAppTencent(List<AppTencentVO> appTencentVOS) {
        if (appFlag) {
            if (StringUtils.isNotNullAndEmpty(appTencentVOS)) {
                try {
                    String encode = StringUtils.encode((appId + ":" + secretKey).getBytes());
                    log.info("腾讯移动推送info={}", appTencentVOS);
                    for (AppTencentVO appTencentVO : appTencentVOS) {
                        Boolean send = appSendTencentUtils.send(appTencentVO, encode);
                        if (send) {
                            log.info("腾讯移动推送，成功");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("腾讯移动推送，失败={}", e.getMessage());
                }
            }
        }
    }
}
