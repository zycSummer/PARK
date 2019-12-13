package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.GdpMonthly;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * @author maohandong
 * @create 2019/11/21 15:43
 */
public interface GdpMonthlyService {
    Response query(QueryVO vo);

    ServiceData addOrEdit(GdpMonthly gdpMonthly);

    ServiceData delete(Integer id);

    Response queryById(Integer id);
}
