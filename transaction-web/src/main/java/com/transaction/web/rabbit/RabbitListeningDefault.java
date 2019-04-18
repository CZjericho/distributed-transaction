package com.transaction.web.rabbit;

import com.alibaba.fastjson.JSONObject;
import com.transaction.common.entity.Goods;
import com.transaction.common.entity.Order;
import com.transaction.common.entity.TransactionLog;
import com.transaction.common.service.AccountService;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.OrderService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.web.entity.CreateOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 普通消息监听
 * 生成订单
 * Created by Hua wb on 2018/12/11.
 */
@Component
@EnableRabbit
@Configuration
@RabbitListener(queues = RabbitConfig.QUEUE_DEFAULT)
public class RabbitListeningDefault {

    @Autowired
    private AccountService accountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionLogService transactionLogService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RabbitHandler
    public void process(String content) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = sdf.format(new Date());
        logger.info("实时消息：" + content + "时间:" + format);
        CreateOrderRequest request = JSONObject.parseObject(content, CreateOrderRequest.class);
        Integer userId = request.getUserId();
        Integer goodsId = request.getGoodsId();
        Integer count = request.getCount();
        Random random = new Random();
        String no = String.valueOf(random.nextInt(9000) + 1000);
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setCentreNo(no);
        transactionLog.setCount(3);
        transactionLog.setPrepareCount(3);
        transactionLogService.addTransactionLog(transactionLog);
        Goods goods = goodsService.getGoods(goodsId);
        double money = goods.getGoodsMoney() * count;
        accountService.updateAccountSafe(userId, money, no);
        Order order = new Order();
        order.setOrderNo(no);
        order.setOrderMoney(money);
        order.setOrderDate(new Date());
        order.setOrderGoodsName(goods.getGoodsName());
        order.setUserId(userId);
        orderService.addOrderNoDelay(order);
        goodsService.updateCountSafe(goodsId, count, no);
    }

}

