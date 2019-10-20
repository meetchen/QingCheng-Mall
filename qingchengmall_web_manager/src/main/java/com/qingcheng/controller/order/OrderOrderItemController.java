package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.OrderOrderItem;
import com.qingcheng.service.order.OrderOrderItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ooItem")
public class OrderOrderItemController {



    @Reference
    private OrderOrderItemService orderOrderItemService;


    @GetMapping("/findOrderOrderItem")
    public OrderOrderItem findOrderOrderItem(String id){
        return orderOrderItemService.findOrderOrderItem(id);
    }
}
