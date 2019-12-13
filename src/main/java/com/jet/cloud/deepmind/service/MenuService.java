package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysMenu;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;


/**
 * Class MenuService
 *
 * @package
 */
public interface MenuService {
    Response getAllMenu();

    ServiceData updateMenu(SysMenu menu);
}
