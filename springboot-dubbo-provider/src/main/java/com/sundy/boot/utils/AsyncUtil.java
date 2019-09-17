package com.sundy.boot.utils;

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
        public T get(long timeout, T defaultValue) {
            try {
                return completableFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("completableFuture.get error ", e);
            }
            return defaultValue;
        }

        public T get(long timeout) throws Exception {
            return completableFuture.get(timeout, TimeUnit.MILLISECONDS);
        }
    }

    public static <U> DefinedCompletableFuture<U> runAsync(Supplier<U> supplier, Executor executor) {
        CompletableFuture<U> completableFuture = CompletableFuture.supplyAsync(supplier, executor);
        return new DefinedCompletableFuture<>(completableFuture);
    }

    public static <T> Future<T> runCallable(Callable<T> callable, ThreadPoolExecutor threadPoolExecutor) {
        return threadPoolExecutor.submit(callable);
    }

    public static <T> Future<T> runCallable(Callable<T> callable, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return threadPoolTaskExecutor.submit(callable);
    }
}
