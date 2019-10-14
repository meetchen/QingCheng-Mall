package com.qingcheng.controller;

import com.qingcheng.pojo.goods.Brand;
import com.qingcheng.service.goods.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<Brand>  findAll(){
        System.out.println("hello world");
        return brandService.findAll();
    }
}
