package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.Site;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/20 13:25
 */
public interface SiteService {
    Response querySite(QueryVO vo);

    @Transactional
    ServiceData addOrEditSite(MultipartFile file, @Valid Site site);

    @Transactional
    ServiceData delete(String siteId);

    Response querySiteById(Integer id);

    void uploadImage(MultipartFile file, String fileName, String path);
}
