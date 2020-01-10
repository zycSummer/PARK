package com.jet.cloud.deepmind.service.impl;

import com.google.common.collect.*;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.security.model.Permission;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.MenuVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.service.MenuMappingRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yhy
 * @create 2019-10-15 13:35
 */
@Service
public class MenuMappingRoleServiceImpl implements MenuMappingRoleService {

    @Autowired
    private MenuMappingRoleRepo menuMappingRoleRepo;
    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private MenuFunctionRepo menuFunctionRepo;
    @Autowired
    private UserMappingRoleRepo userMappingRoleRepo;
    @Autowired
    MenuFunctionMappingRoleRepo menuFunctionMappingRoleRepo;

    @Override
    public List<Permission> getAuthList() {

        List<MenuMappingRole> menuMappingRoleList = menuMappingRoleRepo.findAll();
        Multimap<String, SysMenuFunction> functionMultimap = ArrayListMultimap.create();
        for (MenuFunctionMappingRole role : menuFunctionMappingRoleRepo.findAll()) {
            functionMultimap.put(role.getMenuId() + "@@" + role.getRoleId(), role.getSysMenuFunction());
        }

        if (StringUtils.isNullOrEmpty(menuMappingRoleList)) {
            return new ArrayList<>();
        }

        Map<String, SysMenu> menuMap = new HashMap<>();
        for (SysMenu menu : menuRepo.findAll()) {
            menuMap.put(menu.getMenuId(), menu);
        }
        Multimap<String, Permission> multimap = ArrayListMultimap.create();
        for (MenuMappingRole menuMappingRole : menuMappingRoleList) {
            SysMenu sysMenu = menuMappingRole.getSysMenu();
            if (sysMenu == null) {
                sysMenu = menuMap.get(menuMappingRole.getMenuId());
                if (sysMenu == null) {
                    continue;
                }
            }
            multimap.put(sysMenu.getUrl(), new Permission(sysMenu.getUrl(), sysMenu.getMethod(), menuMappingRole.getRoleId()));
            if (!StringUtils.isNullOrEmpty(sysMenu)) {
                Collection<SysMenuFunction> sysMenuFunctions = functionMultimap.get(menuMappingRole.getMenuId() + "@@" + menuMappingRole.getRoleId());
                if (StringUtils.isNullOrEmpty(sysMenuFunctions)) {
                    continue;
                } else {
                    for (SysMenuFunction sysMenuFunction : sysMenuFunctions) {
                        multimap.put(sysMenuFunction.getUrl(), new Permission(sysMenuFunction.getUrl(), sysMenuFunction.getMethod(), menuMappingRole.getRoleId()));
                    }
                }
            }
        }

        List<Permission> permissionsList = new ArrayList<>();
        for (Map.Entry<String, Collection<Permission>> entry : multimap.asMap().entrySet()) {
            String roleCode = entry.getKey();
            Permission permission = new Permission();
            permission.setUrl(roleCode);
            for (Permission entity : entry.getValue()) {
                permission.setMethod(entity.getMethod());
                permission.setRoleList(entity.getRoleId());
            }
            permissionsList.add(permission);
        }
        return permissionsList;
    }

