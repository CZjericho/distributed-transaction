package com.transaction.log.dao;

import com.transaction.common.entity.TransactionLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TransactionLog record);

    int insertSelective(TransactionLog record);

    TransactionLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TransactionLog record);

    int updatePrepareCount(String centreNo);

    int updateByPrimaryKey(TransactionLog record);

    TransactionLog getTransactionLogByCentreNo(String centreNo);
}