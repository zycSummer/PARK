package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.QSysEnergyType;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.service.SysEnergyTypeService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author yhy
 * @create 2019-11-21 14:36
 */
@Service
public class SysEnergyTypeServiceImpl implements SysEnergyTypeService {

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response query(String energyTypeId, String energyTypeNameLike) {
        QSysEnergyType obj = QSysEnergyType.sysEnergyType;
        Predicate pre = obj.isNotNull();
        if (StringUtils.isNotNullAndEmpty(energyTypeId)) {
            pre = ExpressionUtils.and(pre, obj.energyTypeId.equalsIgnoreCase(energyTypeId));
        }
        if (StringUtils.isNotNullAndEmpty(energyTypeNameLike)) {
            pre = ExpressionUtils.and(pre, obj.energyTypeName.containsIgnoreCase(energyTypeNameLike));
        }
        List<SysEnergyType> all = (List<SysEnergyType>) sysEnergyTypeRepo.findAll(pre, Sort.by(Sort.Direction.ASC, "sortId"));
        Response ok = Response.ok(all);
        ok.setQueryPara(energyTypeId, energyTypeNameLike);
        return ok;
    }

    @Override
    public ServiceData addOrEdit(SysEnergyType energyType) {

        String content;
        SysEnergyType old = sysEnergyTypeRepo.findByEnergyTypeId(energyType.getEnergyTypeId());

        if (energyType.getId() == null) {
            content = "新增能源种类配置";
            if (old != null) {
                return ServiceData.error(content + ",能源种类标识重复", currentUser);
            }
            energyType.setCreateNow();
            energyType.setCreateUserId(currentUser.userId());
            sysEnergyTypeRepo.save(energyType);
            return ServiceData.success(content, currentUser);
        } else {
            content = "修改能源种类配置";

            if (old == null) {
                return ServiceData.error(content + ",能源种类标识不存在", currentUser);
            }
            old.setEnergyTypeName(energyType.getEnergyTypeName());
            old.setStdCoalCoeff(energyType.getStdCoalCoeff());
            old.setCo2Coeff(energyType.getCo2Coeff());
            old.setEnergyLoadParaId(energyType.getEnergyLoadParaId());
            old.setEnergyUsageParaId(energyType.getEnergyUsageParaId());
            old.setSortId(energyType.getSortId());
            old.setMemo(energyType.getMemo());
            old.setUpdateNow();
            old.setUpdateUserId(currentUser.userId());
            sysEnergyTypeRepo.save(old);
            return ServiceData.success(content, currentUser);
        }
    }

    @Override
    public ServiceData delete(String energyTypeId) {
        sysEnergyTypeRepo.deleteByEnergyTypeId(energyTypeId);
        return ServiceData.success("删除能源种类配置:" + energyTypeId, currentUser);
    }
}
