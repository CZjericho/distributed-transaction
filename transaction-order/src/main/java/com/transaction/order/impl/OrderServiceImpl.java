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
        System.out.println("-------NoDelay------order--------------");
        String centreNo = order.getOrderNo();
        int result;
        try {
            result = orderMapper.insertSelective(order);
            transactionLogService.updatePrepareCount(centreNo);
        } catch (Exception e) {
            System.out.println("------NoDelay-------添加订单失败---↑--------------");
            transactionLogService.updateFailedCount(centreNo);
            throw new RuntimeException();
        }
        int failedCount = transactionLogService.returnFailedCountNoDelay(centreNo);
        System.out.println("NoDelay订单显示失败count:" + failedCount);
        if (failedCount == 1) {
            System.out.println("--NoDelay--抛出异常 -----回滚");
            throw new RuntimeException();
        }
        return result;
    }
}
