package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdminMapper;
import com.qingcheng.dao.AdminRoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminAdminRole;
import com.qingcheng.pojo.system.AdminRole;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.util.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service(interfaceClass = AdminService.class)
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Reference
    private BCrypt bCrypt;
    @Autowired
    private AdminRoleMapper adminRoleMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Admin> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectAll();
        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Admin> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Admin> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectByExample(example);
        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public AdminAdminRole findById(Integer id) {
        Admin admin = adminMapper.selectOneByExample(id);
        Example ex = new Example(AdminAdminRole.class);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo("adminId",id);
        List<AdminRole> adminRoles = adminRoleMapper.selectByExample(ex);
        AdminAdminRole adminAdminRole = new AdminAdminRole();
        admin.setPassword(null);
        adminAdminRole.setAdmin(admin);
        adminAdminRole.setAdminRoles(adminRoles);
        return adminAdminRole;
    }

    @Override
    @Transactional
    public void add(AdminAdminRole adminAdminRole) {
        Admin admin = adminAdminRole.getAdmin();
        String password = admin.getPassword();
        String gensalt = bCrypt.gensalt();
        String newPwd = bCrypt.hashpw(password, gensalt);
        admin.setPassword(newPwd);
        int insert = adminMapper.insert(admin);
        List<AdminRole> adminRoles = adminAdminRole.getAdminRoles();
        for (AdminRole adminRole : adminRoles) {
            adminRole.setAdminId(insert);
            adminRoleMapper.insert(adminRole);
        }
    }


    /**
     * 先删除用户的角色，再加入用户的角色
     * 如果密码为空，则不作改变
     * 不为空，加密存储
     * @param adminAdminRole 用户 用户角色对象
     */
    @Transactional
    public void update(AdminAdminRole adminAdminRole) {
        Admin admin = adminAdminRole.getAdmin();
        Integer id = admin.getId();
        Example ex = new Example(AdminRole.class);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo("adminId",id);
        adminRoleMapper.deleteByExample(ex);
        String password = admin.getPassword();
        if (!(password==null||password.equals(""))){
            String gensalt = bCrypt.gensalt();
            String hashpw = bCrypt.hashpw(password, gensalt);
            admin.setPassword(hashpw);
        }
        adminMapper.updateByPrimaryKeySelective(admin);
        List<AdminRole> adminRoles = adminAdminRole.getAdminRoles();
        for (AdminRole adminRole : adminRoles) {
            adminRole.setAdminId(id);
            adminRoleMapper.insert(adminRole);
        }
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        adminMapper.deleteByPrimaryKey(id);
    }

    public void updatePassword(String loginName,String oldPassword, String newPassword) {
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("loginName",loginName);
        Admin admin = adminMapper.selectOneByExample(example);
        String password = admin.getPassword();
        boolean checkpw = bCrypt.checkpw(oldPassword, password);
        if (checkpw){
            String gensalt = bCrypt.gensalt();
            password=bCrypt.hashpw(newPassword,gensalt);
            admin.setPassword(password);
            adminMapper.updateByPrimaryKeySelective(admin);
        }else {
            throw new RuntimeException("原密码错误");
        }
    }

    @Override
    public List<String> findResKeyByLoginName(String username) {
        return adminMapper.findResKeyByLoginName(username);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andEqualTo("loginName",searchMap.get("loginName"));
            }
            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
            }

            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}