    @Override
    public List<MenuVO> getMenuAndButtons(String roleId) {
        final String tag = "@@";
        //配置的
        List<MenuMappingRole> menuMappingRoleList = menuMappingRoleRepo.findByRoleId(roleId);
        List<MenuFunctionMappingRole> menuFunctionMappingRoleList = menuFunctionMappingRoleRepo.findByRoleId(roleId);
        Set<String> codeList = new HashSet<>();
        for (MenuMappingRole menuMappingRole : menuMappingRoleList) {
            SysMenu sysMenu = menuMappingRole.getSysMenu();
            codeList.add(sysMenu.getMenuId());
        }
        for (MenuFunctionMappingRole menuFunctionMappingRole : menuFunctionMappingRoleList) {
            codeList.add(menuFunctionMappingRole.getMenuId() + tag + menuFunctionMappingRole.getFunctionId());
        }

        //所有的菜单树
        List<MenuVO> modelList = new ArrayList<>();
        Multimap<String, MenuVO> menuMultimap = ArrayListMultimap.create();

        for (SysMenuFunction function : menuFunctionRepo.findAll()) {
            menuMultimap.put(function.getMenuId(), new MenuVO(function, codeList));
        }

        for (SysMenu sysMenu : menuRepo.findAllOrderBySortIdAsc()) {
            if (sysMenu.getParentId() == null) {
                modelList.add(new MenuVO(sysMenu, codeList));
                continue;
            }
            menuMultimap.put(sysMenu.getParentId(), new MenuVO(sysMenu, codeList));
        }

        for (MenuVO model : modelList) {
            addChild(model, menuMultimap, 20);
        }

        return modelList;
    }

    private void addChild(MenuVO treeModel, Multimap<String, MenuVO> menuMultimap, int size) {

        if (size > 0 && treeModel != null) {
            treeModel.setChildren(new ArrayList<>());
            if (treeModel.isButton()) {
                treeModel.setChildren(null);
                return;
            }
            Collection<MenuVO> objs = menuMultimap.get(treeModel.getMenuId());

            if (objs.size() > 0) {
                for (MenuVO subModel : objs) {
                    addChild(subModel, menuMultimap, --size);
                    treeModel.getChildren().add(subModel);
                }
            } else {
                treeModel.setChildren(null);
            }
        }
    }

    @Override
    public Response getMenuAndButtonsResp(String roleCode) {

        try {
            List<MenuVO> list = getMenuAndButtons(roleCode);
            Response ok = Response.ok(list);
            ok.setQueryPara(roleCode);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error("查询失败：" + e.getMessage());
            error.setQueryPara(roleCode);
            return error;
        }

    }

    @Override
    public Response getCurrentMenuTree() {
        SysUser user = currentUser.user();
        List<String> roleIds = getRoleIdsByUserId(user.getUserId());
        LinkedHashSet<MenuVO> modelList = new LinkedHashSet<>();
        Multimap<String, MenuVO> menuMultimap = ArrayListMultimap.create();
        for (MenuMappingRole menuMappingRole : menuMappingRoleRepo.findByRoleIdIn(roleIds)) {
            SysMenu menu = menuMappingRole.getSysMenu();
            if (menu.getParentId() == null) {
                modelList.add(new MenuVO(menu));
                continue;
            }
            menuMultimap.put(menu.getParentId(), new MenuVO(menu));
        }

        List<MenuVO> menuList = getMenuVOTreeList(modelList, menuMultimap);
        Response ok = Response.ok(menuList);
        ok.setQueryPara("获取当前用户 角色集 对应的菜单");
        return ok;
    }

    @Override
    public List<MenuVO> getMenuVOTreeList(LinkedHashSet<MenuVO> modelList, Multimap<String, MenuVO> menuMultimap) {
        Multimap<String, MenuVO> subMenuMultimap = ArrayListMultimap.create();
        for (String key : menuMultimap.keySet()) {
            subMenuMultimap.putAll(key, new LinkedHashSet<>(menuMultimap.get(key)));
        }

        List<MenuVO> menuList = CommonUtil.setToArrayList(modelList);

        menuList.sort(Comparator.comparing(MenuVO::getSortId
                , Comparator.nullsLast(String::compareTo)));
        for (MenuVO model : menuList) {
            addChild(model, subMenuMultimap, 10);
        }
        return menuList;
    }

    @Override
    public List<String> getRoleIdsByUserId(String userId) {
        List<String> roleIds = new ArrayList<>();
        for (UserMappingRole userMappingRole : userMappingRoleRepo.findAllByUserId(userId)) {
            roleIds.add(userMappingRole.getRoleId());
        }
        return roleIds;
    }
}
