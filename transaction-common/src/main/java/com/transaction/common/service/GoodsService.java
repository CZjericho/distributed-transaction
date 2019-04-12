package com.transaction.common.service;

import com.transaction.common.entity.Goods;

/**
 * Created by HuaWeiBo on 2019/4/4.
 */
public interface GoodsService {

    int updateCount(int id, int count, String centreNo);

    /**
     * 取消延时
     * @param id
     * @param count
     * @param centreNo
     * @return
     */
    int updateCountNoDelay(int id, int count, String centreNo);

    Goods getGoods(int id);
}
