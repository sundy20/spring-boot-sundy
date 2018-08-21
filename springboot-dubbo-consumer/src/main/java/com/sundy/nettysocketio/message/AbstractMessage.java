package com.sundy.nettysocketio.message;

import com.sundy.nettysocketio.message.enums.MessageType;

import java.io.Serializable;

/**
 * @author plus.wang
 * @description 抽象消息体
 * @date 2018/4/18
 */
public abstract class AbstractMessage implements Serializable {

    private static final long serialVersionUID = 4287695641905109810L;

    MessageType messageType;

    String messageContent;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return "AbstractMessage{" +
                "messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }
}
