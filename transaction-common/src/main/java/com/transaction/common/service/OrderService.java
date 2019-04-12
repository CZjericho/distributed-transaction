package com.transaction.common.service;

import com.transaction.common.entity.Order;

/**
 * Created by HuaWeiBo on 2019/4/10.
 */
public interface OrderService {

    int addOrder(Order order);

    /**
     * 取消延时
     * @param order
     * @return
     */
    int addOrderNoDelay(Order order);
}
