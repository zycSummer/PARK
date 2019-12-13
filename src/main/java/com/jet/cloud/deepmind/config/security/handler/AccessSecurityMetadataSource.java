package com.jet.cloud.deepmind.config.security.handler;

import com.jet.cloud.deepmind.config.security.model.Permission;
import com.jet.cloud.deepmind.config.security.model.PublicPermissions;
import com.jet.cloud.deepmind.repository.MenuMappingRoleRepo;
import com.jet.cloud.deepmind.service.MenuMappingRoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 权限资源映射的数据源 <br>  * 这里重写并实现了基于数据库的权限数据源 <br>
 * 实现了 {@link FilterInvocationSecurityMetadataSource}接口 <br>
 *
 * @author yhy
 * @create 2019-10-15 10:06
 */
@Component
@Log4j2
public class AccessSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /**
     * key 是url+method ， value 是对应url资源的角色列表
     */
    private static Map<RequestMatcher, Collection<ConfigAttribute>> permissionMap;

    @Autowired
    private MenuMappingRoleService menuMappingRoleService;
    @Autowired
    private PublicPermissions publicPermissions;

    @PostConstruct
    public void loadResourceDefine() {
        permissionMap = new LinkedHashMap<>();

        // 需要鉴权的url资源，@needAuth标志
        List<Permission> authList = menuMappingRoleService.getAuthList();
        for (Permission permission : authList) {
            String url = permission.getUrl();
            String method = permission.getMethod();
            Set<String> roleList = permission.getRoleList();
            //log.info("{} - {}", url, method);
            AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(url, method);

            Collection<ConfigAttribute> attributes = new ArrayList<>();
            for (String role : roleList) {
                attributes.add(new SecurityConfig(role));
            }
            // 占位符，需要权限才能访问的资源 都需要添加一个占位符，保证value不是空的
            attributes.add(new SecurityConfig("@needAuth"));
            permissionMap.put(requestMatcher, attributes);
        }

        //公共的url资源 & 系统接口的url资源，value为null
        List<Permission> publicList = publicPermissions.getPublicPermission();
        for (Permission permission : publicList) {
            String url = permission.getUrl();
            String method = permission.getMethod();
            AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(url, "*".equals(method) ? null : method);
            // value为空时不做鉴权，相当于所有人都可以访问该资源URL
            permissionMap.put(requestMatcher, null);
        }

        //多余的url资源， @noAuth，所有人都无法访问
        //Collection<ConfigAttribute> attributes = new ArrayList<>();
        //attributes.add(new SecurityConfig("@noAuth"));
        //permissionMap.put(new AntPathRequestMatcher("/**", null), attributes);
        log.info("[全局权限映射集合初始化]: {}", permissionMap.toString());
    }

    /**
     * 鉴权时会被AbstractSecurityInterceptor.beforeInvocation()调用，根据URL找到对应需要的权限
     *
     * @param object 安全对象类型 FilterInvocation.class
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //log.info("[资源被访问：根据URL找到权限配置]: {}\n {}", object, permissionMap.get(object));
        //log.info("[资源被访问：]: {}", object);
        if (permissionMap == null) {
            loadResourceDefine();
        }
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        //log.info(request.getRequestURI());
        if (!isAjax && request.getMethod().equals("GET")) {
            //导入或者页面路由直接通过
            return null;
        }
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : permissionMap.entrySet()) {
            if (entry.getKey().matches(request)) {
                //log.info("[找到的Key]: {}", entry.getKey());
                //log.info("[找到的Value]: {}", entry.getValue());
                //if (entry.getValue().size() > 0) {
                if (entry.getValue() != null && entry.getValue().size() > 0) {
                    return entry.getValue();
                }
                //return entry.getValue();
            }
        }

        //如果是公共资源 返回null
        for (String urlPrefix : publicPermissions.getPublicUrlWithoutWildcard()) {
            if (request.getRequestURI().startsWith(urlPrefix)) {
                return null;
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    /**
     * 用于被AbstractSecurityInterceptor调用，返回所有的 Collection<ConfigAttribute> ，以筛选出不符合要求的attribute
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return new ArrayList<>();
    }

    /**
     * 用于被AbstractSecurityInterceptor调用，验证指定的安全对象类型是否被MetadataSource支持
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
