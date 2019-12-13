package com.jet.cloud.deepmind.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.RoleService;
import com.jet.cloud.deepmind.service.UserGroupService;
import com.jet.cloud.deepmind.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yhy
 * @create 2019-10-21 14:45
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserGroupService userGroupService;

    @GetMapping("/resetPwd/{userId}")
    public Response resetPwd(@PathVariable String userId) {
        return userService.resetPwd(userId).getResponse();
    }


    @PostMapping("/delete")
    public Response delete(@RequestBody List<String> userIdList) {
        return userService.delete(userIdList).getResponse();
    }

    @PostMapping("/add")
    public Response add(@RequestBody SysUser sysUser) {
        return userService.addOrEdit(sysUser).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody SysUser sysUser) {
        return userService.addOrEdit(sysUser).getResponse();
    }

    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return userService.query(vo);
    }

    @PostMapping("/updatePwd")
    public Response updatePwd(@RequestBody JSONObject data) {

        String oldPwd = data.getString("oldPwd");
        String newPwd = data.getString("newPwd");
        return userService.updatePwd(oldPwd,newPwd).getResponse();
    }

    /**
     * 获取所有的角色下拉
     *
     * @return
     */
    @GetMapping("/getAllRoles")
    public Response getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * 获取所有的用户组下拉
     *
     * @return
     */
    @GetMapping("/getAllUserGroup")
    public Response getAllUserGroup() {
        return userGroupService.getAllUserGroup();
    }

}
