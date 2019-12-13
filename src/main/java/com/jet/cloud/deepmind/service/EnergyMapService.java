package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.Response;

/**
 * Class EnergyMapService
 *
 * @package
 */
public interface EnergyMapService {
    Response getSiteList();

    Response getDetail(String siteId);
}
