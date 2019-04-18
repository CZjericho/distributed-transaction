package com.transaction.web.entity;

/**
 * Created by HuaWeiBo on 2019/4/17.
 */
public class CreateOrderRequest {
    private Integer userId;
    private Integer goodsId;
    private Integer count;
    private Integer number;

    public CreateOrderRequest(Integer userId, Integer goodsId, Integer count, Integer number) {
        this.userId = userId;
        this.goodsId = goodsId;
        this.count = count;
        this.number = number;
    }

    public CreateOrderRequest(Integer userId, Integer goodsId, Integer count) {
        this.userId = userId;
        this.goodsId = goodsId;
        this.count = count;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
