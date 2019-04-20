package com.transaction.common.redis;

import java.util.List;

/**
 * Created by xhga on 2019/4/19.
 */
public interface RedisApi {

    boolean isConnection();

    void add(String key, Object value);

    String get(String key);

    /**
     * 将 key 所储存的值增加指定的增量值。
     * @param key
     * @param number
     */
    void increase(String key, int number);

    /**
     * 将 key 所储存的值减去指定的减量值。
     * @param key
     * @param number
     */
    int decrement(String key, int number);

    void addList(String key, String value);

    void addList(String key, List<String> list);

    String pop(String key);
}
