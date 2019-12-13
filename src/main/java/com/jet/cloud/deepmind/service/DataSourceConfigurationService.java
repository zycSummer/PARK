package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/20 10:03
 * @desc 对象数据源配置service
 */
public interface DataSourceConfigurationService {

    Response queryCondition();

    Response queryDataSource(String objType, String objId, List<String> energyTypeIds);

    @Transactional
    ServiceData insertOrUpdateDataSource(DataSource dataSource);

    @Transactional
    ServiceData deleteDataSource(String objType, String objId, String energyTypeId, String energyParaId);

    Response queryDataSourceById(Integer id);
}
