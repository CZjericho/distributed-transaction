package com.transaction.common.service;

import com.transaction.common.entity.TransactionLog;

/**
 * Created by HuaWeiBo on 2019/4/6.
 */
public interface TransactionLogService {
    /**
     * 添加日志
     * @param transactionLog
     * @return
     */
    int addTransactionLog(TransactionLog transactionLog);

    /**
     * 更新失败操作
     * @param centreNo
     */
    void updateFailedCount(String centreNo);

    /**
     * 更新预操作次数
     * @param centreNo
     */
    void updatePrepareCount(String centreNo);

    /**
     * 1.获取失败次数(0:成功,1:失败)
     */
    int returnFailedCount(String centreNo);

    /**
     * 2.获取失败次数(0:成功,异常:失败)
     */
    int returnFailedCountException(String centreNo) throws RuntimeException;

    /**
     * 取消延时
     * @param centreNo
     * @return
     */
    int returnFailedCountNoDelay(String centreNo);

    /**
     * 取消延时
     * @param centreNo
     * @return
     */
    int returnFailedCountExceptionNoDelay(String centreNo) throws RuntimeException;
}
