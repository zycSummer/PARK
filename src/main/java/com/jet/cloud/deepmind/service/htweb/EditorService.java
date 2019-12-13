package com.jet.cloud.deepmind.service.htweb;

import com.corundumstudio.socketio.SocketIOClient;
import com.jet.cloud.deepmind.common.htweb.CommUtil;
import com.jet.cloud.deepmind.common.htweb.FileUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class EditorService {

    /**
     * 异步导出文件
     */
    @Async
    public void export(SocketIOClient client, String[] dir) {
        String fileName = "package";
        if (dir != null && dir.length > 0) {
            fileName = new File(dir[0]).getName();
            fileName = fileName.substring(0, fileName.indexOf("."));
            if (dir.length > 1) {
                fileName += "(+" + dir.length + ")";
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileUtil.export(dir, bos);

        Long tempName = System.currentTimeMillis();
        String tempPath = String.join("/", String.valueOf(tempName), fileName);
        String downloadPath = String.join("/", "download", tempPath);
        CommUtil.fileMapPut(tempPath, bos);
        client.sendEvent("download", downloadPath);
    }
}

