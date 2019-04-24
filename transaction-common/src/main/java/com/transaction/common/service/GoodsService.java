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


    /**
     * 保证库存为正( >0 )
     *
     * @param id
     * @param count
     * @param centreNo
     * @return
     */
    int updateCountSafe(int id, int count, String centreNo);


    /**
     * 编程式事务
     * @param id
     * @param count
     * @param centreNo
     * @return
     */
    int updateCountSafeProgramme(int id, int count, String centreNo);

    Goods getGoods(int id);
}
