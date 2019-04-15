# 实现分布式事务
框架:SpringBoot+mybatis+dubbo+zk+mysql
  ```
     表结构在项目中db目录
      
     transaction-log,account,order,goods 
  ```
  
```
   操作简介:
   
   采用log日志(transaction-log)来记录业务系统(account,order,goods)操作成功或者失败;
   业务系统完成操作之后检查日志系统失败个数,存在失败进行回滚;
```


### 业务场景：扣款(account)->创建订单(order)->减少库存(goods)
* 1.日志表(centre_no,count,prepare_count,failed_count)

     操作简介：
     
     生成centreNo,记录事务整天操作关联的日志表
     
     设置count(此事务关联几个中心操作,此业务场景3),
     
     设置prepare_count(未处理的业务数,初始时等于count),
     
     默认0：failed_count(失败次数)
     ```
                        TransactionLog transactionLog = new TransactionLog();
                        transactionLog.setCentreNo(no);
                        transactionLog.setCount(3);
                        transactionLog.setPrepareCount(3);
                        transactionLogService.addTransactionLog(transactionLog);
     ```
* 2 .业务系统操作(账户,订单,库存)
     操作简介：dubbo+zk实现rpc远程服务调用,分四部分：
     ```
        注:--修改失败次数:(prepare_count=0,failed_count=1)
           --减少准备操作次数:(prepare_count-1)
           --也正是因为2.3需要结合其他业务减少准备操作次数,所以业务操作方法(或服务)需要使用异步
             ( <dubbo:method name="addOrder" async="true" />)
        2.1.校验回滚(校验数据,如库存,账户金额)
          不满足:修改失败次数,并抛异常回滚
          
        2.2.业务系统操作处理(如扣款,创建订单,扣库存,捕获异常)
          2.2.1失败(捕获异常): 修改失败次数,并抛异常回滚
          2.2.2成功:修改失败次数
        2.3.查询失败次数(当1,2任意步骤失败,都可以修改失败次数为1)
          实现了两种返回:(成功:0,失败:1/抛异常)
          实现: while循环,跳出循环条件:
          2.3.1 失败:failed_count>0 (return:1/抛异常)
          2.3.2 成功:prepare_count<=0(业务系统操作2步骤全部完成) 并且 failed_count==0
        2.4.处理查询失败次数返回值
          成功:你懂得
          失败：1->判断返回值如果等于1,抛异常
               捕获异常->catch块中,抛异常(总而言之就是抛异常)
          ps:忘说了异常为:RuntimeException
     ```
### 实现代码简介
1.web：

     ```
     TransactionLog transactionLog = new TransactionLog();
     transactionLog.setCentreNo(no);
     transactionLog.setCount(3);
     transactionLog.setPrepareCount(3);
     // 第一步：生成事务日志
     transactionLogService.addTransactionLog(transactionLog);
     double money = goods.getGoodsMoney() * count;
     // 第二步(账户)：业务系统操作->扣钱
     accountService.updateAccountNoDelay(userId, money, no);
     Order order = new Order();
     order.setOrderNo(no);
     order.setOrderMoney(money);
     order.setOrderDate(new Date());
     order.setOrderGoodsName(goods.getGoodsName());
     order.setUserId(userId);
     // 第二步(订单)：业务系统操作->生成订单
     orderService.addOrderNoDelay(order);
     // 第二步(库存)：业务系统操作->减库存
     goodsService.updateCountNoDelay(goodId, count, no);

     ```
 2.业务系统操作：(订单)
 
     ```
         @Override
         @Transactional(rollbackFor = RuntimeException.class)
         public int addOrderNoDelay(Order order) {
             System.out.println("-------NoDelay------order--------------");
             String centreNo = order.getOrderNo();
             int result;
             try {
                 // 1.生成订单
                 result = orderMapper.insertSelective(order);
                 // 2.日志表：减少准备操作次数:(已有一个操作预完成)
                 transactionLogService.updatePrepareCount(centreNo);
             } catch (Exception e) {
                 System.out.println("------NoDelay-------添加订单失败---↑--------------");
                 // 3.日志表：修改失败次数(有失败次数，就可以认为所有操作预完成，并回滚)
                 transactionLogService.updateFailedCount(centreNo);
                 throw new RuntimeException();
             }
             //  4.预完成成功,查询失败次数,存在失败进行回滚
             int failedCount = transactionLogService.returnFailedCountNoDelay(centreNo);
             System.out.println("NoDelay订单显示失败count:" + failedCount);
             if (failedCount == 1) {
                 System.out.println("--NoDelay--抛出异常 -----回滚");
                 throw new RuntimeException();
             }
             return result;
         }
     ```
             
3.预完成成功,查询失败次数
操作简介：1.失败次数>0返回，2.预操作都以完成：失败次数>0返回 / 返回成功

     ```
             
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
         
     ```       
* [csdn](https://blog.csdn.net/qq_37751454/article/details/89265134)

