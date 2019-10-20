package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

public class OrderOrderItem implements Serializable {

    private Order order;
    private List<OrderItem> orderItemList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public String toString() {
        return "OrderOrderItem{" +
                "order=" + order +
                ", orderItemList=" + orderItemList +
                '}';
    }
}
