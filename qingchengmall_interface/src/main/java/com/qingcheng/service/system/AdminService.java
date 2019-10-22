package com.qingcheng.service.system;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminAdminRole;

import java.util.*;

/**
 * admin业务逻辑层
 */
public interface AdminService {


     List<Admin> findAll();


     PageResult<Admin> findPage(int page, int size);


     List<Admin> findList(Map<String,Object> searchMap);


     PageResult<Admin> findPage(Map<String,Object> searchMap,int page, int size);


     AdminAdminRole findById(Integer id);

     void add(AdminAdminRole adminAdminRole);


     void update(AdminAdminRole adminAdminRole);


     void delete(Integer id);

    void updatePassword(String loginName,String oldPassword,String newPassword);

}
