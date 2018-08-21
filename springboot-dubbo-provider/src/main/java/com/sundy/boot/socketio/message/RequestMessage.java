package com.sundy.boot.socketio.message;

/**
 * @author plus.wang
 * @description 请求体
 * @date 2018/4/18
 */
public class RequestMessage extends AbstractMessage {

    private static final long serialVersionUID = 841863600667716116L;

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "key='" + key + '\'' +
                ", messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }
}
