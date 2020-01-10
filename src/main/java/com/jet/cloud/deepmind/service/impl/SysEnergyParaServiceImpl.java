package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.QSysEnergyPara;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.SysEnergyParaRepo;
import com.jet.cloud.deepmind.service.SysEnergyParaService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yhy
 * @create 2019-11-21 15:14
 */
@Service
public class SysEnergyParaServiceImpl implements SysEnergyParaService {

    @Autowired
    private SysEnergyParaRepo sysEnergyParaRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response query(String energyTypeId, String energyParaId, String energyParaNameLike) {
        QSysEnergyPara obj = QSysEnergyPara.sysEnergyPara;
        Predicate pre = obj.isNotNull();
        pre = ExpressionUtils.and(pre, obj.energyTypeId.eq(energyTypeId));

        if (StringUtils.isNotNullAndEmpty(energyParaId)) {
            pre = ExpressionUtils.and(pre, obj.energyParaId.equalsIgnoreCase(energyParaId));
        }
        if (StringUtils.isNotNullAndEmpty(energyParaNameLike)) {
            pre = ExpressionUtils.and(pre, obj.energyParaName.containsIgnoreCase(energyParaNameLike));
        }
        List<SysEnergyPara> all = (List<SysEnergyPara>) sysEnergyParaRepo.findAll(pre, Sort.by(Sort.Direction.ASC, "sortId"));
        Response ok = Response.ok(all);
        ok.setQueryPara(energyParaId, energyTypeId, energyParaNameLike);
        return ok;
    }

    @Override
    public ServiceData addOrEdit(SysEnergyPara energyPara) {
        String content;
        SysEnergyPara old = sysEnergyParaRepo.findByEnergyTypeIdAndEnergyParaId(energyPara.getEnergyTypeId(), energyPara.getEnergyParaId());

        if (energyPara.getId() == null) {
            content = "新增能源参数配置";
            if (old != null) {
                return ServiceData.error(content + ",能源参数标识重复", currentUser);
            }
            energyPara.setCreateNow();
            energyPara.setCreateUserId(currentUser.userId());
            sysEnergyParaRepo.save(energyPara);
            return ServiceData.success(content, currentUser);
        } else {
            content = "修改能源参数配置";

            if (old == null) {
                return ServiceData.error(content + ",能源参数标识不存在", currentUser);
            }
            old.setEnergyParaName(energyPara.getEnergyParaName());
            old.setUnit(energyPara.getUnit());
            old.setMemo(energyPara.getMemo());
            old.setUpdateNow();
            old.setUpdateUserId(currentUser.userId());
            sysEnergyParaRepo.save(old);
            return ServiceData.success(content, currentUser);
        }
    }

    @Override
    public ServiceData delete(String energyParaId) {
        sysEnergyParaRepo.deleteByEnergyParaId(energyParaId);
        return ServiceData.success("删除能源种类配置:" + energyParaId, currentUser);
    }
}
