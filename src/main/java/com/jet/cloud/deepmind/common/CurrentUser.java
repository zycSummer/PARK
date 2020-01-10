package com.jet.cloud.deepmind.common;

import com.jet.cloud.deepmind.entity.SysMenu;
import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.repository.UserMappingRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class CurrentUser {

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserMappingRoleRepo userMappingRoleRepo;

    public Map<String, SysMenu> menuList() {
        return (Map<String, SysMenu>) session.getAttribute(Constants.CURRENT_USER_MENUS);
    }

    public HttpServletRequest request() {
        return request;
    }

    public String getRemoteURI() {
        return request.getRequestURI();
    }

    public Map<String, SysMenuFunction> buttonList() {
        return (Map<String, SysMenuFunction>) session.getAttribute(Constants.CURRENT_USER_BUTTONS);
    }

    public SysUser user() {
        return (SysUser) session.getAttribute(Constants.SESSION_USER_ID);
    }

    public String userId() {
        return user().getUserId();
    }

    public String userGroupId() {
        return user().getUserGroupId();
    }

    //public String getUserGroup() {
    //    return user().getUserGroupCode();
    //}
    //
    //public String getUserGroupPrefix() {
    //    return user().getUserGroupCode() + SYMBOL_SPLIT;
    //}

    public List<String> getCurrentRoleIdList() {
        return userMappingRoleRepo.findRoleIdByUserId(userId());
    }
}
