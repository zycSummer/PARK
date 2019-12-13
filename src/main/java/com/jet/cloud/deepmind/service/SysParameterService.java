package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysParameter;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * Class SysParameterService
 *
 * @package
 */
public interface SysParameterService {
    Response query(String sysParaId);

    ServiceData edit(SysParameter sysParameter);
}
