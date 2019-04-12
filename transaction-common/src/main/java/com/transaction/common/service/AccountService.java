package com.transaction.common.service;

import com.transaction.common.entity.Account;

/**
 * Created by HuaWeiBo on 2019/4/3.
 */
public interface AccountService {

    int updateAccount(int id, double money, String centreNo);

    /**
     * 取消延时
     * @param id
     * @param money
     * @param centreNo
     * @return
     */
    int updateAccountNoDelay(int id, double money, String centreNo);

    Account getAccount(int id);

}
