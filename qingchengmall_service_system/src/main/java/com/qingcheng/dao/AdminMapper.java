package com.qingcheng.dao;

import com.qingcheng.pojo.system.Admin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AdminMapper extends Mapper<Admin> {

    @Select("SELECT res_key from tb_resource WHERE id in (SELECT resource_id from tb_role_resource WHERE role_id " +
            " in(SELECT role_id from tb_admin_role WHERE admin_id in" +
            "(SELECT id FROM tb_admin WHERE login_name=#{userName})))")
    List<String> findResKeyByLoginName(@Param("userName") String userName);

}
