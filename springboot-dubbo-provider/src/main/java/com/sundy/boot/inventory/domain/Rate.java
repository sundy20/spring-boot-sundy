package com.sundy.boot.inventory.domain;

public class Rate {
    String key;
    Long cnt;

    public Rate() {
    }

    public Rate(String key, Long cnt) {
        this.key = key;
        this.cnt = cnt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }
}
