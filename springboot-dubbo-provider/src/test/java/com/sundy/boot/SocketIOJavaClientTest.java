package com.sundy.boot;

import com.sundy.boot.socketio.message.RequestMessage;
import com.sundy.boot.utils.JsonConverter;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * @author plus.wang
 * @description
 * @date 2018/4/18
 */
public class SocketIOJavaClientTest {

    private static final Logger logger = LoggerFactory.getLogger(SocketIOJavaClientTest.class);

    private Socket socket;

    public Socket createSocket() throws URISyntaxException {

        IO.Options options = new IO.Options();

        options.transports = new String[]{"websocket"};

        options.reconnectionDelay = 2000;//失败重连的时间间隔

        options.timeout = 3000;//连接超时时间(ms)

        socket = IO.socket("http://localhost:9090", options);

        socket.on(Socket.EVENT_CONNECT, requestMessageArray -> {

            logger.info("Socket.EVENT_CONNECT");

        }).on(Socket.EVENT_CONNECT_ERROR, args -> {

            logger.info("Socket.EVENT_CONNECT_ERROR");

            socket.disconnect();

        }).on(Socket.EVENT_DISCONNECT, args -> {

            logger.info("Socket.EVENT_CONNECT_TIMEOUT");

            socket.disconnect();

        });

        socket.connect();

        return socket;

    }

    public void sendFutures(Socket socket) {

        int i = 1801;

        while (true) {

            RequestMessage requestMessage = new RequestMessage();

            if (i < 1810) {

                requestMessage.setKey("CU" + i);

            } else {

                requestMessage.setKey("CU1810");
            }

            requestMessage.setMessageContent("jsonData" + i);

            i++;

            socket.emit("futures", JsonConverter.objectToJSONObject(requestMessage));

            try {

                Thread.sleep(500);

            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        SocketIOJavaClientTest socketIOJavaClient = new SocketIOJavaClientTest();

        Socket currentSocket = socketIOJavaClient.createSocket();

        socketIOJavaClient.sendFutures(currentSocket);
    }
}
