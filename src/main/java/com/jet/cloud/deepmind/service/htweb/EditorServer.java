package com.jet.cloud.deepmind.service.htweb;


import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileFilter;

@org.springframework.context.annotation.Configuration
public class EditorServer {

    @Value("${ws.server.host}")
    private String host;

    @Value("${ws.server.port}")
    private Integer port;

    @Value("${ws.server.maxHttpContentLength}")
    private Integer maxHttpContentLength;

    @Value("${ws.server.maxFramePayloadLength}")
    private Integer maxFramePayloadLength;

    @Value("${storage}")
    private String storagePath = "instance/storage";

    @Bean
    public SocketIOServer socketIOServer() {

        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setMaxHttpContentLength(maxHttpContentLength);
        config.setMaxFramePayloadLength(maxFramePayloadLength);
        // 身份验证
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                // http://localhost:8081?username=test&password=test
                // 例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息
                // String username = data.getSingleUrlParam("username");
                // String password = data.getSingleUrlParam("password");
                return true;
            }
        });

        final SocketIOServer server = new SocketIOServer(config);
        return server;
    }

    @Bean
    public FileAlterationMonitor fileAlterationMonitor() {
        FileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), FileFilterUtils.fileFileFilter());
        FileAlterationObserver filealtertionObserver = new FileAlterationObserver(storagePath, filter);
        FileAlterationListener listener = new FileAlterationListener(socketIOServer(), storagePath);
        filealtertionObserver.addListener(listener);

        FileAlterationMonitor filealterationMonitor = new FileAlterationMonitor(1000);
        filealterationMonitor.addObserver(filealtertionObserver);
        return filealterationMonitor;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }

    //public String getHost() {
    //    return host;
    //}
    //
    //public Integer getPort() {
    //    return port;
    //}

    //public JSONObject getHostAndPort() {
    //    JSONObject o = new JSONObject();
    //    o.put("host", getHost());
    //    o.put("port", getPort());
    //    return o;
    //}

    public static void main(String[] args) {
        SpringApplication.run(EditorServer.class, args);
    }
}

