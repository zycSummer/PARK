package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/12/23 16:23
 */
@Repository
public interface NoticeRepo extends JpaRepository<Notice, Integer>, QuerydslPredicateExecutor<Notice> {
    Notice findByObjTypeAndObjIdAndCreateTime(String objType, String objId, LocalDateTime createTime);

    @Query(value = "SELECT * FROM tb_obj_notice n  where n.obj_type=?1 and n.obj_id=?2 order by n.create_time DESC limit 10",nativeQuery = true)
    List<Notice> findNoticelist(String objType, String objId);

}
