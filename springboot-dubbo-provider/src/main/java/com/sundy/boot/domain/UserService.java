package com.sundy.boot.domain;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserService {
    private long timeOut = RandomUtils.nextLong(1000, 10000);
    private long defaultTime = 5 * 60 * 1000;
    private LoadingCache<String, UserBean> guavaCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(defaultTime + timeOut, TimeUnit.MILLISECONDS)
            .maximumSize(1000)
            .build(new CacheLoader<String, UserBean>() {
                @Override
                public UserBean load(@Nonnull String key) {
                    return getUser(key);
                }
            });

    private Cache<String, UserBean> caffeineCache = Caffeine.newBuilder()
            .refreshAfterWrite(defaultTime + timeOut, TimeUnit.MILLISECONDS)
            .maximumSize(1000)
            .build(this::getUser);

    public UserBean getUser(String username) {
        // 没有此用户直接返回null
        if (!DataSource.getData().containsKey(username)) {
            return null;
        }
        UserBean user = new UserBean();
        Map<String, String> detail = DataSource.getData().get(username);
        user.setUsername(username);
        user.setPassword(detail.get("password"));
        user.setRole(detail.get("role"));
        user.setPermission(detail.get("permission"));
        user.setUserId(Long.valueOf(detail.get("id")));
        return user;
    }
}
