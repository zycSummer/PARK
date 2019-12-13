package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.entity.SysRole;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Class RoleService
 *
 * @package
 */
public interface RoleService {
    Response query(QueryVO vo);

    @Transactional
    ServiceData addOrEdit(SysRole sysRole);

    @Transactional
    ServiceData delete(List<String> roleCodeList);

    @Transactional
    ServiceData editAuth(String roleId, List<String> menuIdList, List<SysMenuFunction> buttonList);

    Response getAllRoles();
}
