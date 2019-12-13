package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysUser;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author yhy
 * @create 2019-10-11 17:08
 */
public interface UserService {
    @Transactional
    ServiceData<SysUser> addOrEdit(SysUser sysUser);

    Response query(QueryVO vo);

    ServiceData delete(List<String> userIdList);

    ServiceData resetPwd(String userId);

    ServiceData updatePwd(String oldPwd, String newPwd);
}
