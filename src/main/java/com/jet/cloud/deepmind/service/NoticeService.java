package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.Notice;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/12/23 16:21
 */
public interface NoticeService {
    Response query(QueryVO vo);

    @Transactional
    ServiceData addOrEdit(Notice notice);

    ServiceData delete(Integer id);
}
