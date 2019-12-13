package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.security.handler.AccessSecurityMetadataSource;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.*;
import com.jet.cloud.deepmind.service.RoleService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author yhy
 * @create 2019-10-17 13:26
 */
@Service
@Log4j2
public class RoleServiceImpl implements RoleService {
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private AccessSecurityMetadataSource accessSecurityMetadataSource;
    @Autowired
    private MenuMappingRoleRepo menuMappingRoleRepo;
    @Autowired
    private MenuFunctionMappingRoleRepo menuFunctionMappingRoleRepo;

    @Autowired
    private UserMappingRoleRepo userMappingRoleRepo;

    @Override
    public Response query(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        QSysRole obj = QSysRole.sysRole;
        JSONObject key = vo.getKey();
        Predicate pre = obj.isNotNull();
        if (key != null) {
            String name = key.getString("name");
            String code = key.getString("code");

            if (isNotNullAndEmpty(code)) {
                pre = ExpressionUtils.and(pre, obj.roleId.containsIgnoreCase(code));
            }
            if (isNotNullAndEmpty(name)) {
                pre = ExpressionUtils.and(pre, obj.roleName.containsIgnoreCase(name));
            }
        }
        Page<SysRole> list = roleRepo.findAll(pre, pageable);
        Response ok = Response.ok(list.getContent(), list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Transactional
    @Override
    public ServiceData addOrEdit(SysRole temp) {
        try {
            if (StringUtils.isNullOrEmpty(temp.getRoleId(), temp.getRoleName())) {
                return ServiceData.error("角色名称或角色标识不能为空", currentUser);
            }
            SysRole sysRole = roleRepo.findByRoleId(temp.getRoleId());

            if (temp.getId() == null) {
                if (sysRole != null) {
                    return ServiceData.error("角色标识不能重复", currentUser);
                }
                temp.setCreateNow();
                temp.setCreateUserId(currentUser.userId());
                roleRepo.save(temp);
            } else {
                SysRole old = roleRepo.findById(temp.getId()).get();
                if (sysRole != null && !Objects.equals(sysRole.getId(), temp.getId())) {
                    return ServiceData.error("角色标识不能重复", currentUser);
                }
                String oldRoleId = old.getRoleId();
                old.setRoleId(temp.getRoleId());
                old.setRoleName(temp.getRoleName());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setMemo(temp.getMemo());
                roleRepo.save(old);

                //修改UserMappingRole
                if (!Objects.equals(oldRoleId, temp.getRoleId())) {
                    userMappingRoleRepo.updateRoleIdByOldRoleId(oldRoleId, temp.getRoleId());
                    menuMappingRoleRepo.updateRoleIdByOldRoleId(oldRoleId, temp.getRoleId());
                    menuFunctionMappingRoleRepo.updateRoleIdByOldRoleId(oldRoleId, temp.getRoleId());
                }
            }
            return ServiceData.success("新增或修改成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改失败" + e.getMessage(), currentUser);
        }
    }

    @Transactional
    @Override
    public ServiceData delete(List<String> roleIds) {

        if (StringUtils.isNullOrEmpty(roleIds)) return ServiceData.error("未选择删除的角色", currentUser);
        Set<String> errorList = new HashSet<>();
        List<UserMappingRole> userMappingRoleList = userMappingRoleRepo.findByRoleIdIn(roleIds);
        if (userMappingRoleList.size() > 0) {
            for (UserMappingRole userMappingRole : userMappingRoleList) {
                errorList.add(userMappingRole.getUserId());
            }
            return ServiceData.error("删除角色失败，此角色下有以下用户：" + errorList, currentUser);
        }
        roleRepo.deleteAllByRoleIdIn(roleIds);
        menuMappingRoleRepo.deleteAllByRoleIdIn(roleIds);
        menuFunctionMappingRoleRepo.deleteAllByRoleIdIn(roleIds);
        return ServiceData.success("删除角色", currentUser);
    }

    /**
     * 权限维护
     */
    @Transactional
    @Override
    public ServiceData editAuth(String roleId, List<String> menuIdList, List<SysMenuFunction> buttonList) {
        try {

            SysRole old = roleRepo.findByRoleId(roleId);
            if (old == null) {
                log.error("[{}]角色对应的权限不存在，无法进行修改", roleId);
                return ServiceData.error("角色对应的权限不存在，无法进行修改", currentUser);
            }

            menuMappingRoleRepo.deleteAllByRoleId(roleId);
            menuFunctionMappingRoleRepo.deleteAllByRoleId(roleId);
            menuMappingRoleRepo.flush();
            menuFunctionMappingRoleRepo.flush();
            List<MenuMappingRole> menuMappingRoleList = new ArrayList<>();
            List<MenuFunctionMappingRole> menuFunctionMappingRoleList = new ArrayList<>();
            menuIdList.forEach(e ->
                    menuMappingRoleList.add(new MenuMappingRole(roleId, e, currentUser))
            );
            buttonList.forEach(e ->
                    menuFunctionMappingRoleList.add(new MenuFunctionMappingRole(roleId, e.getMenuId(), e.getFunctionId(), currentUser))
            );
            if (menuMappingRoleList.size() > 0) {
                menuMappingRoleRepo.saveAll(menuMappingRoleList);
                log.info("[{}]菜单权限更新成功；{}", roleId, menuIdList);
            }
            if (menuFunctionMappingRoleList.size() > 0) {
                menuFunctionMappingRoleRepo.saveAll(menuFunctionMappingRoleList);
                log.info("[{}]按钮权限更新成功；{}", roleId, buttonList);
            }
            //刷新session
            accessSecurityMetadataSource.loadResourceDefine();

            return ServiceData.success("权限更新成功:" + roleId, currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("权限更新失败:" + roleId, e, currentUser);
        }

    }

    @Override
    public Response getAllRoles() {
        List<SysRole> roleList = roleRepo.getAllRoles();
        return Response.ok(roleList);
    }
}
