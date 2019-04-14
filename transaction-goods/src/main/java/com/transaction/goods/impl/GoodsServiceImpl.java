package com.transaction.goods.impl;

import com.transaction.common.entity.Goods;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.goods.dao.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by HuaWeiBo on 2019/4/4.
 */
@Service("goodsService")
public class GoodsServiceImpl implements GoodsService{
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private TransactionLogService transactionLogService;

    /**
     * 更新库存
     * <p>直接抛出异常: 库存不足</p>
     * <p>捕获抛出异常: 修改库存失败,失败次数异常</p>
     * @param id      商品id
     * @param count   购买数量
     * @param centreNo no标识
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int updateCount(int id, int count, String centreNo) {
        System.out.println("-------------goods--------------");
        transactionLogService.updatePrepareCount(centreNo);
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        int flag = goods.getGoodsCount() - count;
        if (flag >= 0) {
            goods.setGoodsCount(flag);
            int result;
            // 直接捕获异常
            try {
                result = goodsMapper.updateByPrimaryKeySelective(goods);
                transactionLogService.returnFailedCountException(centreNo);
            } catch (RuntimeException e) {
                System.out.println("----捕获异常 -----回滚");
                throw new RuntimeException();
            }
            return result;
        }
        System.out.println("----库存不足异常----end---↑-------------");
        transactionLogService.updateFailedCount(centreNo);
        throw new RuntimeException();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int updateCountNoDelay(int id, int count, String centreNo) {
        System.out.println("--------NoDelay-----goods--------------");
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        int flag = goods.getGoodsCount() - count;
        if (flag >= 0) {
            goods.setGoodsCount(flag);
            int result;
            // 直接捕获异常
            try {
                result = goodsMapper.updateByPrimaryKeySelective(goods);
                transactionLogService.updatePrepareCount(centreNo);
                transactionLogService.returnFailedCountExceptionNoDelay(centreNo);
            } catch (RuntimeException e) {
                System.out.println("---NoDelay-捕获异常 -----回滚");
                throw new RuntimeException();
            }
            return result;
        }
        System.out.println("---NoDelay-库存不足异常----end---↑-------------");
        transactionLogService.updateFailedCount(centreNo);
        throw new RuntimeException();
    }

    @Override
    public Goods getGoods(int id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

}
