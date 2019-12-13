package com.jet.cloud.deepmind.service.htweb;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.jet.cloud.deepmind.common.htweb.FileUtil;
import com.jet.cloud.deepmind.common.htweb.PropertiesUtil;
import com.jet.cloud.deepmind.model.htweb.FileContent;
import com.jet.cloud.deepmind.model.htweb.ImportFile;
import com.jet.cloud.deepmind.model.htweb.RenameEntity;
import org.apache.commons.io.FileExistsException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 编辑器操作实现
 *
 * @author yeshanbao
 */
@Component
public class EditorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(EditorEventHandler.class);

    @Value("${storage}")
    private String storagePath = "instance/storage";

    @Value("${custom}")
    private String customPath = "instance/custom";

    @Value("${compress.path}")
    private String targetPath = "temp";

    private final SocketIOServer server;

    @Autowired
    private EditorService editorService;

    @Autowired
    public EditorEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("创建链接");
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("链接断开");
    }

    @OnEvent(value = "ping")
    public void ping(SocketIOClient client, AckRequest request, Integer cookie, String pingCookie) {

        sendMessage(client, "operationDone", cookie, pingCookie);
    }

    /**
     * 获取文件夹下所有内容
     *
     * @param client
     * @param cookie
     * @param dir
     */
    @OnEvent(value = "explore")
    public void explore(SocketIOClient client, Integer cookie, String dir) {

        sendMessage(client, "operationDone", cookie, FileUtil.explore(dir));
    }

    /**
     * 新建文件夹
     *
     * @param client
     * @param cookie
     * @param dir
     */
    @OnEvent(value = "mkdir")
    public void mkdir(SocketIOClient client, Integer cookie, String dir) {
        try {
            FileUtil.mkdir(dir);
            sendMessage(client, "operationDone", cookie, true);
        } catch (FileExistsException e) {
            sendMessage(client, "operationDone", cookie, false);
        }
    }

    /**
     * 上传文件
     *
     * @param client
     * @param cookie
     * @param content
     */
    @OnEvent(value = "upload")
    public void upload(SocketIOClient client, Integer cookie, FileContent content) {
        try {
            Map<String, Object> resultMap = FileUtil.upload(content);
            if (resultMap != null) {
                client.sendEvent("confirm", resultMap.get("tempName"), resultMap.get("paths"));
            }
            sendMessage(client, "operationDone", cookie, true);
        } catch (Exception e) {
            sendMessage(client, "operationDone", cookie, false);
        }
    }

    ;

    /**
     * 文件名修改
     *
     * @param client
     * @param cookie
     * @param entity
     */
    @OnEvent(value = "rename")
    public void rename(SocketIOClient client, Integer cookie, RenameEntity entity) {
        try {
            FileUtil.rename(entity.getOld(), entity.getNewFile());
            sendMessage(client, "operationDone", cookie, true);
        } catch (IOException e) {
            sendMessage(client, "operationDone", cookie, false);
        }
    }

    /**
     * 删除文件
     *
     * @param client
     * @param cookie
     * @param dir
     */
    @OnEvent(value = "remove")
    public void remove(SocketIOClient client, Integer cookie, String dir) {
        Boolean result = FileUtil.remove(dir);
        sendMessage(client, "operationDone", cookie, result);
    }

    @OnEvent(value = "open")
    public void open(SocketIOClient client, Integer cookie, String dir) {
        sendMessage(client, "operationDone", cookie, false);
    }

    @OnEvent(value = "locate")
    public void locate(SocketIOClient client, Integer cookie, String dir) {
        sendMessage(client, "operationDone", cookie, false);
    }

    /**
     * 获取文件内容
     *
     * @param client
     * @param cookie
     * @param path
     */
    @OnEvent(value = "source")
    public void source(SocketIOClient client, Integer cookie, String path) {
        String result = FileUtil.readeFile(path);
        sendMessage(client, "operationDone", cookie, result);
    }

    /**
     * 打包
     *
     * @param client
     * @param cookie
     * @param path
     */
    @OnEvent(value = "export")
    public void export(SocketIOClient client, Integer cookie, String[] path) {
        log.debug("path size: {}", path.length);
        editorService.export(client, path);
        sendMessage(client, "operationDone", cookie, true);
    }

    /**
     * 资源导入确认
     *
     * @param client
     * @param cookie
     */
    @OnEvent(value = "import")
    public void importSource(SocketIOClient client, Integer cookie, ImportFile content) {
        String storagePath = PropertiesUtil.getProperties("storage");
        String temp = PropertiesUtil.getProperties("compress.path");
        String tempPath = String.join("/", storagePath, temp, content.getPath());
        File tempFile = new File(tempPath);
        if (content.getMove()) {
            FileUtil.moveSource(storagePath, tempFile, tempPath.length());
        }
        try {
            FileUtils.deleteDirectory(tempFile);
        } catch (IOException e) {
            log.error("Delete dir error", e);
        }
    }

    @OnEvent(value = "message")
    public void message() {
//        System.err.println("message is send!");
    }


    /**
     * 发送消息给客户端
     *
     * @param client
     * @param event
     * @param cookie
     * @param result
     */
    public void sendMessage(SocketIOClient client, String event, Integer cookie, Object result) {
        client.sendEvent(event, cookie, result);
    }

    public SocketIOServer getServer() {
        return server;
    }
}

