package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.OrgTree;
import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import com.jet.cloud.deepmind.model.OrgTreeDetailVOs;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/20 13:17
 * @desc 结构树Service
 */
public interface TreeService {
    Response queryLeftNavigation(String objType, String objId, String orgTreeName);

    @Transactional
    ServiceData startOrOver(String objType, String objId, String orgTreeId, Boolean isUse);

    @Transactional
    ServiceData insertOrUpdateOrgTree(OrgTree orgTree);

    @Transactional
    ServiceData deleteOrgTree(String objType, String objId, String orgTreeId);

    Response queryLeftNavigationById(Integer id);

    List<OrgTreeDetailVOs> queryTreeInfoDetails(String objType, String objId, String orgTreeId);

    Response queryTreeInfoDetail(String objType, String objId, String orgTreeId);

    @Transactional
    ServiceData insertOrUpdateOrgTreeDetail(OrgTreeDetail orgTreeDetail);

    @Transactional
    ServiceData deleteOrgTreeDetail(String objType, String objId, String orgTreeId, String nodeId);

    void exportExcel(String objType, String objId, String orgTreeId, String energyTypeName, HttpServletResponse response, String userAgent) throws Exception;

    ServiceData importExcel(MultipartFile file, String objType, String objId, String orgTreeId);
}
