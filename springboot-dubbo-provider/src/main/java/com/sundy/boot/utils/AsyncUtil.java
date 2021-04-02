package com.sundy.boot.utils;

import com.sundy.boot.exception.BizException;
import com.sundy.boot.exception.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author plus.wang
 * @description 异步工具
 * @date 2019-09-17
 */
@Slf4j
public class AsyncUtil {

    public static class DefinedCompletableFuture<T> {

        private CompletableFuture<T> completableFuture;

        DefinedCompletableFuture(CompletableFuture<T> completableFuture) {
            this.completableFuture = completableFuture;
        }

        /**
         * 执行失败，或者超时，返回默认值。
         */
        public T get(long timeout, TimeUnit timeUnit, T defaultValue) {
            try {
                return completableFuture.get(timeout, timeUnit);
            } catch (Exception e) {
                log.error("completableFuture.get error ", e);
            }
            return defaultValue;
        }

        public T get(long timeout, TimeUnit timeUnit) throws Exception {
            return completableFuture.get(timeout, timeUnit);
        }
    }

    public static <U> DefinedCompletableFuture<U> defineAsyncCall(Executor executor, Supplier<U> supplier) {
        CompletableFuture<U> completableFuture = CompletableFuture.supplyAsync(supplier, executor);
        return new DefinedCompletableFuture<>(completableFuture);
    }

    public static <T> Future<T> asyncCall(ThreadPoolTaskExecutor threadPoolTaskExecutor, Supplier<T> supplier) {
        return threadPoolTaskExecutor.submit(supplier::get);
    }

    public static <T> Future<T> asyncCall(ExecutorService executorService, Supplier<T> supplier) {
        return executorService.submit(supplier::get);
    }

    public static <T> Future<T> asyncCallableCall(ExecutorService executorService, Callable<T> callable) {
        return executorService.submit(callable);
    }

    public static <T> Future<T> asyncCallableCall(ThreadPoolTaskExecutor threadPoolTaskExecutor, Callable<T> callable) {
        return threadPoolTaskExecutor.submit(callable);
    }

    public static <T> T futureGet(Future<T> future, int timeOut, TimeUnit timeUnit, T defaultValue) {
        try {
            return future.get(timeOut, timeUnit);
        } catch (Exception e) {
            log.error("future.get error ", e);
        }
        return defaultValue;
    }

    public static <T> T asyncGet(ThreadPoolTaskExecutor threadPoolTaskExecutor, Supplier<T> supplier, int defaultTimeOut, TimeUnit timeUnit) {
        Future<T> future = asyncCall(threadPoolTaskExecutor, supplier);
        try {
            return future.get(defaultTimeOut, timeUnit);
        } catch (Exception e) {
            log.error("AsyncUtil ThreadPoolTaskExecutor asyncGet error ", e);
            throw new BizException(ErrorCodeEnum.BIZ_ERROR);
        }
    }

    public static <T> T asyncGet(ExecutorService executorService, Supplier<T> supplier, int defaultTimeOut, TimeUnit timeUnit) {
        Future<T> future = asyncCall(executorService, supplier);
        try {
            return future.get(defaultTimeOut, timeUnit);
        } catch (Exception e) {
            log.error("AsyncUtil ExecutorService asyncGet error ", e);
            throw new BizException(ErrorCodeEnum.BIZ_ERROR);
        }
    }
}
