package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.List;

public class ResourceRoleResource implements Serializable {

    private List<Resource> resources;
    private String userId;

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
