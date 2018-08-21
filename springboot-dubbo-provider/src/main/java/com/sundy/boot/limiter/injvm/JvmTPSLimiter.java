package com.sundy.boot.limiter.injvm;

import com.sundy.boot.limiter.StatItem;
import com.sundy.boot.limiter.TPSLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JvmTPSLimiter implements TPSLimiter {

    private final ConcurrentMap<String, StatItem> stats = new ConcurrentHashMap<>();

    /**
     * 默认一秒允许5次请求
     */
    private int rate = 5;

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public boolean isAllowable(String sessionIdAndReqURI) {

        StatItem statItem = stats.get(sessionIdAndReqURI);

        if (statItem == null) {

            long interval = 1000L;

            stats.putIfAbsent(sessionIdAndReqURI, new StatItem(sessionIdAndReqURI, rate, interval));

            statItem = stats.get(sessionIdAndReqURI);
        }

        return statItem.isAllowable();
    }

    public void timerClear() {

        for (String sessionIdAndReqURI : stats.keySet()) {

            StatItem statItem = stats.get(sessionIdAndReqURI);

            if (statItem != null) {

                if (statItem.isOutOfDate()) {

                    stats.remove(sessionIdAndReqURI);
                }
            }
        }
    }

    public ConcurrentMap<String, StatItem> getStats() {

        return stats;
    }
}
