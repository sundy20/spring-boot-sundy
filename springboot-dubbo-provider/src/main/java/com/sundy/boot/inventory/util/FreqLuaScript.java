package com.sundy.boot.inventory.util;

public class FreqLuaScript {
    public static final String INCRBY =
            "local cnt = tonumber(ARGV[1])\n" +
                    "local limit = tonumber(ARGV[2])\n" +
                    "local expire_time = ARGV[3]\n" +
                    "local is_exists = redis.call(\"EXISTS\", KEYS[1])\n" +
                    "if is_exists == 1 then\n" +
                    "    local ttl = redis.call(\"GET\", KEYS[1])\n" +
                    "    if (ttl + cnt) > limit then\n" +
                    "        return 0\n" +
                    "    else\n" +
                    "        redis.call(\"INCRBY\", KEYS[1], cnt)\n" +
                    "        return 1\n" +
                    "    end\n" +
                    "else\n" +
                    "    redis.call(\"SET\", KEYS[1], 1)\n" +
                    "    redis.call(\"EXPIRE\", KEYS[1], expire_time)\n" +
                    "    return 1\n" +
                    "end";

    public static final String DECREASE =
            "local cnt = tonumber(ARGV[1])\n" +
                    "local limit = tonumber(ARGV[2])\n" +
                    "local expire_time = ARGV[3]\n" +
                    "local is_exists = redis.call(\"EXISTS\", KEYS[1])\n" +
                    "if is_exists == 1 then\n" +
                    "    local stock = tonumber(redis.call(\"GET\", KEYS[1]))\n" +
                    "    if stock == -1 then\n" +
                    "        return -1\n" +
                    "    end\n" +
                    "    if stock < cnt then\n" +
                    "        return -1\n" +
                    "    end\n" +
                    "    if (stock >= cnt) then\n" +
                    "        redis.call(\"INCRBY\", KEYS[1], 0 - cnt)\n" +
                    "        return 1\n" +
                    "    end\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
}
