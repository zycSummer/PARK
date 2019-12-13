package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.QSysUser;
import com.jet.cloud.deepmind.entity.SysRole;
import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.entity.UserMappingRole;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.RoleRepo;
import com.jet.cloud.deepmind.repository.UserMappingRoleRepo;
import com.jet.cloud.deepmind.repository.UserRepo;
import com.jet.cloud.deepmind.service.UserService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author yhy
 * @create 2019-10-11 17:08
 */
@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserMappingRoleRepo userMappingRoleRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public ServiceData<SysUser> addOrEdit(SysUser sysUser) {
        try {
            String userId = sysUser.getUserId();
            if (StringUtils.isNullOrEmpty(userId, sysUser.getUserGroupId(), sysUser.getUserName())) {
                return ServiceData.error("用户标识或用户组或用户名称不能为空", currentUser);
            }
            SysUser dbSysUser = userRepo.findByUserId(userId);

            if (sysUser.getId() == null) {
                if (dbSysUser != null) {
                    return ServiceData.error("用户编码重复", currentUser);
                }
                //新增
                sysUser.setCreateNow();
                sysUser.setCreateUserId(currentUser.userId());
                sysUser.setPassword(passwordEncoder.encode("123456"));
                SysUser save = userRepo.save(sysUser);
                List<SysRole> roleList = sysUser.getRoleList();
                if (isNotNullAndEmpty(roleList)) {
                    List<UserMappingRole> temp = new ArrayList<>();
                    for (SysRole role : roleList) {
                        UserMappingRole t = new UserMappingRole(save.getUserId(), role);
                        t.setCreateNow();
                        t.setCreateUserId(currentUser.userId());
                        temp.add(t);
                    }
                    userMappingRoleRepo.saveAll(temp);
                }
            } else {
                SysUser old = userRepo.findById(sysUser.getId()).get();
                if (dbSysUser != null && !Objects.equals(old.getId(), sysUser.getId())) {
                    return ServiceData.error("用户编码重复", currentUser);
                }
                if (old == null) {
                    return ServiceData.error("此用户不存在", currentUser);
                }
                String oldUserId = old.getUserId();

                old.setUserId(userId);
                old.setEnabled(sysUser.isEnabled());
                old.setLocked(sysUser.isLocked());
                old.setExpireDate(sysUser.getExpireDate());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setUserName(sysUser.getUserName());
                old.setMemo(sysUser.getMemo());
                userRepo.save(old);
                userMappingRoleRepo.deleteAllByUserId(oldUserId);
                userMappingRoleRepo.flush();
                if (sysUser.getRoleList() != null && sysUser.getRoleList().size() > 0) {
                    List<UserMappingRole> userMappingRoleList = new ArrayList<>();
                    for (SysRole role : sysUser.getRoleList()) {
                        UserMappingRole t = new UserMappingRole(sysUser.getUserId(), role.getRoleId());
                        t.setCreateNow();
                        t.setCreateUserId(currentUser.userId());
                        userMappingRoleList.add(t);
                    }
                    userMappingRoleRepo.saveAll(userMappingRoleList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增用户失败；" + e.getMessage(), currentUser);
        }
        return ServiceData.success("新增用户", currentUser);
    }

    @Override
    public Response query(QueryVO vo) {
        QSysUser obj = QSysUser.sysUser;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        if (key != null) {
            String code = key.getString("code");
            if (isNotNullAndEmpty(code)) {
                pre = ExpressionUtils.and(pre, obj.userId.containsIgnoreCase(code));
            }
            String userName = key.getString("name");
            if (isNotNullAndEmpty(userName)) {
                pre = ExpressionUtils.and(pre, obj.userName.containsIgnoreCase(userName));
            }
        }
        Page<SysUser> list = userRepo.findAll(pre, vo.Pageable());
        List<String> userIdList = new ArrayList<>();
        List<SysUser> content = list.getContent();
        for (SysUser user : content) {
            userIdList.add(user.getUserId());
        }
        Multimap<String, SysRole> multimap = ArrayListMultimap.create();
        for (UserMappingRole temp : userMappingRoleRepo.findByUserIdIn(userIdList)) {
            multimap.put(temp.getUserId(), temp.getSysRole());
        }
        for (SysUser user : content) {
            List<SysRole> temp = new ArrayList<>();
            for (SysRole role : multimap.get(user.getUserId())) {
                temp.add(new SysRole(role.getRoleId(), role.getRoleName()));
            }
            user.setRoleList(temp);
            if (isNotNullAndEmpty(user.getUserGroup())) {
                user.setUserGroupName(user.getUserGroup().getUserGroupName());
            }
        }
        Response ok = Response.ok(content, list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public ServiceData delete(List<String> userIdList) {
        if (StringUtils.isNullOrEmpty(userIdList)) return ServiceData.error("未选择删除的用户", currentUser);
        userRepo.deleteAllByUserIdIn(userIdList);
        return ServiceData.success("删除成功", currentUser);
    }

    @Override
    public ServiceData resetPwd(String userId) {
        SysUser user = userRepo.findByUserId(userId);
        if (user == null) {
            return ServiceData.error("[" + userId + "]用户不存在，重置密码失败", currentUser);
        }
        user.setPassword(passwordEncoder.encode("123456"));
        user.setUpdateNow();
        user.setUpdateUserId(currentUser.userId());
        userRepo.save(user);
        return ServiceData.success("[" + userId + "]用户重置密码成功", currentUser);
    }

    @Override
    public ServiceData updatePwd(String oldPwd, String newPwd) {
        String userId = currentUser.userId();
        SysUser user = userRepo.findByUserId(userId);
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return ServiceData.error("原密码输入错误", currentUser);
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        user.setUpdateNow();
        user.setUserId(currentUser.userId());
        userRepo.save(user);
        return ServiceData.success("修改密码成功", currentUser);
    }

}
