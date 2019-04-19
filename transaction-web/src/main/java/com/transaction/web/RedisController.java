package com.transaction.web;

import com.alibaba.fastjson.JSONObject;
import com.transaction.web.entity.CreateOrderRequest;
import com.transaction.web.rabbit.RabbitSend;
import com.transaction.web.redis.RedisApi;
import com.transaction.web.redis.RedisConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

/**
 * 分布式事务-2.解决并发访问
 *
 * 通过mq发送消息
 * Created by HuaWeiBo on 2019/4/19.
 */
@RestController
@RequestMapping("/redis")
@Service
public class RedisController {
    @Autowired
    private RabbitSend rabbitSend;
    @Autowired
    private RedisApi redisApi;

    /**
     * 商品库存添加到缓存
     * （这一步实际上在添加商品时的处理）
     * @param goodsId
     * @param count
     * @return
     */
    @RequestMapping("/addRedis")
    @ResponseBody
    public String addRedis(@Param("goodsId") int goodsId,
                           @Param("count") int count) {
        redisApi.add(RedisConfig.GOODS_COUNT + goodsId, count);
        return "ok";
    }

    /**
     * 通过redis验证库存,
     * 不足直接拒绝
     * @param userId
     * @param goodsId
     * @param count
     * @return
     */
    @RequestMapping("/redisCreateOrder")
    @ResponseBody
    public String redisCreateOrder(@Param("userId") int userId,
                                   @Param("goodsId") int goodsId,
                                   @Param("count") int count) {
        // 减去所购买的商品数量
        int decrement = redisApi.decrement(RedisConfig.GOODS_COUNT + goodsId, count);
        if (decrement < 0) {
            // 设置库存为0(为了创建订单失败时, 将库存重新加入缓存)
            redisApi.add(RedisConfig.GOODS_COUNT + goodsId, 0);
            return "库存不足..";
        }
        CreateOrderRequest request = new CreateOrderRequest(userId, goodsId, count);
        String jsonString = JSONObject.toJSONString(request);
        // 发送创建订单请求
        rabbitSend.sendMessage(jsonString);
        return "有点开心..";
    }

    /**
     * 测试并发test and rabbit send message.
     */
    @RequestMapping("/testRedisCreateOrder")
    @ResponseBody
    public String testRedisCreateOrder(@Param("userId") int userId,
                                      @Param("goodsId") int goodsId,
                                      @Param("count") int count,
                                      @Param("number") int number) {
        CountDownLatch cdl = new CountDownLatch(number);
        for (int i = 0; i < number; i++) {
            new Thread(()->{
                CreateOrderRequest request = new CreateOrderRequest(userId, goodsId, count, number);
                String jsonString = JSONObject.toJSONString(request);
                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int decrement = redisApi.decrement(RedisConfig.GOODS_COUNT + goodsId, count);
                if (decrement < 0) {
                    // 设置库存为0
                    redisApi.add(RedisConfig.GOODS_COUNT + goodsId, 0);
                    System.out.println("---------------拦截---------------");
                    return;
                }
                rabbitSend.sendMessage(jsonString);
            }).start();
            cdl.countDown();
        }
        return "有点开心..";
    }

}
