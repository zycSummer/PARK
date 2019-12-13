package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.UserGroup;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * Class UserGroupService
 *
 * @package
 */
public interface UserGroupService {

    Response query(QueryVO vo);

    ServiceData delete(String userGroupId);

    Response getAllUserGroup();

    ServiceData addOrEdit(UserGroup userGroup);


    Response queryParkAndSiteByUserGroupId(String userGroupId);

    Response queryParkAndSite();


    Response queryByUserGroupId(String userGroupId);
}
