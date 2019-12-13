package com.jet.cloud.deepmind.config.security.handler;

import com.jet.cloud.deepmind.common.Constants;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.MenuFunctionMappingRoleRepo;
import com.jet.cloud.deepmind.repository.MenuMappingRoleRepo;
import com.jet.cloud.deepmind.repository.SysParameterRepo;
import com.jet.cloud.deepmind.repository.UserRepo;
import com.jet.cloud.deepmind.service.LogService;
import com.jet.cloud.deepmind.service.UserMappingRoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jet.cloud.deepmind.common.HttpConstants.POST;

/**
 * @author yhy
 * @create 2019-10-11 11:22
 */
@Component
@Log4j2
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserMappingRoleService userMappingRoleService;
    @Autowired
    private MenuMappingRoleRepo menuMappingRoleRepo;
    @Autowired
    private MenuFunctionMappingRoleRepo menuFunctionMappingRoleRepo;
    @Autowired
    private LogService logService;
    @Autowired
    private SysParameterRepo sysParameterRepo;
    @Autowired
    private AccessSecurityMetadataSource accessSecurityMetadataSource;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication token) throws IOException, ServletException {
        Response ok = new Response(HttpConstants.SUCCESS);
        response.setContentType("application/json;charset=utf-8");
        String resp = Constants.mapper.writeValueAsString(ok);
        //UserGroupWebAuthenticationDetails details = (UserGroupWebAuthenticationDetails) token.getDetails();
        UserDetails userDetails = (UserDetails) token.getPrincipal();
        HttpSession session = request.getSession();
        SysUser sysUser = userRepo.findByUserId(userDetails.getUsername());

        session.setAttribute(Constants.SESSION_USER_ID, sysUser);
        session.setAttribute(Constants.SESSION_USER_NAME, sysUser.getUserName());

        SysParameter platformName = sysParameterRepo.findByParaId("PlatformName");
        session.setAttribute(Constants.SESSION_PLATFORM_NAME, platformName);

        LocalDateTime lastLoginTime = sysUser.getLastLoginTime();
        SysLog s = new SysLog("用户登录", true);
        s.setUserId(sysUser.getUserId());
        s.setOperateTime(LocalDateTime.now());
        s.setMenu("系统");
        s.setFunction("登陆");
        s.setOperateContent("系统-登陆");
        s.setMemo(sysUser.getUserName());
        s.setUrl("/");
        s.setMethod(POST);
        //s.setOperatorIp(CommonUtil.getIpAddr(request));
        logService.save(s);

        session.setAttribute(Constants.SESSION_LAST_LOGIN_TIME, lastLoginTime);
        userRepo.updateLastLoginTime(sysUser.getId(), LocalDateTime.now(), CommonUtil.getIpAddr(request));

        List<String> roleCodeList = userMappingRoleService.getRoleIdList(sysUser.getUserId());

        Map<String, SysMenu> menus = new HashMap<>();
        Map<String, SysMenuFunction> buttons = new HashMap<>();

        for (MenuMappingRole menuMappingRole : menuMappingRoleRepo.findByRoleIdIn(roleCodeList)) {
            SysMenu sysMenu = menuMappingRole.getSysMenu();
            menus.put(sysMenu.getUrl(), sysMenu);
        }
        for (MenuFunctionMappingRole menuFunctionMappingRole : menuFunctionMappingRoleRepo.findByRoleIdIn(roleCodeList)) {
            SysMenuFunction sysMenuFunction = menuFunctionMappingRole.getSysMenuFunction();
            buttons.put(sysMenuFunction.getUrl(), sysMenuFunction);
        }
        session.setAttribute(Constants.CURRENT_USER_BUTTONS, buttons);
        session.setAttribute(Constants.CURRENT_USER_MENUS, menus);

        //刷新session
        accessSecurityMetadataSource.loadResourceDefine();
        response.getWriter().write(resp);
        log.info("[{}-{}]已登陆", sysUser.getUserId(), sysUser.getUserName());
    }
}
