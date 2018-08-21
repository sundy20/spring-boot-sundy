package com.sundy.nettysocketio.utils;

public class MessageIdUtils {

    public static synchronized String getMsgId() {

        return String.valueOf(System.nanoTime());
    }
}
