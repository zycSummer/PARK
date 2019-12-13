package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * Class SysEnergyParaService
 *
 * @package
 */
public interface SysEnergyParaService {
    Response query(String energyTypeId, String energyParaId, String energyParaNameLike);

    ServiceData addOrEdit(SysEnergyPara energyPara);

    ServiceData delete(String energyParaId);
}
