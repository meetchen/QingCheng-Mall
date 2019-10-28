package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {
    @Reference
    private SpuService spuService;
    @Reference
    private CategoryService categoryService;
    @Value("${pagePath}")
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
        //查询商品分类
        List<String> categoryList =new ArrayList<>();
        categoryList.add(categoryService.findById(spu.getCategory1Id()).getName());
        categoryList.add(categoryService.findById(spu.getCategory2Id()).getName());
        categoryList.add(categoryService.findById(spu.getCategory3Id()).getName());
        //sku地址列表
        Map<String, String> urlMap = new HashMap<>();
        for (Sku sku : skuList) {
            String jsonString = JSON.toJSONString(JSON.parseObject(sku.getSpec()), SerializerFeature.MapSortField);
            urlMap.put(jsonString,sku.getId()+".html");
        }

        //批量生成页面
        for (Sku sku : skuList) {
            // 1.创建上下文和数据模型
            Context context = new Context();
            Map dateModel = new HashMap<>();
            dateModel.put("spu",spu);
            dateModel.put("sku",sku);
            dateModel.put("categoryList",categoryList);
            dateModel.put("skuImages",sku.getImage().split(","));
            dateModel.put("spuImages",spu.getImage().split(","));
            //参数列表
            Map paraItems = JSON.parseObject(spu.getParaItems());
            dateModel.put("paraItems",paraItems);
            //规格列表
            Map<String,String> specItems = (Map) JSON.parseObject(sku.getSpec());
            dateModel.put("specItems",specItems);
            Map<String,List> specMap = (Map)JSON.parseObject(sku.getSpec());
            for (String key : specMap.keySet()) {
                List<String> list = specMap.get(key);
                List<Map> mapList = new ArrayList<>();
                for (String value : list) {
                    Map map = new HashMap();
                    map.put("option",value);
                    if (specItems.get(key).equals(value)){
                        map.put("checked",true);
                    }else {
                        map.put("checked",false);
                    }
                    Map<String,String> spec = (Map)JSON.parseObject(sku.getSpec());
                    spec.put(key,value);
                    String jsonString = JSON.toJSONString(spec, SerializerFeature.MapSortField);
                    map.put("url",urlMap.get(jsonString));
                    mapList.add(map);
                }
                specMap.put(key,mapList);
            }
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
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
