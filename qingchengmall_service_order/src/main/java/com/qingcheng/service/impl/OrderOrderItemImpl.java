package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.OrderOrderItem;
import com.qingcheng.service.order.OrderOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class OrderOrderItemImpl implements OrderOrderItemService {


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderOrderItem findOrderOrderItem(String id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        OrderOrderItem orderOrderItem = new OrderOrderItem();
        orderOrderItem.setOrder(order);
//        OrderItem orderItem = new OrderItem();
//        orderItem.setOrderId(id);
//        List<OrderItem> select = orderItemMapper.select(orderItem);
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        orderOrderItem.setOrderItemList(orderItems);
        System.out.println(orderOrderItem);
        return orderOrderItem;
    }
}
