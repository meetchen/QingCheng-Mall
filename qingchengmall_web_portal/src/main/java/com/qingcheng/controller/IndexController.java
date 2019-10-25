package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller//不用@restContoller是因为这里不需要返回json格式的数据
public class IndexController {

    @Reference
    private AdService adService;
    @Reference
    private CategoryService categoryService;
    @GetMapping("/index")
    public String index(Model model){
        //这里得到首页广告轮播的图的列表
        List<Ad> lb = adService.findByPosition("web_index_lb");
        model.addAttribute("lbt",lb);
        //得到商品的分类导航
        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryList",categoryTree);
        return "index";
    }
}
