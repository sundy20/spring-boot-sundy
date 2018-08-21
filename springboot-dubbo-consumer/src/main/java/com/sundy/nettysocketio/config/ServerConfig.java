package com.sundy.nettysocketio.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author plus.wang
 * @description socketIO server 配置
 * @date 2018/4/16
 */
@Configuration
public class ServerConfig {

    @Value("${ws.host}")
    private String host;

    @Value("${ws.port}")
    private Integer port;

    @Bean
    public SocketIOServer server() {

        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();

        configuration.setHostname(host);

        configuration.setPort(port);

        configuration.setAuthorizationListener(data -> {
            //TODO
            // 握手协议参数使用JWT的Token认证方案
            String token = data.getSingleUrlParam("token");

            return true;
        });

        return new SocketIOServer(configuration);
    }


    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {

        return new SpringAnnotationScanner(socketServer);
    }
}
