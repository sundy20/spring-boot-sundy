package com.sundy.nettysocketio.handler;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.sundy.nettysocketio.message.RequestMessage;
import com.sundy.nettysocketio.message.ResponseMessage;
import com.sundy.nettysocketio.message.Room;
import com.sundy.nettysocketio.message.enums.MessageType;
import com.sundy.nettysocketio.utils.CleanJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author plus.wang
 * @description 行情消息处理
 * @date 2018/4/18
 */
@Component
public class FutureMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(FutureMessageHandler.class);

    private static final ConcurrentHashMap<String, String> FUTURES_MAP = new ConcurrentHashMap<>();

    @Autowired
    private SocketIOServer server;

    @OnConnect
    public void onConnect(SocketIOClient client) {

        String uuid = client.getSessionId().toString();

        logger.info(uuid + "  onConnect");
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {

        logger.info("客户端断开连接, sessionId=" + client.getSessionId().toString());

        client.disconnect();
    }

    @OnEvent(value = "message")
    public void message(SocketIOClient client, RequestMessage requestMessage, AckRequest ackRequest) {

        if (null == requestMessage) {

            return;
        }

        String roomName = client.get("roomName");

        if (!StringUtils.isEmpty(roomName)) {

            ResponseMessage responseMessage = new ResponseMessage(roomName, MessageType.TEXT, roomName);

            server.getRoomOperations(roomName).sendEvent("message", responseMessage);

        } else {

            logger.info("'{}' send message reject  '{}'", client.getSessionId(), client.getRemoteAddress());
        }
    }

    @OnEvent(value = "futures")
    public void futures(SocketIOClient client, RequestMessage requestMessage, AckRequest ackRequest) {

        if (null == requestMessage) {

            return;
        }

        String key = requestMessage.getKey();

        String jsonData = requestMessage.getMessageContent();

        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(jsonData)) {

            FUTURES_MAP.put(key, jsonData);

            sendMessageToAllRooms();

            client.sendEvent("message", "success");

        } else {

            client.sendEvent("message", "failure jsonData is empty");
        }
    }

    /**
     * 给所有房间推送消息
     */
    private void sendMessageToAllRooms() {

        ConcurrentHashMap<String, Room> roomConcurrentHashMap = JoinRoomHandler.ROOM_MAP;

        roomConcurrentHashMap.forEach((roomName, room) -> {

            ResponseMessage responseMessage = new ResponseMessage(roomName, MessageType.TEXT, CleanJsonUtil.clean(JSON.toJSONString(FUTURES_MAP)));

            //TODO 根据roomName推送对应的行情列表
            server.getRoomOperations(roomName).sendEvent("message", responseMessage);

        });
    }

    /**
     * 给所有连接客户端推送消息
     *
     * @param eventType 推送的事件类型
     * @param message   推送的内容
     */
    public void sendMessageToAllClients(String eventType, String message) {

        Collection<SocketIOClient> clients = server.getAllClients();

        for (SocketIOClient client : clients) {

            client.sendEvent(eventType, message);
        }
    }
}
