package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.repository.UserGroupMappingObjRepo;
import com.jet.cloud.deepmind.repository.UserGroupRepo;
import com.jet.cloud.deepmind.service.UserGroupService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jet.cloud.deepmind.common.Constants.OBJ_TYPE_PARK;
import static com.jet.cloud.deepmind.common.Constants.OBJ_TYPE_SITE;
import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author yhy
 * @create 2019-10-16 16:05
 */
@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupRepo userGroupRepo;

    @Autowired
    private UserGroupMappingObjRepo userGroupMappingObjRepo;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private SiteRepo siteRepo;

    @Autowired
    private ParkRepo parkRepo;

    @Override
    public Response query(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        QUserGroup obj = QUserGroup.userGroup;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        if (key != null) {
            String userGroupId = key.getString("userGroupId");
            if (isNotNullAndEmpty(userGroupId)) {
                pre = ExpressionUtils.and(pre, obj.userGroupId.containsIgnoreCase(userGroupId));
            }
            String userGroupName = key.getString("userGroupName");
            if (isNotNullAndEmpty(userGroupName)) {
                pre = ExpressionUtils.and(pre, obj.userGroupName.containsIgnoreCase(userGroupName));
            }
        }
        Page<UserGroup> list = userGroupRepo.findAll(pre, pageable);
        Response ok = Response.ok(list.getContent(), list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public Response queryByUserGroupId(String userGroupId) {
        try {
            UserGroup userGroup = userGroupRepo.findByUserGroupId(userGroupId);
            List<UserGroupMappingObj> userGroupMappingObjList = userGroupMappingObjRepo.findByUserGroupId(userGroupId);
            userGroup.setList(userGroupMappingObjList);
            Response ok = Response.ok("查询成功", userGroup);
            ok.setQueryPara(userGroupId);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error(e.getMessage());
            error.setQueryPara(userGroupId);
            return error;
        }
    }

    @Override
    public Response queryParkAndSiteByUserGroupId(String userGroupId) {
        try {
            Park park = parkRepo.findFirstPark();
            ParkVO parkVO = new ParkVO();
            String parkId = park.getParkId();
            parkVO.setObjType(OBJ_TYPE_PARK);
            parkVO.setObjId(parkId);
            parkVO.setName(park.getParkName());
            UserGroupMappingObj parkObj = userGroupMappingObjRepo.findByUserGroupIdAndObjTypeAndObjId(userGroupId, OBJ_TYPE_PARK, parkId);
            if (parkObj != null) {
                parkVO.setIsRelate(true);
            } else {
                parkVO.setIsRelate(false);
            }
            Sort sort = new Sort(Sort.Direction.ASC, "sortId");
            List<Site> siteList = siteRepo.findAll(sort);
            HashMap<String, String> map = new HashMap<>();
            List<UserGroupMappingObj> groupMappingObjs = userGroupMappingObjRepo.findAllByUserGroupIdAndObjType(userGroupId, OBJ_TYPE_SITE);
            for (UserGroupMappingObj groupMappingObj : groupMappingObjs) {
                map.put(groupMappingObj.getObjId(), groupMappingObj.getUserGroupId());
            }
            ArrayList<SiteVO> list = new ArrayList<>();
            for (Site site : siteList) {
                SiteVO siteVO = new SiteVO();
                String siteId = site.getSiteId();
                siteVO.setObjType(OBJ_TYPE_SITE);
                siteVO.setObjId(siteId);
                siteVO.setName(site.getSiteName());
                String groupId = map.get(siteId);
                if (StringUtils.isNotNullAndEmpty(groupId)) {
                    siteVO.setIsRelate(true);
                } else {
                    siteVO.setIsRelate(false);
                }
                list.add(siteVO);
            }
            parkVO.setChildren(list);
            Response ok = Response.ok(parkVO);
            ok.setQueryPara(userGroupId);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error("查询失败");
            error.setQueryPara(userGroupId);
            return error;
        }
    }

    @Override
    public Response queryParkAndSite() {
        try {
            Park park = parkRepo.findFirstPark();
            ParkVO parkVO = new ParkVO();
            String parkId = park.getParkId();
            parkVO.setObjType(OBJ_TYPE_PARK);
            parkVO.setObjId(parkId);
            parkVO.setName(park.getParkName());
            Sort sort = new Sort(Sort.Direction.ASC, "sortId");
            List<Site> siteList = siteRepo.findAll(sort);
            ArrayList<SiteVO> list = new ArrayList<>();
            for (Site site : siteList) {
                SiteVO siteVO = new SiteVO();
                String siteId = site.getSiteId();
                siteVO.setObjType(OBJ_TYPE_SITE);
                siteVO.setObjId(siteId);
                siteVO.setName(site.getSiteName());
                list.add(siteVO);
            }
            parkVO.setChildren(list);
            Response ok = Response.ok(parkVO);
            ok.setQueryPara("查询园区和企业");
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error("查询失败");
            error.setQueryPara("查询园区和企业");
            return error;
        }
    }

    @Override
    @Transactional
    public ServiceData addOrEdit(UserGroup userGroup) {
        try {
            String userGroupId = userGroup.getUserGroupId();
            UserGroup old = userGroupRepo.findByUserGroupId(userGroupId);
            if (userGroup.getId() == null) {
                if (old != null) {
                    return ServiceData.error("用户组标识重复", currentUser);
                }
                //新增用户组
                userGroup.setCreateNow();
                userGroup.setCreateUserId(currentUser.userId());
                userGroupRepo.save(userGroup);
//                新增用户组映射关系
                List<Map<String, String>> obj = userGroup.getObj();
                if (obj != null) {
                    for (int i = 0; i < obj.size(); i++) {
                        Map<String, String> map = obj.get(i);
                        String objType = map.get("objType");
                        String objId = map.get("objId");
                        UserGroupMappingObj userGroupMappingObj = new UserGroupMappingObj();
                        userGroupMappingObj.setUserGroupId(userGroupId);
                        userGroupMappingObj.setCreateUserId(currentUser.userId());
                        userGroupMappingObj.setCreateNow();
                        userGroupMappingObj.setObjType(objType);
                        userGroupMappingObj.setObjId(objId);
                        userGroupMappingObjRepo.save(userGroupMappingObj);
                    }
                }
                return ServiceData.success("新增用户组成功", currentUser);
            } else {
                //修改用户组，只修改用户组名称和备注
                String oldUserGroupId = old.getUserGroupId();
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setMemo(userGroup.getMemo());
                old.setUserGroupName(userGroup.getUserGroupName());
                userGroupRepo.save(old);
//                删除当前用户组关联的对象
                userGroupMappingObjRepo.deleteByUserGroupId(oldUserGroupId);
                userGroupMappingObjRepo.flush();
                //修改用户组和对象关联表
                List<Map<String, String>> obj = userGroup.getObj();
                List<UserGroupMappingObj> list = new ArrayList<>();
                for (int i = 0; i < obj.size(); i++) {
                    Map<String, String> map = obj.get(i);
                    String objType = map.get("objType");
                    String objId = map.get("objId");
                    UserGroupMappingObj userGroupMappingObj = new UserGroupMappingObj();
                    userGroupMappingObj.setUserGroupId(oldUserGroupId);
                    userGroupMappingObj.setObjType(objType);
                    userGroupMappingObj.setObjId(objId);
                    userGroupMappingObj.setCreateUserId(currentUser.userId());
                    userGroupMappingObj.setCreateNow();
                    list.add(userGroupMappingObj);
                }
                userGroupMappingObjRepo.saveAll(list);
                return ServiceData.success("修改用户组成功", currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改用户组失败", currentUser);
        }
    }

    @Override
    @Transactional
    public ServiceData delete(String userGroupId) {
        try {
            userGroupRepo.deleteByUserGroupId(userGroupId);
            userGroupMappingObjRepo.deleteByUserGroupId(userGroupId);
            return ServiceData.success("删除用户组成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除用户组失败", e, currentUser);
        }
    }

    @Override
    public Response getAllUserGroup() {
        List<UserGroup> list = userGroupRepo.getAllUserGroups();
        return Response.ok(list);
    }
}
