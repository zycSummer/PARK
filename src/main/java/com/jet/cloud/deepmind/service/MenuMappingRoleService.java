package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.config.security.model.Permission;
import com.jet.cloud.deepmind.model.MenuVO;
import com.jet.cloud.deepmind.model.Response;

import java.util.List;

/**
 * Class MenuMappingRoleService
 *
 * @package
 */
public interface MenuMappingRoleService {

    List<Permission> getAuthList();

    /**
     * 根据groupCode,roleCode获取用户的菜单和按钮
     *
     * @return
     */
    List<MenuVO> getMenuAndButtons(String roleId);

    Response getMenuAndButtonsResp(String roleId);

    Response getCurrentMenuTree();

    List<String> getRoleIdsByUserId(String userId);
}
