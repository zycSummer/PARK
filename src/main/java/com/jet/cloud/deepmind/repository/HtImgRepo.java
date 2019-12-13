package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.HtImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/25 10:13
 * @desc 对象组态画面Repo
 */
@Repository
public interface HtImgRepo extends JpaRepository<HtImg, Integer> {
    List<HtImg> findByObjTypeAndObjIdOrderBySortId(String objType, String objId);

    HtImg findByObjTypeAndObjIdAndHtImgId(String objType, String objId, String htImgId);

    @Modifying
    @Transactional
    @Query("delete from HtImg where objType=?1 and objId=?2 and htImgId=?3")
    void deleteRightHtImg(String objType, String objId, String htImgId);
}
