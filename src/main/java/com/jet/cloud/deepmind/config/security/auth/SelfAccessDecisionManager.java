package com.jet.cloud.deepmind.config.security.auth;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * {@link AccessDecisionManager} 鉴权决策管理器 <br>
 *
 * @author yhy
 * @create 2019-10-15 09:58
 */
@Component
@Log4j2
public class SelfAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        //log.info("[资源权限]: {}", configAttributes);
        //log.info("[用户权限]: {}", authentication.getAuthorities());

        Iterator<ConfigAttribute> it = configAttributes.iterator();
        while (it.hasNext()) {
            // 资源的权限
            ConfigAttribute resourceAttr = it.next();
            String resourceRole = "ROLE_" + (resourceAttr).getAttribute();
            // 用户的权限
            for (GrantedAuthority userAuth : authentication.getAuthorities()) {
                //log.info("[资源角色==用户角色] ？ {} == {}", resourceRole.trim(), userAuth.getAuthority().trim());
                if (resourceRole.trim().equals(userAuth.getAuthority().trim())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    /**
     * 被AbstractSecurityInterceptor调用，遍历ConfigAttribute集合，筛选出不支持的attribute
     */
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    /**
     * 被AbstractSecurityInterceptor调用，验证AccessDecisionManager是否支持这个安全对象的类型。
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
