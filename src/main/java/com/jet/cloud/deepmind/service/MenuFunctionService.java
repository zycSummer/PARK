package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

/**
 * Class MenuFunctionService
 *
 * @package
 */
public interface MenuFunctionService {
    Response getFunctionsByMenuId(String menuId);

    ServiceData updateFunction(SysMenuFunction function);

    ServiceData addFunction(SysMenuFunction function);
}
