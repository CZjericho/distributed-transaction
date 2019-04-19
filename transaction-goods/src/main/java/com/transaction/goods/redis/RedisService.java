package com.transaction.goods.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * redis服务类
 * Created by xhga on 2019/4/19.
 */
@Repository
public class RedisService implements RedisApi {
    private Logger logger = Logger.getLogger(RedisService.class);

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean isConnection(){
        try {
            redisTemplate.opsForValue().get("luck");
        } catch (Exception e) {
            logger.info("Redis service is not open.");
            return false;
        }
        logger.info("Redis service is open.");
        return true;
    }

    @Override
    public void add(String key, Object value){
        redisTemplate.opsForValue().set(key, value.toString());
    }

    @Override
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void increase(String key, int number) {
        System.out.println("------添加redis数量------");
        redisTemplate.opsForValue().increment(key, number);
    }

    @Override
    public int decrement(String key, int number) {
        return redisTemplate.opsForValue().decrement(key, number).intValue();
    }

    @Override
    public void addList(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void addList(String key, List<String> list) {
        redisTemplate.opsForList().leftPushAll(key,list);
    }

    @Override
    public String pop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
}
