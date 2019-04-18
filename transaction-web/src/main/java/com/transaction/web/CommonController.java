package com.transaction.web;

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
import java.util.concurrent.CountDownLatch;

/**
 * 分布式事务-1.初始实现
 *
 * 存在问题:并发支持特别差
 * Created by HuaWeiBo on 2019/4/18.
 */
@RestController
@RequestMapping("/common")
public class CommonController {

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
     * @param goodsId
     * @param count
     * @return
     */
    @RequestMapping("/distributed")
    @ResponseBody
    public String distributedTransaction(@Param("userId") int userId,
                                         @Param("goodsId") int goodsId,
                                         @Param("count") int count) {
        System.out.println("分布式事务实现：");
        Random random = new Random();
        // 订单no,用做事务日志记录标识
        String centreNo = String.valueOf(random.nextInt(9000) + 1000);
        try {
            System.out.println("--------------添加日志-----------");
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setCentreNo(centreNo);
            transactionLog.setCount(3);
            transactionLog.setPrepareCount(3);

            transactionLogService.addTransactionLog(transactionLog);
        } catch (Exception e) {
            System.out.println("--------------服务异常--> 日志服务异常.");
            return "fail";
        }
        int result = 1;
        try {
            Goods goods = goodsService.getGoods(goodsId);
            // 剩余库存 测试分布式事务时可将库存验证注释点，模拟库存不足回滚事务
            /*int newCount = goods.getGoodsCount() - count;
            if (newCount < 0) {
                return "库存不足.";
            }*/
            double money = goods.getGoodsMoney() * count;
            // 第二步(账户)：业务系统操作->扣钱
            accountService.updateAccount(userId, money, centreNo);
            Order order = new Order();
            order.setOrderNo(centreNo);
            order.setOrderMoney(money);
            order.setOrderDate(new Date());
            order.setOrderGoodsName(goods.getGoodsName());
            order.setUserId(userId);
            // 第二步(订单)：业务系统操作->生成订单
            orderService.addOrder(order);
            // 第二步(库存)：业务系统操作->减库存
            goodsService.updateCount(goodsId, count, centreNo);
            // 获取成功或者失败
            result = transactionLogService.returnFailedCount(centreNo);
        } catch (Exception e) {
            transactionLogService.updateFailedCount(centreNo);
            System.out.println("--------------服务异常--> 修改失败次数.");
        }
        return result== 0 ? "success":"fail";
    }

    /**
     * 分布式事务实现、无延时
     * @param userId
     * @param goodsId
     * @param count
     * @return
     */
    @RequestMapping("/distributedNoDelay")
    @ResponseBody
    public String distributedTransactionNoDelay(@Param("userId") int userId,
                                                @Param("goodsId") int goodsId,
                                                @Param("count") int count) {
        System.out.println("分布式事务实现NoDelay：");
        Random random = new Random();
        // 订单no,用做事务日志记录标识
        String no = String.valueOf(random.nextInt(9000) + 1000);
        try {
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setCentreNo(no);
            transactionLog.setCount(3);
            transactionLog.setPrepareCount(3);
            // 第一步：生成事务日志
            transactionLogService.addTransactionLog(transactionLog);
        } catch (Exception e) {
            System.out.println("----------NoDelay服务异常--> 日志服务异常.");
            return "fail";
        }
        int result = 1;
        try {
            Goods goods = goodsService.getGoods(goodsId);
            // 剩余库存 测试分布式事务时可将库存验证注释点，模拟库存不足回滚事务
            /*int newCount = goods.getGoodsCount() - count;
            if (newCount < 0) {
                return "库存不足.";
            }*/
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
            goodsService.updateCountNoDelay(goodsId, count, no);
            // 获取成功或者失败
            result = transactionLogService.returnFailedCountNoDelay(no);
        } catch (Exception e) {
            transactionLogService.updateFailedCount(no);
            System.out.println("--------------服务异常--> 修改失败次数.");
        }
        return result== 0 ? "success":"fail";
    }

    /**
     * 测试并发
     * 1.0:严格控制余额,库存数量(待完成)
     * @param userId
     * @param goodsId
     * @param count
     * @param number 并发量
     * @return
     */
    @RequestMapping("/testDistributedNoDelay")
    @ResponseBody
    public String testDistributedNoDelay(@Param("userId") int userId,
                                         @Param("goodsId") int goodsId,
                                         @Param("count") int count,
                                         @Param("number") int number) {
        CountDownLatch countDownLatch = new CountDownLatch(number);
        for (int i = 0; i < number; i++) {
            new Thread(()->{
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Random random = new Random();
                String no = String.valueOf(random.nextInt(9000) + 1000);
                TransactionLog transactionLog = new TransactionLog();
                transactionLog.setCentreNo(no);
                transactionLog.setCount(3);
                transactionLog.setPrepareCount(3);
                transactionLogService.addTransactionLog(transactionLog);
                Goods goods = goodsService.getGoods(goodsId);
                double money = goods.getGoodsMoney() * count;
                accountService.updateAccountNoDelay(userId, money, no);
                Order order = new Order();
                order.setOrderNo(no);
                order.setOrderMoney(money);
                order.setOrderDate(new Date());
                order.setOrderGoodsName(goods.getGoodsName());
                order.setUserId(userId);
                orderService.addOrderNoDelay(order);
                goodsService.updateCountNoDelay(goodsId, count, no);
            }).start();
            countDownLatch.countDown();
        }
        return "悲伤.";
    }
}
