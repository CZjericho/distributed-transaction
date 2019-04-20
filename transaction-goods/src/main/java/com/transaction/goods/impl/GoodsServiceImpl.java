package com.transaction.goods.impl;

import com.transaction.common.entity.Goods;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.goods.dao.GoodsMapper;
import com.transaction.common.redis.RedisApi;
import com.transaction.common.redis.RedisConfig;
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

    @Autowired
    private RedisApi redisApi;

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
                result = goodsMapper.reduceCount(id, count);
                transactionLogService.updatePrepareCount(centreNo);
                transactionLogService.returnFailedCountExceptionNoDelay(centreNo);
            } catch (RuntimeException e) {
                System.out.println("---NoDelay-捕获异常 -----回滚");
                transactionLogService.updateFailedCount(centreNo);
                throw new RuntimeException();
            }
            return result;
        }
        System.out.println("---NoDelay-库存不足异常----end---↑-------------");
        transactionLogService.updateFailedCount(centreNo);
        throw new RuntimeException();
    }

    /**
     * 保证库存为正( >0 )
     * 1.更新库存: 成功/失败
     * 1.1 成功 (继续)
     * 1.2 失败 (修改事务日志失败次数,抛异常)
     * 2.try-catch(任意失败认定失败,1.2操作)
     * 2.1事务日志准备次数-1
     * 2.2获取失败次数(失败:抛异常, 成功:0)
     * @param id
     * @param count
     * @param centreNo
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int updateCountSafe(int id, int count, String centreNo) {
        System.out.println(centreNo + "--Safe--goods-begin:");
        int result = goodsMapper.reduceCount(id, count);
        if (result == 0) {
            System.out.println(centreNo + "--Safe--库存不足;");
            transactionLogService.updateFailedCount(centreNo);
            redisApi.increase(RedisConfig.GOODS_COUNT + id, count);
            throw new RuntimeException();
        }
        try {
            transactionLogService.updatePrepareCount(centreNo);
            transactionLogService.returnFailedCountExceptionNoDelay(centreNo);
        } catch (RuntimeException e) {
            System.out.println(centreNo + "--Safe--操作失败;");
            transactionLogService.updateFailedCount(centreNo);
            redisApi.increase(RedisConfig.GOODS_COUNT + id, count);
            throw new RuntimeException();
        }
        System.out.println(centreNo + "--Safe--操作成功end.");
        return result;
    }

    @Override
    public Goods getGoods(int id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

}
