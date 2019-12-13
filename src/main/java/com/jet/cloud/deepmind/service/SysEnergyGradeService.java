package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysEnergyGrade;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

public interface SysEnergyGradeService {
    Response query(String energyGradeId);

    ServiceData addOrEdit(SysEnergyGrade energyGrade);

    ServiceData delete(String energyGradeId);
}
