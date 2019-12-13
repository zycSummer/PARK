package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.DataSourceRepo;
import com.jet.cloud.deepmind.repository.SysEnergyParaRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.DataSourceConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/11/20 10:03
 * @desc 对象数据源配置Impl
 */
@Service
public class DataSourceConfigurationServiceImpl implements DataSourceConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigurationServiceImpl.class);

    @Autowired
    private DataSourceRepo dataSourceRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Override
    public Response queryCondition() {
        return commonService.queryEnergyTypesAll();
    }

    @Override
    public Response queryDataSource(String objType, String objId, List<String> energyTypeIds) {
        try {
            List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAll();
            Map<String, String> mapType = new HashMap<>();
            Map<String, String> mapPara = new HashMap<>();
            if (StringUtils.isNotNullAndEmpty(sysEnergyTypes)) {
                for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                    String energyTypeId = sysEnergyType.getEnergyTypeId();
                    mapType.put(energyTypeId, sysEnergyType.getEnergyTypeName());
                    List<SysEnergyPara> sysEnergyParas = sysEnergyParaRepo.findByEnergyTypeIdOrderBySortId(energyTypeId);
                    if (StringUtils.isNotNullAndEmpty(sysEnergyParas)) {
                        for (SysEnergyPara sysEnergyPara : sysEnergyParas) {
                            mapPara.put(energyTypeId + sysEnergyPara.getEnergyParaId(), sysEnergyPara.getEnergyParaName());
                        }
                    }
                }
            }

            List<DataSource> dataSources = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdIn(objType, objId, energyTypeIds);
            if (StringUtils.isNotNullAndEmpty(dataSources)) {
                for (DataSource dataSource : dataSources) {
                    dataSource.setEnergyTypeName(mapType.get(dataSource.getEnergyTypeId()));
                    dataSource.setEnergyParaName(mapPara.get(dataSource.getEnergyTypeId() + dataSource.getEnergyParaId()));
                }
            }
            Response ok = Response.ok("查询成功", dataSources);
            ok.setQueryPara(objId, objType, energyTypeIds);
            return ok;
        } catch (Exception e) {
            logger.error("查询结果失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询结果失败", e);
            error.setQueryPara(objId, objType, energyTypeIds);
            return error;
        }
    }

    @Transactional
    @Override
    public ServiceData insertOrUpdateDataSource(DataSource dataSource) {
        try {
            Integer id = dataSource.getId();
            if (id == null) {
                String objId = dataSource.getObjId();
                String objType = dataSource.getObjType();
                String energyTypeId = dataSource.getEnergyTypeId();
                String energyParaId = dataSource.getEnergyParaId();
                DataSource old = dataSourceRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, energyTypeId, energyParaId);
                if (old != null) return ServiceData.error("该能源种类和参数已存在,不能新增!", currentUser);
                dataSource.setCreateNow();
                dataSource.setCreateUserId(currentUser.userId());
                dataSourceRepo.save(dataSource);
            } else {
                DataSource old = dataSourceRepo.findById(id).get();
                old.setObjType(dataSource.getObjType());
                old.setObjId(dataSource.getObjId());
                old.setEnergyTypeId(dataSource.getEnergyTypeId());
                old.setEnergyParaId(dataSource.getEnergyParaId());
                old.setDataSource(dataSource.getDataSource());
                old.setMemo(dataSource.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                dataSourceRepo.save(old);
            }
            return ServiceData.success("同步数据源信息成功", currentUser);
        } catch (Exception e) {
            logger.error("同步数据源信息失败", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("同步数据源信息失败", e, currentUser);
        }
    }

    @Transactional
    @Override
    public ServiceData deleteDataSource(String objType, String objId, String energyTypeId, String energyParaId) {
        try {
            dataSourceRepo.deleteByObjTypeAndObjIdAndEnergyTypeIdAndEnergyParaId(objType, objId, energyTypeId, energyParaId);
            return ServiceData.success("删除数据源信息成功", currentUser);
        } catch (Exception e) {
            logger.error("删除数据源信息失败", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("删除数据源信息失败", e, currentUser);
        }
    }

    @Override
    public Response queryDataSourceById(Integer id) {
        Response ok = Response.ok("查询成功", dataSourceRepo.findById(id).get());
        ok.setQueryPara(id);
        return ok;
    }
}
