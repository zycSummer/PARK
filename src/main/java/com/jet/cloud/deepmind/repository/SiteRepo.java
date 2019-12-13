package com.jet.cloud.deepmind.repository;

import com.jet.cloud.deepmind.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/24 17:13
 * @desc 企业repo
 */
@Repository
public interface SiteRepo extends JpaRepository<Site, Integer>, QuerydslPredicateExecutor<Site> {
    Site findBySiteId(String siteId);

    List<Site> findBySiteIdIn(List<String> siteIdList);

    @Query("select t.siteId from Site t where t.siteId in ?1 and t.isOnline = 'Y' ")
    List<String> findSiteIdBySiteIdIn(List<String> siteIdList);

    @Modifying
    @Transactional
    void deleteBySiteId(String siteId);

}
