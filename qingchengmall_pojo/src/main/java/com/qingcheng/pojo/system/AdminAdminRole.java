package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.List;

public class AdminAdminRole implements Serializable {

    private Admin admin;
    private List<AdminRole> adminRoles;

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<AdminRole> getAdminRoles() {
        return adminRoles;
    }

    public void setAdminRoles(List<AdminRole> adminRoles) {
        this.adminRoles = adminRoles;
    }
}
