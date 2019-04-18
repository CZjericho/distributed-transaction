package com.transaction.web;

import com.alibaba.fastjson.JSONObject;
import com.transaction.common.service.AccountService;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.OrderService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.web.entity.CreateOrderRequest;
import com.transaction.web.rabbit.RabbitSend;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

/**
 * 分布式事务-2.解决并发访问
 *
 * 通过mq发送消息
 * Created by HuaWeiBo on 2019/4/18.
 */
@RestController
@RequestMapping("/concurrence")
public class ConcurrenceController {

    @Autowired
    private RabbitSend rabbitSend;

    /**
     * 通过mq发送消息, 由消费消者生成订单
     * @param userId
     * @param goodsId
     * @param count
     * @return
     */
    @RequestMapping("/sendCreateOrder")
    @ResponseBody
    public String sendCreateOrder(@Param("userId") int userId,
                                  @Param("goodsId") int goodsId,
                                  @Param("count") int count) {
        // 可以先判断库存,余额,生成centreNo
        CreateOrderRequest request = new CreateOrderRequest(userId, goodsId, count);
        String jsonString = JSONObject.toJSONString(request);
        // 发送创建订单请求
        rabbitSend.sendMessage(jsonString);
        return "还是不开心.";
    }

    /**
     * 测试并发rabbit send message
     */
    @RequestMapping("/testSendCreateOrder")
    @ResponseBody
    public String testSendCreateOrder(@Param("userId") int userId,
                                  @Param("goodsId") int goodsId,
                                  @Param("count") int count,
                                  @Param("number") int number) {
        CreateOrderRequest request = new CreateOrderRequest(userId, goodsId, count, number);
        String jsonString = JSONObject.toJSONString(request);
        CountDownLatch cdl = new CountDownLatch(number);
        for (int i = 0; i < number; i++) {
            new Thread(()->{
                try {
                    cdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rabbitSend.sendMessage(jsonString);
            }).start();
            cdl.countDown();
        }
        return "还是不开心.";
    }
}
