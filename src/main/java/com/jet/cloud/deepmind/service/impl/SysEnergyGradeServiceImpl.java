package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.SysEnergyGrade;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.SysEnergyGradeRepo;
import com.jet.cloud.deepmind.service.SysEnergyGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yhy
 * @create 2019-11-22 10:45
 */
@Service
public class SysEnergyGradeServiceImpl implements SysEnergyGradeService {

    @Autowired
    private SysEnergyGradeRepo sysEnergyGradeRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response query(String energyGradeId) {
        List<SysEnergyGrade> gradeList;
        if (StringUtils.isNullOrEmpty(energyGradeId)) {
            gradeList = sysEnergyGradeRepo.findAll(Sort.by("lower"));
        } else {
            gradeList = sysEnergyGradeRepo.findAllByEnergyGradeId(energyGradeId, Sort.by("lower"));
        }
        Response ok = Response.ok(gradeList);
        ok.setQueryPara(energyGradeId);
        return ok;
    }

    @Override
    public ServiceData addOrEdit(SysEnergyGrade energyGrade) {
        String content;
        SysEnergyGrade old = sysEnergyGradeRepo.findByEnergyGradeId(energyGrade.getEnergyGradeId());

        if (energyGrade.getId() == null) {
            content = "新增能耗强度等级";
            if (old != null) {
                return ServiceData.error(content + ",能耗强度等级标识重复", currentUser);
            }
            energyGrade.setCreateNow();
            energyGrade.setCreateUserId(currentUser.userId());
            sysEnergyGradeRepo.save(energyGrade);
            return ServiceData.success(content, currentUser);
        } else {
            content = "修改能耗强度等级";

            if (old == null) {
                return ServiceData.error(content + ",能耗强度等级标识不存在", currentUser);
            }
            old.setLower(energyGrade.getLower());
            old.setUpper(energyGrade.getUpper());
            old.setColor(energyGrade.getColor());
            old.setMemo(energyGrade.getMemo());
            old.setUpdateNow();
            old.setUpdateUserId(currentUser.userId());
            sysEnergyGradeRepo.save(old);
            return ServiceData.success(content, currentUser);
        }
    }

    @Override
    public ServiceData delete(String energyGradeId) {
        sysEnergyGradeRepo.deleteByEnergyGradeId(energyGradeId);
        return ServiceData.success("删除能源种类配置:" + energyGradeId, currentUser);
    }
}
