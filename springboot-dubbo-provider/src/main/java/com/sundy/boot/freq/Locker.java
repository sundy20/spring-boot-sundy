package com.sundy.boot.freq;

import com.sundy.boot.utils.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

import java.util.Optional;

@Slf4j
public class Locker {
    public static final int FC_PX_EXPIRE_TIME = 200;
    public static final int FC_SLEEP_TIME = 2 * 1000;
    private JedisCluster jc;
    private FreqRecorder recorder;
    private String lockKey;
    private String featureCode;
    private long cur;

    public Locker(JedisCluster jc, FreqRecorder recorder, String key) {
        this.jc = jc;
        this.featureCode = Md5Utils.featureCode();
        this.lockKey = "lock_" + key;
        this.recorder = recorder;
        this.cur = System.currentTimeMillis();
    }

    public void tryLock() {
        long sleepCnt = 0;
        while (!"OK".equals(jc.set(this.lockKey, this.featureCode + ":" + System.currentTimeMillis(), "nx", "px", FC_PX_EXPIRE_TIME))) {
            try {
                ++sleepCnt;
                Thread.sleep(FC_SLEEP_TIME);
            } catch (Exception ignore) {
            }
        }
        String elapse = String.valueOf(System.currentTimeMillis() - cur);
        this.recorder.setBizCode("lockGain").setStep(String.valueOf(sleepCnt)).setResult(elapse).record();
    }

    public void releaseLock() {
        String elapse = String.valueOf(System.currentTimeMillis() - cur);
        String r = this.jc.get(this.lockKey);
        int testLen = Math.min(Md5Utils.FEATURE_CODE_LEN, this.featureCode.length());
        if (Optional.ofNullable(r)
                .filter(e -> e.length() >= testLen)
                .map(e -> e.substring(0, testLen))
                .map(e -> this.featureCode.equals(e))
                .orElse(false)) {
            this.recorder.setBizCode("lockRelease").setAction("succ").setResult(elapse).record();
            this.jc.del(this.lockKey);
        } else {
            this.recorder.setBizCode("lockRelease").setAction("fail")
                    .setStep(String.valueOf(testLen)).setResult(elapse).record();
            log.error("lockRelease: o:{}, r:{}", this.featureCode, r);
        }
    }
}
