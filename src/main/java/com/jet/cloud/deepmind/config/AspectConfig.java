package com.jet.cloud.deepmind.config;

import com.google.common.collect.Lists;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.CommonUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.security.model.Permission;
import com.jet.cloud.deepmind.config.security.model.PublicPermissions;
import com.jet.cloud.deepmind.entity.SysLog;
import com.jet.cloud.deepmind.entity.SysMenu;
import com.jet.cloud.deepmind.model.ButtonVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.CommonRepo;
import com.jet.cloud.deepmind.repository.MenuFunctionMappingRoleRepo;
import com.jet.cloud.deepmind.repository.MenuFunctionRepo;
import com.jet.cloud.deepmind.repository.MenuRepo;
import com.jet.cloud.deepmind.service.LogService;
import com.jet.cloud.deepmind.service.RoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static com.jet.cloud.deepmind.config.security.model.Permission.PERMISSION_POST;

@Aspect
@Configuration
public class AspectConfig {

    @Autowired
    private LogService logService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private CommonRepo commonRepo;
    @Autowired
    private MenuFunctionMappingRoleRepo functionMappingRoleRepo;
    @Autowired
    private MenuRepo menuRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private AntPathMatcher antPathMatcher;
    @Autowired
    private PublicPermissions publicPermissions;

    @AfterReturning(value = "execution(com.jet.cloud.deepmind.model.ServiceData com.jet.cloud.deepmind.service.*.*.*(..))", returning = "result")
    public void saveLog(JoinPoint joinPoint, Object result) {
        ServiceData data = (ServiceData) result;
        SysLog log = data.getLog();
        log.setUserId(currentUser.user().getUserId());
        log.setOperateIp(CommonUtil.getIpAddr(request));
        log.setOperateTime(LocalDateTime.now());
        log.setUrl(request.getRequestURI());
        log.setMethod(request.getMethod());
        logService.save(log);
    }

    @Around("@within(com.jet.cloud.deepmind.annotation.PrivilegeCheck)")
    public Object checkPrivilege(ProceedingJoinPoint joinPoint) throws Throwable {
        String uri = request.getRequestURI();
        List<ButtonVO> list = commonRepo.queryAllButtonsByMenuUrl(uri);
        if (list.size() == 0) return joinPoint.proceed();
        List<String> roleIdList = currentUser.getCurrentRoleIdList();
        if (roleIdList.size() == 0) return joinPoint.proceed();
        ButtonVO buttonVO = list.get(0);
        List<String> visibleButtonList = functionMappingRoleRepo.findByRoleIdInAndMenuIdStr(roleIdList
                , buttonVO.getMenuId());
        for (ButtonVO vo : list) {
            vo.setVisible(visibleButtonList.contains(vo.getToken()));
            //vo.setVisible(true);
            request.setAttribute("" + vo.getToken(), vo);
        }
        //面包屑导航栏数据
        SysMenu currentMenu = null;
        Map<String, SysMenu> menuMap = new HashMap<>();
        for (SysMenu menu : menuRepo.findAll()) {
            menuMap.put(menu.getMenuId(), menu);
            if (currentMenu != null) continue;
            if (Objects.equals(uri, menu.getUrl())) currentMenu = menu;
        }

        List<String> nb = Lists.reverse(getNavigationBar(null, currentMenu, menuMap));
        request.setAttribute("nb", nb);
        return joinPoint.proceed();
    }

    @AfterReturning(value = "execution(com.jet.cloud.deepmind.model.Response com.jet.cloud.deepmind.service.*.*.*(..))", returning = "result")
    public void saveLog(JoinPoint joinPoint, Response result) {
        List<Permission> list = publicPermissions.getPublicPermission();
        List<Permission> res = new ArrayList<>();
        for (Permission permission : list) {
            if (!Objects.equals("/**", permission.getUrl())) {
                res.add(permission);
            }
        }
        res.add(PERMISSION_POST("/sysLog/query")); //日志不记录


        for (Permission permission : res) {
            if (antPathMatcher.match(permission.getUrl(), currentUser.getRemoteURI())) {
                return;
            }
        }
        int code = result.getCode();
        String queryPara = result.getQueryPara();
        String content = null;
        if (StringUtils.isNotNullAndEmpty(queryPara)) {
            content = "查询：" + queryPara;
        }
        SysLog log = new SysLog(antPathMatcher, content, currentUser, code == 0);
        //SysLog log = data.getLog();
        log.setUserId(currentUser.user().getUserId());
        log.setOperateIp(CommonUtil.getIpAddr(request));
        log.setOperateTime(LocalDateTime.now());
        log.setUrl(request.getRequestURI());
        log.setMethod(request.getMethod());
        logService.save(log);
    }

    private List<String> getNavigationBar(List<String> res, SysMenu currentMenu, Map<String, SysMenu> menuMap) {
        if (currentMenu == null || res == null) res = new ArrayList<>();
        res.add(currentMenu.getMenuName());
        if (currentMenu.getParentId() == null) return res;
        SysMenu t = menuMap.get(currentMenu.getParentId());
        return getNavigationBar(res, t, menuMap);
    }
}
