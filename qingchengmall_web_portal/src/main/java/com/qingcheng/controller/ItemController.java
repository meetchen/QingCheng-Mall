package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {
    @Reference
    private SpuService spuService;
    @Value("$(pagePath)")
    private String pagePath;
    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/createPage")
    public void createPage(String spuId){
        //查询商品信息
        Goods goodsById = spuService.findGoodsById(spuId);
        //获取spu信息
        Spu spu = goodsById.getSpu();
        //获取sku列表
        List<Sku> skuList = goodsById.getSkuList();
        //批量生成页面
        for (Sku sku : skuList) {
            // 1.创建上下文和数据模型
            Context context = new Context();
            Map dateModel = new HashMap<>();
            dateModel.put("spu",spu);
            dateModel.put("sku",sku);
            context.setVariables(dateModel);
            // 2.准备文件
            File dir = new File(pagePath);
            if (!dir.exists()){
                // 递归创建
                dir.mkdirs();
            }
            File dest = new File(dir,sku.getId()+".html");
            // 3.生成页面
            try {
                PrintWriter writer = new PrintWriter(dest,"UTF-8");
                templateEngine.process("item",context,writer);
                System.out.println("生成页面"+sku.getId()+".html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
