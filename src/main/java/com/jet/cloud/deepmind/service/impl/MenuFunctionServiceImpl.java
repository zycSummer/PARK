package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.MenuFunctionRepo;
import com.jet.cloud.deepmind.service.MenuFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author yhy
 * @create 2019-11-19 15:32
 */
@Service
public class MenuFunctionServiceImpl implements MenuFunctionService {

    @Autowired
    private MenuFunctionRepo menuFunctionRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response getFunctionsByMenuId(String menuId) {
        try {
            List<SysMenuFunction> list = menuFunctionRepo.findAllByMenuId(menuId);
            Response ok = Response.ok(list);
            ok.setQueryPara(menuId);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(menuId);
            return error;
        }
    }

    @Override
    public ServiceData updateFunction(SysMenuFunction function) {
        String content = "修改按钮";
        try {
            SysMenuFunction old = menuFunctionRepo.findByMenuIdAndFunctionId(function.getMenuId(), function.getFunctionId());
            old.setFunctionName(function.getFunctionName());
            old.setFunctionDesc(function.getFunctionDesc());
            old.setMemo(function.getMemo());
            old.setUrl(function.getUrl());
            old.setMethod(function.getMethod());
            old.setUpdateUserId(currentUser.userId());
            old.setUpdateNow();
            menuFunctionRepo.save(old);
            return ServiceData.success(content, getUpdateString(old), currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(content, e, currentUser);
        }

    }

    @Override
    public ServiceData addFunction(SysMenuFunction function) {
        function.setCreateNow();
        function.setCreateUserId(currentUser.userId());
        menuFunctionRepo.save(function);
        return null;
    }

    private String getUpdateString(SysMenuFunction old) {

        StringJoiner joiner = new StringJoiner("],[");
        joiner.add(old.getMenuId()).add(old.getFunctionId())
                .add(old.getFunctionName()).add(old.getFunctionDesc())
                .add(old.getMemo());
        return "[" + joiner.toString() + "]";
    }

}
