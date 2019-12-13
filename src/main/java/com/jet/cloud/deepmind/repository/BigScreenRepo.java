package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.BigScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/23 15:43
 * @desc 大屏dao
 */
@Repository
public interface BigScreenRepo extends JpaRepository<BigScreen, Integer> {

    BigScreen findByObjTypeAndObjIdAndIsMainPage(String objType, String objId, String isMainPage);

    @Modifying
    @Transactional
    @Query("delete from BigScreen where objType=?1 and objId=?2 and htImgId=?3")
    void deleteSiteImg(String objType, String objId, String htImgId);

    List<BigScreen> findByObjTypeAndObjId(String objType, String objId);

    BigScreen findByObjTypeAndObjIdAndHtImgId(String objType, String objId, String htImgId);


    @Modifying
    @Transactional
    @Query("update BigScreen set isMainPage=?1 where isMainPage='Y' ")
    void updateSiteImg(String isMainPage);
}
