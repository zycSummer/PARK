package com.jet.cloud.deepmind.config.security.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jet.cloud.deepmind.common.HttpConstants.*;

/**
 * 权限model
 *
 * @author yhy
 * @create 2019-10-15 13:41
 */
@Data
public class Permission implements Serializable {
    private static final long serialVersionUID = 4026626958729988430L;

    private String url;

    private String method;

    private String roleId;

    private Set<String> roleList;

    public void setRoleList(String roleCode) {
        if (this.roleList == null) this.roleList = new HashSet<>();
        this.roleList.add(roleCode);
    }

    public Permission(String url, String method, String roleId) {
        this.url = url;
        this.method = method;
        this.roleId = roleId;
    }

    public Permission(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public static Permission PERMISSION_GET(String url) {
        return new Permission(url, GET);
    }

    public static Permission PERMISSION_POST(String url) {
        return new Permission(url, POST);
    }

    public static Permission PERMISSION_DELETE(String url) {
        return new Permission(url, DELETE);
    }

    public static Permission PERMISSION_PUT(String url) {
        return new Permission(url, PUT);
    }
    public Permission() {
    }

    @Override
    public String toString() {
        return "Permission{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", roleList=" + roleList +
                '}';
    }
}
