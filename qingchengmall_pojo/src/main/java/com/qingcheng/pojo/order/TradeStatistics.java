package com.qingcheng.pojo.order;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Table(name = "tb_trade_statistics")
public class TradeStatistics implements Serializable {

    private Integer peopleNum;
    private Integer orderNum;
    private Integer goodsNum;
    private Integer effectiveOrderNum;
    private Integer orderMoney;
    private Integer returnMoney;
    private Integer payOrderNum;
    private Integer payMoney;
    private Integer getPeopleNum;
    @Id
    private LocalDate date;


    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getEffectiveOrderNum() {
        return effectiveOrderNum;
    }

    public void setEffectiveOrderNum(Integer effectiveOrderNum) {
        this.effectiveOrderNum = effectiveOrderNum;
    }

    public Integer getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Integer orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(Integer returnMoney) {
        this.returnMoney = returnMoney;
    }

    public Integer getPayOrderNum() {
        return payOrderNum;
    }

    public void setPayOrderNum(Integer payOrderNum) {
        this.payOrderNum = payOrderNum;
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getGetPeopleNum() {
        return getPeopleNum;
    }

    public void setGetPeopleNum(Integer getPeopleNum) {
        this.getPeopleNum = getPeopleNum;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


}
