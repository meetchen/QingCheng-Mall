package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.util.IdWorker;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    @Autowired
    private AuditMapper auditMapper;
    @Autowired
    private LogMapper logMapper;


    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     *
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 保存商品
     *
     * @param goods
     */
    @Transactional
    public void saveGoods(Goods goods) {
        boolean isAdd = true;
        Spu spu = goods.getSpu();
        if (spu.getId() == null) {
            spu.setId(idWorker.nextId() + "");
            spuMapper.insert(spu);
        } else {
            isAdd = false;
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId", spu.getId());
            skuMapper.deleteByExample(example);
            spuMapper.updateByPrimaryKeySelective(spu);
        }
        spu.setId(idWorker.nextId() + "");
        spuMapper.insert(spu);
        List<Sku> skuList = goods.getSkuList();
        Date date = new Date();
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        for (Sku sku : skuList) {
            if (isAdd) {
                sku.setId(idWorker.nextId() + "");
                sku.setCreateTime(date);
            }
            sku.setSpuId(spu.getId());
            String name = spu.getName();
            if (sku.getSpec() == null || "".equals(sku.getSpec())) {
                sku.setSpec("{}");
            }
            Map<String, String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            for (String value : specMap.values()) {
                name += "" + value;
            }
            sku.setUpdateTime(date);
            sku.setCategoryId(spu.getCategory3Id());
            sku.setCategoryName(category.getName());
            sku.setName(name);
            sku.setCommentNum(0);
            sku.setSaleNum(0);
            skuMapper.insert(sku);
        }
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(spu.getCategory3Id());
        categoryBrand.setBrandId(spu.getBrandId());
        if (categoryBrandMapper.selectCount(categoryBrand) == 0) {
            categoryBrandMapper.insert(categoryBrand);
        }
    }

    public Goods findGoodsById(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        Goods goods = new Goods();
        goods.setSkuList(skuList);
        goods.setSpu(spu);
        return goods;
    }

    /**
     * 商品审核
     *
     * @param id 商品ID
     * @param status 商品是否审核通过 1过 0 否
     * @param message 执行消息
     */
    @Transactional
    public void audit(String id, String status, String message) {
        //修改审核状态
        Spu spu = new Spu();
        spu.setId(id);
        spu.setStatus(status);
        if (status.equals("1")) {
            spu.setIsMarketable("1");
        }
        spuMapper.updateByPrimaryKeySelective(spu);
        // 记录商品审核记录

            // 商品审核并且通过 则不执行下面代码
        Example example =new Example(Audit.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        criteria.andEqualTo("result","1");
        if (auditMapper.selectCountByExample(example)!=1){
            Audit audit = new Audit();
            audit.setAuditDate(new Date());
            audit.setMessage(message);
            IdWorker idWorker = new IdWorker();
            audit.setId(idWorker.nextId() + "");
            audit.setAuditor("admin"); //默认admin修改
            audit.setSpuId(id);
            audit.setResult(status);
            auditMapper.insert(audit);
        }
        /**
         * 记录日志信息，
         * 按照修改时间按降序排列，取第一个
         * 如果该货物已存在日志信息，
         *  则设置isNew字段为0 即为 原信息
         *  并插入新的信息isNew为1 更新时间
         *  如果不存在，则新建 更新时间
         */
        Log log = new Log();
        Example exampleLog = new Example(Log.class);
        Example.Criteria exampleLogCriteria = exampleLog.createCriteria();
        criteria.andEqualTo("spuId",id);
        exampleLog.setOrderByClause("`updateTime` DESC");
        List<Log> logs = logMapper.selectByExample(exampleLog);
        if (logs!=null){
            Log log1 = logs.get(0);
            log1.setIsNew("0");
            logMapper.insert(log1);
        }
        log.setId(idWorker.nextId()+"");
        log.setIsNew("1");
        log.setUpdateTime(new Date());
        Sku sku = new Sku();
        sku.setSpuId(id);
        log.setSalesPrice(skuMapper.selectOne(sku).getPrice());
        logMapper.insert(log);

    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andLike("sn", "%" + searchMap.get("sn") + "%");
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andLike("isMarketable", "%" + searchMap.get("isMarketable") + "%");
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andLike("isEnableSpec", "%" + searchMap.get("isEnableSpec") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
