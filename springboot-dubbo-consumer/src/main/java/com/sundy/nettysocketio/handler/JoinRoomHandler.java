package com.sundy.nettysocketio.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.sundy.nettysocketio.message.Room;
import com.sundy.nettysocketio.message.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author plus.wang
 * @description 行情分类处理
 * @date 2018/4/18
 */
@Component
public class JoinRoomHandler {

    private static final Logger logger = LoggerFactory.getLogger(JoinRoomHandler.class);

    public static final ConcurrentHashMap<String, Room> ROOM_MAP = new ConcurrentHashMap<>();

    @Autowired
    private SocketIOServer server;

    @OnEvent(value = "joinRoom")
    public void joinRoom(SocketIOClient client, User user, AckRequest ackRequest) {

        if (user == null) {

            return;
        }

        //判断传入的用户名和房间号是否有效
        if (StringUtils.isEmpty(user.getToken()) && StringUtils.isEmpty(user.getExchange()) && StringUtils.isEmpty(user.getMetal())) {

            logger.info("joinRoom user is empty, token: {},exchange: {},metal: {}", user.getToken(), user.getExchange(), user.getMetal());

            //参数全部为空时  默认推送推荐行情 recommend

            String roomName = "recommend";

            joinRoom(client, roomName);

        } else if (!StringUtils.isEmpty(user.getExchange())) {

            String roomName = user.getExchange();

            joinRoom(client, roomName);

        } else if (!StringUtils.isEmpty(user.getMetal())) {

            String roomName = user.getMetal();

            joinRoom(client, roomName);

        } else if (!StringUtils.isEmpty(user.getToken())) {

            //每个用户自己的订阅行情
            String roomName = user.getToken();

            joinRoom(client, roomName);
        }
    }

    private void joinRoom(SocketIOClient client, String roomName) {

        Set<String> clientAllRooms = client.getAllRooms();

        clientAllRooms.forEach(client::leaveRoom);

        client.set("roomName", roomName);

        client.joinRoom(roomName);

        Room room = new Room(roomName);

        ROOM_MAP.putIfAbsent(roomName, room);

        server.getRoomOperations(roomName).sendEvent("sys", client.getSessionId() + " JION ROOM : " + roomName);
    }

}
