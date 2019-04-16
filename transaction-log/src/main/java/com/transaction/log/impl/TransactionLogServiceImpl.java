package com.transaction.log.impl;

import com.transaction.common.entity.TransactionLog;
import com.transaction.common.service.TransactionLogService;
import com.transaction.log.dao.TransactionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HuaWeiBo on 2019/4/6.
 */
@Service("transactionLogService")
public class TransactionLogServiceImpl implements TransactionLogService {

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Override
    public int addTransactionLog(TransactionLog transactionLog) {
        String centreNo = transactionLog.getCentreNo();
        TransactionLog log = transactionLogMapper.getTransactionLogByCentreNo(centreNo);
        if (log != null) {
            System.out.println("=============小老弟,多次消费了===========");
            return 0;
        }
        return transactionLogMapper.insertSelective(transactionLog);
    }

    @Override
    public void updateFailedCount(String centreNo) {
        System.out.println("修改失败数量：");
        // 修改分布式状态
        TransactionLog centre = transactionLogMapper.getTransactionLogByCentreNo(centreNo);
        if (centre == null) {
            return;
        }
        centre.setPrepareCount(0);
        centre.setFailedCount(1);
        transactionLogMapper.updateByPrimaryKeySelective(centre);
    }

    @Override
    public void updatePrepareCount(String centreNo) {
       synchronized (centreNo) {
           System.out.println("修改准备状态");
           transactionLogMapper.updatePrepareCount(centreNo);
       }
    }

    @Override
    public int returnFailedCount(String centreNo){
        System.out.println("====================查询失败个数");
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TransactionLog transaction = transactionLogMapper.getTransactionLogByCentreNo(centreNo);
            System.out.println("进行查询:"+transaction);
            Integer prepareCount = transaction.getPrepareCount();
            Integer failedCount = transaction.getFailedCount();
            // 回滚
            if (failedCount > 0) {
                System.out.println("失败数字1：" + failedCount);
                return 1;
            }
            if (prepareCount <= 0){
                System.out.println("都已完成操作2：" + prepareCount);
                if (failedCount != 0) {
                    System.out.println("失败数字不是0：" + failedCount);
                    return 1;
                }else {
                    System.out.println("失败数字是0：" + failedCount);
                    return 0;
                }
            }
        }
    }

    /**
     * 预完成成功,查询失败次数
     * 操作简介：
     * 1.失败次数>0返回.
     * 2.预操作都以完成：失败次数>0返回 / 返回成功.
     * @param centreNo
     * @return
     * @throws RuntimeException
     */
    @Override
    public int returnFailedCountException(String centreNo) throws RuntimeException {
        System.out.println("====================查询失败个数--抛异常=========");
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TransactionLog transaction = transactionLogMapper.getTransactionLogByCentreNo(centreNo);
            System.out.println(transaction);
            Integer prepareCount = transaction.getPrepareCount();
            Integer failedCount = transaction.getFailedCount();
            // 回滚
            if (failedCount > 0) {
                throw new RuntimeException();
            }
            if (prepareCount <= 0){
                if (failedCount != 0) {
                    throw new RuntimeException();
                }else {
                    return 0;
                }
            }
        }
    }

    @Override
    public int returnFailedCountNoDelay(String centreNo) {
        System.out.println("==========NoDelay==========查询失败个数");
        TransactionLog transaction = new TransactionLog();
        while (true) {
            transaction = transactionLogMapper.getTransactionLogByCentreNo(centreNo);
            System.out.println("NoDelay进行查询:"+transaction);
            Integer prepareCount = transaction.getPrepareCount();
            Integer failedCount = transaction.getFailedCount();
            // 回滚
            if (failedCount > 0) {
                return 1;
            }
            if (prepareCount <= 0){
                if (failedCount != 0) {
                    return 1;
                }else {
                    return 0;
                }
            }
        }
    }

    @Override
    public int returnFailedCountExceptionNoDelay(String centreNo) throws RuntimeException {
        System.out.println("=========NoDelay===========查询失败个数--抛异常=========");
        TransactionLog transaction = new TransactionLog();
        while (true) {
            transaction = transactionLogMapper.getTransactionLogByCentreNo(centreNo);
            System.out.println(transaction);
            Integer prepareCount = transaction.getPrepareCount();
            Integer failedCount = transaction.getFailedCount();
            // 回滚
            if (failedCount > 0) {
                throw new RuntimeException();
            }
            if (prepareCount <= 0){
                if (failedCount != 0) {
                    throw new RuntimeException();
                }else {
                    return 0;
                }
            }
        }
    }
}
