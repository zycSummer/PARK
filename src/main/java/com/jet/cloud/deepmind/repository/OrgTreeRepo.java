package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.OrgTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/28 10:51
 * @desc 对象展示结构树Repo
 */
@Repository
public interface OrgTreeRepo extends JpaRepository<OrgTree, Integer> {
    List<OrgTree> findByObjTypeAndObjIdAndEnergyTypeIdAndIsUseOrderBySortId(String objType, String objId, String energyTypeId, boolean isUse);

    List<OrgTree> findByObjTypeAndObjIdOrderBySortId(String objType, String objId);

    List<OrgTree> findByObjTypeAndObjIdAndOrgTreeNameLikeOrderBySortId(String objType, String objId, String orgTreeName);

    OrgTree findByObjTypeAndObjIdAndOrgTreeId(String objType, String objId, String orgTreeId);


    @Modifying
    @Query("update OrgTree u set u.isUse = ?4 where u.objType =?1 and u.objId =?2 and u.orgTreeId =?3 ")
    @Transactional
    void updateIsuse(String objType, String objId, String orgTreeId, Boolean isUse);

    @Modifying
    @Transactional
    void deleteByObjTypeAndObjIdAndOrgTreeId(String objType, String objId, String orgTreeId);
}
