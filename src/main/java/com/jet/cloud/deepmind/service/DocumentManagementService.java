package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/10/30 10:34
 */
public interface DocumentManagementService {
    Response query(QueryVO vo);

    @Transactional
    ServiceData add(MultipartFile file, String fileDesc, String objId, String objType, String memo);

    ServiceData delete(Integer id);

    void download(Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Transactional
    ServiceData edit(Integer id, String fileDesc, String memo);
}
