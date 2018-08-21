package com.sundy.boot.limiter;

import java.util.concurrent.atomic.AtomicInteger;

public class StatItem {

    private String name;

    private long lastResetTime;

    private long interval;

    private AtomicInteger token;

    private int rate;

    public StatItem(String name, int rate, long interval) {
        this.name = name;
        this.rate = rate;
        this.interval = interval;
        this.lastResetTime = System.currentTimeMillis();
        this.token = new AtomicInteger(rate);
    }

    public boolean isAllowable() {

        long now = System.currentTimeMillis();

        if (now > lastResetTime + interval) {

            token.set(rate);

            lastResetTime = now;
        }

        int value = token.get();

        boolean flag = false;

        while (value > 0 && !flag) {

            flag = token.compareAndSet(value, value - 1);

            value = token.get();
        }

        return flag;
    }

    public boolean isOutOfDate() {

        long now = System.currentTimeMillis();

        if (now > lastResetTime + 60 * 60 * 1000) {

            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "StatItem{" +
                "name='" + name + '\'' +
                ", lastResetTime=" + lastResetTime +
                ", interval=" + interval +
                ", token=" + token +
                ", rate=" + rate +
                '}';
    }
}
