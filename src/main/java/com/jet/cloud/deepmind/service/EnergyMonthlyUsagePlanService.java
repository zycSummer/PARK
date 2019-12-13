package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.EnergyMonthlyUsagePlan;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;
import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/21 9:59
 */
public interface EnergyMonthlyUsagePlanService {
    Response query(QueryVO vo);

    @Transactional
    ServiceData addOrEdit(@Valid EnergyMonthlyUsagePlan energyMonthlyUsagePlan);

    @Transactional
    ServiceData delete(Integer id);

    Response queryById(Integer id);
}
