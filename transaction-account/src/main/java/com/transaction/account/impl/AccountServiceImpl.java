package com.transaction.account.impl;


import com.transaction.common.entity.Account;
import com.transaction.common.entity.TransactionLog;
import com.transaction.common.service.AccountService;
import com.transaction.common.service.GoodsService;
import com.transaction.common.service.TransactionLogService;
import com.transaction.account.dao.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xhga
 * @since 2019-04-03
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private TransactionLogService transactionLogService;

    /**
     * 更新账户金额
     * <p>直接抛出异常: 账户余额不足,失败次数异常（大于0）</p>
     * <p>捕获抛出异常: 修改账户金额失败</p>
     * @param id       会员id
     * @param money    消费金额
     * @param centreNo no标识
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int updateAccount(int id, double money, String centreNo) {
        System.out.println("-------------account--------------");
        transactionLogService.updatePrepareCount(centreNo);
        Account account = accountMapper.selectByPrimaryKey(id);
        Double userMoney = account.getUserMoney();
        if (userMoney >= money) {
            account.setUserMoney(userMoney - money);
            int result;
            try {
                result = accountMapper.updateByPrimaryKeySelective(account);
            } catch (Exception e) {
                System.out.println("-------------账户修改失败---↑--------------");
                transactionLogService.updateFailedCount(centreNo);
                throw new RuntimeException();
            }
            int failedCount = transactionLogService.returnFailedCount(centreNo);
            System.out.println("账户显示失败count:" + failedCount);
            if (failedCount == 1) {
                System.out.println("----抛出异常 -----回滚");
                throw new RuntimeException();
            }
            return result;
        }
        System.out.println("-------------账户余额不足--------------");
        transactionLogService.updateFailedCount(centreNo);
        throw new RuntimeException();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int updateAccountNoDelay(int id, double money, String centreNo) {
        System.out.println("------NoDelay-------account--------------");
        Account account = accountMapper.selectByUserId(id);
        Double userMoney = account.getUserMoney();
        if (userMoney >= money) {
            account.setUserMoney(userMoney - money);
            int result;
            try {
                result = accountMapper.reduceMoney(id, money);
                transactionLogService.updatePrepareCount(centreNo);
                transactionLogService.returnFailedCountExceptionNoDelay(centreNo);
            } catch (Exception e) {
                System.out.println("------NoDelay-------账户修改失败---↑--------------");
                transactionLogService.updateFailedCount(centreNo);
                throw new RuntimeException();
            }
            return result;
        }
        System.out.println("-------------NoDelay账户余额不足--------------");
        transactionLogService.updateFailedCount(centreNo);
        throw new RuntimeException();
    }

    /**
     * 保证余额为正( >0 )
     * 1.更新账户: 成功/失败
     * 1.1 成功 (继续)
     * 1.2 失败 (修改事务日志失败次数,抛异常)
     * 2.try-catch(任意失败认定失败,1.2操作)
     * 2.1事务日志准备次数-1
     * 2.2获取失败次数(失败:抛异常, 成功:0)
     * @param id
     * @param money
     * @param centreNo
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int updateAccountSafe(int id, double money, String centreNo) {
        System.out.println(centreNo + "--Safe-account-begin:");
        int result = accountMapper.reduceMoney(id, money);
        if (result == 0) {
            System.out.println(centreNo + "--Safe-余额不足;");
            transactionLogService.updateFailedCount(centreNo);
            throw new RuntimeException();
        }
        try {
            transactionLogService.updatePrepareCount(centreNo);
            transactionLogService.returnFailedCountExceptionNoDelay(centreNo);
        } catch (Exception e) {
            System.out.println(centreNo + "--Safe-操作失败;");
            transactionLogService.updateFailedCount(centreNo);
            throw new RuntimeException();
        }
        System.out.println(centreNo + "--Safe--操作成功end.");
        return result;
    }

    @Override
    public Account getAccount(int id) {
        return accountMapper.selectByPrimaryKey(id);
    }

}
