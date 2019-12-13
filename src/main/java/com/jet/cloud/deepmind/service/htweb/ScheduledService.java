package com.jet.cloud.deepmind.service.htweb;


import com.jet.cloud.deepmind.common.htweb.CommUtil;
import com.jet.cloud.deepmind.common.htweb.PropertiesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author yeshanbao
 * <p>
 * 定时清理缓存中未被下载的文件流信息
 */
@Component
@EnableScheduling
public class ScheduledService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledService.class);

    /**
     * 30 秒清理一次过期文件流
     */
    @Scheduled(fixedDelay = 30 * 1000)
    public void clearFileMap() {
        Long time = Long.valueOf(PropertiesUtil.getProperties("temp.fileostimemillis"));
        CommUtil.clearFileMap(time);
    }

    /**
     * 5分钟 清理一次临时文件夹
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void clearTempDir() {
        String storagePath = PropertiesUtil.getProperties("storage");
        String temp = PropertiesUtil.getProperties("compress.path");
        Long time = Long.valueOf(PropertiesUtil.getProperties("temp.fileostimemillis"));

        File tempDir = new File(storagePath, temp);
        if (tempDir.exists()) {
            for (File file : tempDir.listFiles()) {
                if (!file.isDirectory()) {
                    // 文件， 不处理
                    continue;
                }
                Long createTime = Long.valueOf(file.getName());
                if (System.currentTimeMillis() - createTime > time) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        log.error("Scheduled clearTempDir Error!");
                    }
                }
            }
        }
    }
}

