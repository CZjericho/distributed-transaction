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
import java.util.Random;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xhga
 * @since 2019-04-03
 */
@RestController
@RequestMapping("/hello")
public class HelloController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionLogService transactionLogService;

    /**
     * 分布式事务实现、有延时
     * (延时指的: transactionLogService.returnFailedCount()方法 )
     * @param userId
     * @param goodId
     * @param count
     * @return
     */
    @RequestMapping("/distributed")
    @ResponseBody
    public String distributedTransaction(@Param("userId") int userId,
                                         @Param("goodId") int goodId,
                                         @Param("count") int count) {
        System.out.println("分布式事务实现：");
        Random random = new Random();
        // 订单no,用做事务日志记录标识
        String no = String.valueOf(random.nextInt(9000) + 1000);
        Goods goods = goodsService.getGoods(goodId);
        // 剩余库存 测试分布式事务时可将库存验证注释点，模拟库存不足回滚事务
        /*int newCount = goods.getGoodsCount() - count;
        if (newCount < 0) {
            return "库存不足.";
        }*/
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setCentreNo(no);
        transactionLog.setCount(3);
        transactionLog.setPrepareCount(3);
        transactionLogService.addTransactionLog(transactionLog);
        double money = goods.getGoodsMoney() * count;
        accountService.updateAccount(userId, money, no);
        Order order = new Order();
        order.setOrderNo(no);
        order.setOrderMoney(money);
        order.setOrderDate(new Date());
        order.setOrderGoodsName(goods.getGoodsName());
        order.setUserId(userId);
        orderService.addOrder(order);
        goodsService.updateCount(goodId, count, no);
        int result = transactionLogService.returnFailedCountNoDelay(no);
        return result==0 ? "success":"fail";
    }

    /**
     * 分布式事务实现、无延时
     * @param userId
     * @param goodId
     * @param count
     * @return
     */
    @RequestMapping("/distributedNoDelay")
    @ResponseBody
    public String distributedTransactionNoDelay(@Param("userId") int userId,
                                         @Param("goodId") int goodId,
                                         @Param("count") int count) {
        System.out.println("分布式事务实现：");
        Random random = new Random();
        // 订单no,用做事务日志记录标识
        String no = String.valueOf(random.nextInt(9000) + 1000);
        Goods goods = goodsService.getGoods(goodId);
        // 剩余库存 测试分布式事务时可将库存验证注释点，模拟库存不足回滚事务
        /*int newCount = goods.getGoodsCount() - count;
        if (newCount < 0) {
            return "库存不足.";
        }*/
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setCentreNo(no);
        transactionLog.setCount(3);
        transactionLog.setPrepareCount(3);
        // 第一步：生成事务日志
        int result = 1;
        try {
            transactionLogService.addTransactionLog(transactionLog);
            double money = goods.getGoodsMoney() * count;
            // 第二步(账户)：业务系统操作->扣钱
            accountService.updateAccountNoDelay(userId, money, no);
            Order order = new Order();
            order.setOrderNo(no);
            order.setOrderMoney(money);
            order.setOrderDate(new Date());
            order.setOrderGoodsName(goods.getGoodsName());
            order.setUserId(userId);
            // 第二步(订单)：业务系统操作->生成订单
            orderService.addOrderNoDelay(order);
            // 第二步(库存)：业务系统操作->减库存
            goodsService.updateCountNoDelay(goodId, count, no);
            // 获取成功或者失败
            result = transactionLogService.returnFailedCountNoDelay(no);
        } catch (Exception e) {
            transactionLogService.updateFailedCount(no);
            System.out.println("--------------服务异常--> 修改失败次数.");
        }
        return result== 0 ? "success":"fail";
    }




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
    public String testOrder(){
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setOrderMoney(1.1);
        // 异步返回0
        orderService.addOrder(order);
        return "ok";

    }


}
