package com.jet.cloud.deepmind.service.htweb;


import com.corundumstudio.socketio.SocketIOServer;
import com.jet.cloud.deepmind.common.htweb.CommUtil;
import com.jet.cloud.deepmind.common.htweb.FileUtil;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件变化监听服务
 *
 */
public class FileAlterationListener extends FileAlterationListenerAdaptor {

    private SocketIOServer socketIOServer;

    private String storagePath;

    public FileAlterationListener(SocketIOServer socketIOServer, String storagePath) {
        this.socketIOServer = socketIOServer;
        this.storagePath = storagePath;
    }

    @Override
    public void onDirectoryChange(File directory) {
        sendMessage("change", FileUtil.clearStorage(storagePath, directory.getPath()));
    }

    @Override
    public void onDirectoryCreate(File directory) {
        sendMessage("change", FileUtil.clearStorage(storagePath, directory.getPath()));
    }

    @Override
    public void onDirectoryDelete(File directory) {
        sendMessage("change", FileUtil.clearStorage(storagePath, directory.getPath()));
    }

    @Override
    public void onFileChange(File file) {
        sendMessage("change", FileUtil.clearStorage(storagePath, file.getPath()));
    }

    @Override
    public void onFileCreate(File file) {
        sendMessage("change", FileUtil.clearStorage(storagePath, file.getPath()));
    }

    @Override
    public void onFileDelete(File file) {
        sendMessage("change", FileUtil.clearStorage(storagePath, file.getPath()));
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }

    /**
     * 文件变更通知
     * @param event
     * @param path
     */
    private void sendMessage(String event, String path) {
        Map<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("event", event);
        eventMap.put("path", path);
        CommUtil.watchMapPut(path, System.currentTimeMillis());
        socketIOServer.getBroadcastOperations().sendEvent("fileChanged", eventMap);
    }
}

