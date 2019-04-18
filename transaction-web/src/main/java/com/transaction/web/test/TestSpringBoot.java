package com.transaction.web.test;

import com.transaction.common.service.AccountService;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.OrderService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.web.rabbit.RabbitSend;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by HuaWeiBo on 2019/4/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSpringBoot {
    @Autowired
    private AccountService accountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionLogService transactionLogService;

    @Autowired
    RabbitSend rabbitSend;
    @Test
    public void testSend(){
        rabbitSend.sendMessage("hwb");
    }
}
