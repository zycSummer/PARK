package com.jet.cloud.deepmind.controller;

import com.jet.cloud.deepmind.model.MapDetailVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.EnergyMapService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 能耗地图
 *
 * @author yhy
 * @create 2019-10-24 17:38
 */
@RestController
@RequestMapping("/energyMap")
@Api("能耗地图")
public class EnergyMapController {

    @Autowired
    private EnergyMapService energyMapService;

    /**
     * 获取 site 集合 展示列
     *
     * @return
     */
    @GetMapping("/getSiteList")
    public Response getSiteList() {
        return energyMapService.getSiteList();
    }

    @GetMapping("/getDetail/{siteId}")
    public Response getDetail(@PathVariable String siteId) {
        return energyMapService.getDetail(siteId);
    }
}
