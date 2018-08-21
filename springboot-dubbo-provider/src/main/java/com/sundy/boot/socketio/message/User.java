package com.sundy.boot.socketio.message;

import java.io.Serializable;

/**
 * @author plus.wang
 * @description 行情用户窗口
 * @date 2018/4/18
 */
public class User implements Serializable {

    private static final long serialVersionUID = 9133511210907388743L;

    private String token;

    private String exchange;

    private String metal;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", exchange='" + exchange + '\'' +
                ", metal='" + metal + '\'' +
                '}';
    }
}
