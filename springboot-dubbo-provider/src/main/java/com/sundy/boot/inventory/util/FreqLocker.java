package com.sundy.boot.inventory.util;

import com.sundy.boot.utils.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

import java.util.Optional;

@Slf4j
public class FreqLocker {

    private JedisCluster jedisCluster;
    private FreqRecorder recorder;
    private String lockKey;
    private String featureCode;
    private long cur;
    private static final int FEATURE_CODE_LEN = 32;
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "EX";

    public FreqLocker(JedisCluster jedisCluster, FreqRecorder recorder, String key) {
        this.jedisCluster = jedisCluster;
        this.featureCode = Md5Utils.featureCode();
        this.lockKey = "lock_" + key;
        this.recorder = recorder;
        this.cur = System.currentTimeMillis();
    }

    public void tryLock() {
        long sleepCnt = 0;
        while (!"OK".equals(jedisCluster.set(this.lockKey, this.featureCode + ":" + System.currentTimeMillis(), SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, Constants.FC_PX_EXPIRE_TIME))) {
            try {
                ++sleepCnt;
                Thread.sleep(Constants.FC_SLEEP_TIME);
            } catch (Exception ignore) {
            }
        }
        String elapse = String.valueOf(System.currentTimeMillis() - cur);
        this.recorder.setBizCode("lockGain").setStep(String.valueOf(sleepCnt)).setResult(elapse).record();
    }

    public void releaseLock() {
        String elapse = String.valueOf(System.currentTimeMillis() - cur);
        String r = this.jedisCluster.get(this.lockKey);
        int testLen = Math.min(FEATURE_CODE_LEN, this.featureCode.length());
        if (Optional.ofNullable(r)
                .filter(e -> e.length() >= testLen)
                .map(e -> e.substring(0, testLen))
                .map(e -> this.featureCode.equals(e))
                .orElse(false)) {
            this.recorder.setBizCode("lockRelease").setAction("succ").setResult(elapse).record();
            this.jedisCluster.del(this.lockKey);
        } else {
            this.recorder.setBizCode("lockRelease").setAction("fail").setStep(String.valueOf(testLen)).setResult(elapse).record();
            log.error("lockRelease: o:{}, r:{}", this.featureCode, r);
        }
    }
}
