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
            } catch (Exception e) {
                System.out.println("------NoDelay-------账户修改失败---↑--------------");
                transactionLogService.updateFailedCount(centreNo);
                throw new RuntimeException();
            }
            int failedCount = transactionLogService.returnFailedCountNoDelay(centreNo);
            System.out.println("NoDelay账户显示失败count:" + failedCount);
            if (failedCount == 1) {
                System.out.println("----NoDelay抛出异常 -----回滚");
                throw new RuntimeException();
            }
            return result;
        }
        System.out.println("-------------NoDelay账户余额不足--------------");
        transactionLogService.updateFailedCount(centreNo);
        throw new RuntimeException();
    }

    @Override
    public Account getAccount(int id) {
        return accountMapper.selectByPrimaryKey(id);
    }

}
