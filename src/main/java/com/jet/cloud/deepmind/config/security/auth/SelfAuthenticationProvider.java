package com.jet.cloud.deepmind.config.security.auth;

import com.jet.cloud.deepmind.config.security.model.PasswordNotMatchException;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.repository.UserRepo;
import com.jet.cloud.deepmind.service.UserMappingRoleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-11 11:36
 */
@Component
@Log4j2
public class SelfAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserMappingRoleService userMappingRoleService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken token) throws AuthenticationException {
        SysUser sysUser = userRepo.findByUserId(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException(username);
        }
        String midPassword = (String) token.getCredentials();
        if (!passwordEncoder.matches(midPassword, sysUser.getPassword()))
            throw new PasswordNotMatchException(username);
        boolean enabled = sysUser.isEnabled();
        boolean nonLocked = !sysUser.isLocked();
        LocalDate d = sysUser.getExpireDate();
        boolean nonExpired = d == null ? true : d.isAfter(LocalDate.now());
        return new org.springframework.security.core.userdetails.User(sysUser.getUserId(), sysUser.getPassword(), enabled,
                nonExpired, true, nonLocked, getAuthorities(sysUser));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private List<? extends GrantedAuthority> getAuthorities(SysUser sysUser) {
        List<String> roleList = userMappingRoleService.getRoleIdList(sysUser.getUserId());

        if (roleList == null || roleList.size() < 1) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList("");
        }
        //log.info("[原始用户角色列表装填]:{} ", roleList);
        StringBuilder roles = new StringBuilder();
        for (String role : roleList) {
            roles.append("ROLE_").append(role).append(",");
        }
        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(roles.substring(0, roles.length() - 1));
        log.info("[{}-{}]用户的角色列表: {}", sysUser.getUserId(), sysUser.getUserName(), authorityList);
        return authorityList;
    }

}
