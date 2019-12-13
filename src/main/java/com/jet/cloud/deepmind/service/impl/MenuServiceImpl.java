package com.jet.cloud.deepmind.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.SysMenu;
import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.model.MenuVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.MenuRepo;
import com.jet.cloud.deepmind.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-18 14:52
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response getAllMenu() {
        try {
            List<SysMenu> list = menuRepo.findAll();
            list.sort((m, n) -> {
                if (m.getSortId() == null) return 1;
                if (n.getSortId() == null) return -1;
                return m.getSortId().compareTo(n.getSortId());
            });
            Response ok = Response.ok(list);
            ok.setQueryPara("获取所有菜单");
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara("获取所有菜单");
            return error;
        }
    }

    @Override
    public ServiceData updateMenu(SysMenu menu) {
        String content = "修改菜单";
        try {
            SysMenu old = menuRepo.findByMenuId(menu.getMenuId());
            old.setMenuName(menu.getMenuName());
            old.setParentId(menu.getParentId());
            old.setIcon(menu.getIcon());
            old.setSortId(menu.getSortId());
            old.setMemo(menu.getMemo());
            old.setUpdateUserId(currentUser.userId());
            old.setUpdateNow();
            menuRepo.save(old);
            return ServiceData.success(content, getUpdateString(old), currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(content, e, currentUser);
        }
    }

    private String getUpdateString(SysMenu old) {
        StringJoiner joiner = new StringJoiner("],[");
        joiner.add(old.getMenuId()).add(old.getMenuName())
                .add(old.getParentId() == null ? "null" : old.getParentId())
                .add(old.getIcon()).add(old.getSortId()).add(old.getMenuName());
        return "[" + joiner.toString() + "]";
    }
}
