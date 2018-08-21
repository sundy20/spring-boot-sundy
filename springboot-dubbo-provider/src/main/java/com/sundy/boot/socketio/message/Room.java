package com.sundy.boot.socketio.message;

/**
 * @author plus.wang
 * @description 行情类目窗口
 * @date 2018/4/18
 */
public class Room {

    private String name;

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                '}';
    }
}
