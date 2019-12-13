package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/28 13:57
 * @desc 对象展示结构树明细Repo
 */
@Repository
public interface OrgTreeDetailRepo extends JpaRepository<OrgTreeDetail, Integer> {
    List<OrgTreeDetail> findByObjTypeAndObjIdAndOrgTreeIdOrderBySortId(String objType, String objId, String orgTreeId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndOrgTreeId(String objType, String objId, String orgTreeId);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndOrgTreeIdAndNodeId(String objType, String objId, String orgTreeId, String nodeId);
}
