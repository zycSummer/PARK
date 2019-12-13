package com.jet.cloud.deepmind.service.scheduler;

import com.jet.cloud.deepmind.config.AppConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author yhy
 * @create 2019-11-18 16:17
 */
@Log4j2
@Configuration
public class TaskRunnable {

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private AlarmSchedulerTask alarmSchedulerTask;

    @PostConstruct
    public void check() {
        if (StringUtils.isEmpty(appConfig.getAlarmCron())) {
            log.info("未配置cron表达式，报警程序停止");
            return;
        }
        taskScheduler.schedule(() -> {
            log.info("报警程序任务开始");
            try {
                alarmSchedulerTask.check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, new CronTrigger(appConfig.getAlarmCron()));
    }
}
