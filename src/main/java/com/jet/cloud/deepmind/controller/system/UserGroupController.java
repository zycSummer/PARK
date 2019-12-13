package com.jet.cloud.deepmind.controller.system;

import com.jet.cloud.deepmind.entity.UserGroup;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.UserGroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/10/24 17:16
 */

@RestController
@RequestMapping("/userGroup")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    /**
     * 查询用户组
     * @param vo
     * @return
     */
    @ApiOperation("查询")
    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo){
       return userGroupService.query(vo);
    }


    /**
     * 根据userGroupId查询用户组信息
     * @param userGroupId
     * @return
     */
    @GetMapping("/queryByUserGroupId/{userGroupId}")
    public Response queryByUserGroupId(@PathVariable String userGroupId){
        return userGroupService.queryByUserGroupId(userGroupId);
    }

    /**
     * 查询用户组关联的园区或者企业
     * @param userGroupId
     * @return
     */
    @GetMapping("/queryParkAndSiteByUserGroupId/{userGroupId}")
    public Response queryParkAndSiteByUserGroupId(@PathVariable String userGroupId){
        return userGroupService.queryParkAndSiteByUserGroupId(userGroupId);
    }

    /**
     * 查询园区和企业
     * @return
     */
    @GetMapping("/queryParkAndSite")
    public Response queryParkAndSite(){
        return userGroupService.queryParkAndSite();
    }

    /**
     * 新增或修改用户组以及对象(园区和企业)
     * @param userGroup
     * @return
     */
    @ApiOperation("新增")
    @PostMapping("/add")
    public Response add(@Valid @RequestBody UserGroup userGroup) {
        return userGroupService.addOrEdit(userGroup).getResponse();
    }

    @ApiOperation("修改")
    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody UserGroup userGroup) {
        return userGroupService.addOrEdit(userGroup).getResponse();
    }

    /**
     * 删除用户组以及关联的对象(园区和企业)
     * @param userGroupId
     * @return
     */
    @ApiOperation("删除")
    @GetMapping("/delete/{userGroupId}")
    public Response delete(@PathVariable String userGroupId){
        return userGroupService.delete(userGroupId).getResponse();
    }


}
