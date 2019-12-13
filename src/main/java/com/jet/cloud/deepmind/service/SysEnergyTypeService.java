package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

public interface SysEnergyTypeService {
    Response query(String energyTypeId, String energyTypeNameLike);

    ServiceData addOrEdit(SysEnergyType sysEnergyType);

    ServiceData delete(String energyTypeId);
}
