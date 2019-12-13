package com.jet.cloud.deepmind.service.htweb;

import com.corundumstudio.socketio.SocketIOServer;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerRunner implements CommandLineRunner {

    @Value("${ws.server.start}")
    private Boolean serverStart;

    /** socket.io 服务 */
    private final SocketIOServer socketServer;

    /** 文件监听 服务 */
    private final FileAlterationMonitor fileMonitor;

    @Autowired
    public ServerRunner(SocketIOServer socketServer, FileAlterationMonitor monitor) {
        this.socketServer = socketServer;
        this.fileMonitor = monitor;
    }

    @Override
    public void run(String... args) throws Exception {
        fileMonitor.start();
        if (serverStart) {
            socketServer.start();
        }
    }

}

