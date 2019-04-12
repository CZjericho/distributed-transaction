package com.transaction.common.entity;

import java.io.Serializable;

public class TransactionLog implements Serializable{
    private Integer id;

    private String centreNo;

    private Integer count;

    private Integer prepareCount;

    private Integer failedCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCentreNo() {
        return centreNo;
    }

    public void setCentreNo(String centreNo) {
        this.centreNo = centreNo == null ? null : centreNo.trim();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPrepareCount() {
        return prepareCount;
    }

    public void setPrepareCount(Integer prepareCount) {
        this.prepareCount = prepareCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    @Override
    public String toString() {
        return "TransactionLog{" +
                "id=" + id +
                ", centreNo='" + centreNo + '\'' +
                ", count=" + count +
                ", prepareCount=" + prepareCount +
                ", failedCount=" + failedCount +
                '}';
    }
}