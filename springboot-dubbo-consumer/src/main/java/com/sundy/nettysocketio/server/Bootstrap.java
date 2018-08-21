package com.sundy.nettysocketio.server;

import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Autowired
    private SocketIOServer server;

//    @PostConstruct
    public void start() throws InterruptedException {

        server.start();

        logger.info("--------------server started host: {} port: {}-------------", server.getConfiguration().getHostname(), server.getConfiguration().getPort());
    }

//    @PreDestroy
    public void stop() {

        server.stop();

        logger.info("-------------server stoped-------------");
    }
}
