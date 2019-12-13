package com.jet.cloud.deepmind.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.config.security.handler.AccessSecurityMetadataSource;
import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.entity.SysRole;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.MenuMappingRoleService;
import com.jet.cloud.deepmind.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理
 *
 * @author yhy
 * @create 2019-10-17 11:46
 */
@RestController
@Api("角色管理")
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuMappingRoleService menuMappingRoleService;

    @ApiOperation(value = "查询")
    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return roleService.query(vo);
    }

    @PostMapping("/add")
    public Response add(@RequestBody SysRole sysRole) {
        return roleService.addOrEdit(sysRole).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody SysRole sysRole) {
        return roleService.addOrEdit(sysRole).getResponse();
    }


    @PostMapping("/delete")
    public Response delete(@RequestBody List<String> roleIdList) {
        return roleService.delete(roleIdList).getResponse();
    }

    /**
     * 权限维护展示 功能
     *
     * @param roleId
     * @return
     */
    @GetMapping("/getMenuAndButtons/{roleId}")
    public Response getMenuAndButtons(@PathVariable String roleId) {
        return menuMappingRoleService.getMenuAndButtonsResp(roleId);
    }

    /**
     * 权限维护编辑
     */
    @PostMapping("/editAuth")
    public Response editAuth(@RequestBody JSONObject data) {
        String roleId = data.getString("roleId");
        List<String> menuIdList = data.getJSONArray("menuIdList").toJavaList(String.class);
        List<SysMenuFunction> buttonList = data.getJSONArray("buttonList").toJavaList(SysMenuFunction.class);
        return roleService.editAuth(roleId, menuIdList, buttonList).getResponse();
    }

}
