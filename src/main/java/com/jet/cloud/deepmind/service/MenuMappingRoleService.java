package com.jet.cloud.deepmind.service;

import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.config.security.model.Permission;
import com.jet.cloud.deepmind.model.MenuVO;
import com.jet.cloud.deepmind.model.Response;

import java.util.LinkedHashSet;
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

    List<MenuVO> getMenuVOTreeList(LinkedHashSet<MenuVO> modelList, Multimap<String, MenuVO> menuMultimap);

    List<String> getRoleIdsByUserId(String userId);
}
