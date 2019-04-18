package com.transaction.order.impl;

import com.transaction.common.entity.Order;
import com.transaction.common.service.OrderService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.order.dao.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by HuaWeiBo on 2019/4/10.
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private TransactionLogService transactionLogService;

    /**
     * <p>直接抛出异常: 失败次数异常（大于0）</p>
     * <p>捕获抛出异常: 添加订单失败</p>
     * @param order
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int addOrder(Order order) {
        System.out.println("-------------order--------------");
        String centreNo = order.getOrderNo();
        transactionLogService.updatePrepareCount(centreNo);
        int result;
        try {
            result = orderMapper.insertSelective(order);
        } catch (Exception e) {
            System.out.println("-------------添加订单失败---↑--------------");
            transactionLogService.updateFailedCount(centreNo);
            throw new RuntimeException();
        }
        int failedCount = transactionLogService.returnFailedCount(centreNo);
        System.out.println("订单显示失败count:" + failedCount);
        if (failedCount == 1) {
            System.out.println("----抛出异常 -----回滚");
            throw new RuntimeException();
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int addOrderNoDelay(Order order) {
        String centreNo = order.getOrderNo();
        System.out.println(centreNo + "--NoDelay--order-begin:");
        int result;
        try {
            result = orderMapper.insertSelective(order);
            // 日志表：减少准备操作次数:(已有一个操作预完成)
            transactionLogService.updatePrepareCount(centreNo);
            transactionLogService.returnFailedCountExceptionNoDelay(centreNo);
        } catch (Exception e) {
            System.out.println(centreNo + "--NoDelay--添加失败;");
            // 日志表：修改失败次数(有失败次数，就可以认为所有操作预完成，并回滚)
            transactionLogService.updateFailedCount(centreNo);
            throw new RuntimeException();
        }
        System.out.println(centreNo + "--NoDelay--添加成功end.");
        return result;
    }

}
