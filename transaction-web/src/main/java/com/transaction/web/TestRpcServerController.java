package com.transaction.web;

import com.transaction.common.entity.Account;
import com.transaction.common.entity.Goods;
import com.transaction.common.entity.Order;
import com.transaction.common.entity.TransactionLog;
import com.transaction.common.service.AccountService;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.OrderService;
import com.transaction.common.service.TransactionLogService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


/**
 * 测试 dubbo 服务
 *
 * @author xhga
 * @since 2019-04-03
 */
@RestController
@RequestMapping("/hello")
public class TestRpcServerController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionLogService transactionLogService;

    /*-----------------------------别看了,有鬼----------------------------*/
    /**
     * 测试rpc服务
     * @return
     */
    @RequestMapping("/testLog")
    @ResponseBody
    public String testLog(){
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setCentreNo("test");
        transactionLog.setPrepareCount(2);
        transactionLog.setCount(2);
        transactionLog.setFailedCount(1);
        int i = transactionLogService.addTransactionLog(transactionLog);
        return "操作:" + i;
    }

    @RequestMapping("/testAccount")
    @ResponseBody
    public String testAccount(){
        Account account = accountService.getAccount(1);
        return "账户详情:" + account == null ? null : account.toString();
    }

    @RequestMapping("/testGoods")
    @ResponseBody
    public String testGoods(){
        Goods goods = goodsService.getGoods(1);
        System.out.println(goods);
        return "商品详情:" + goods == null ? null : goods.toString();
    }

    @RequestMapping("/testOrder")
    @ResponseBody
    public String testOrder(@Param("count") int count){
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setOrderMoney(1.1);
        orderService.addOrder(order);
        return "ok";

    }


}
