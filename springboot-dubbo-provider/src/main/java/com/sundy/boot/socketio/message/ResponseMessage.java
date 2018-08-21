package com.sundy.boot.socketio.message;


import com.sundy.boot.socketio.message.enums.MessageType;

/**
 * @author plus.wang
 * @description 响应消息体
 * @date 2018/4/19
 */
public class ResponseMessage extends AbstractMessage {

    private static final long serialVersionUID = -1983930271958390835L;

    private String roomName;

    public ResponseMessage() {

    }

    public ResponseMessage(String roomName, MessageType messageType, String messageContent) {

        this.roomName = roomName;

        this.messageType = messageType;

        this.messageContent = messageContent;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "roomName='" + roomName + '\'' +
                ", messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }
}
