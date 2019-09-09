package com.sundy.nettysocketio;

import com.github.rholder.retry.*;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author plus.wang
 * @description guava-retrying测试
 * @date 2019-09-06
 */
public class RetryingTest {

    public static void main(String[] args) {
        testRetrying();
    }

    public static void testRetrying() {
        Retryer<List<String>> retryer = RetryerBuilder.<List<String>>newBuilder()
                //当返回的结果列表为空时，需要重试
                .retryIfResult(resultList -> resultList == null || resultList.size() == 0)
                //当调用方法遇到RuntimeException异常时，重试
                .retryIfExceptionOfType(RuntimeException.class)
                //当重试次数达到3次时，停止重试
                .withStopStrategy(StopStrategies.stopAfterAttempt(20))
                //每次重试间隔的时长为递增       fixedWait(1000, TimeUnit.MILLISECONDS)为1000毫秒
//                .withWaitStrategy(WaitStrategies.incrementingWait(1000, TimeUnit.MILLISECONDS, 2000, TimeUnit
//                .MILLISECONDS))
                //等待斐波那契数列时长
                .withWaitStrategy(WaitStrategies.fibonacciWait(1000, 3, TimeUnit.MINUTES))
                //每次重试间隔需要暂停当前线程
                .withBlockStrategy(BlockStrategies.threadSleepStrategy())
                //每次重试时，需要调用的重试监听器
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("attemptNumber:" + attempt.getAttemptNumber() + ",delaySinceFirstAttempt:" + attempt.getDelaySinceFirstAttempt());
                    }
                })
                .build();
        try {
            retryer.call(RetryingTest::buildNames);
        } catch (ExecutionException | RetryException e) {
            e.printStackTrace();
        }
    }

    private static List<String> buildNames() {
        System.out.println(Thread.currentThread().getName() + " exeucte ...");
        //故意抛出异常
        int i = 1 / 0;
        return Lists.newArrayList("sundy", "jack");
    }


}
