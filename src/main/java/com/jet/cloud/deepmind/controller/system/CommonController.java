package com.jet.cloud.deepmind.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.Meter;
import com.jet.cloud.deepmind.entity.SysEnergyPara;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.EnergyMonitoringService;
import com.jet.cloud.deepmind.service.MenuMappingRoleService;
import com.jet.cloud.deepmind.service.htweb.EditorServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/10/23 14:27
 * @desc 公共的Controller
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private CommonService commonService;
    @Autowired
    private MenuMappingRoleService menuMappingRoleService;
    @Autowired
    private EnergyMonitoringService energyMonitoringService;
    @Autowired
    private EditorServer editorServer;

    /**
     * 导航栏-选择园区下面的企业
     *
     * @return
     */
    @GetMapping("/queryParkOrSite")
    public Response queryParkOrSite() {
        return commonService.queryParkOrSite();
    }


    /**
     * 获取当前用户 角色集 对应的菜单
     *
     * @return
     */
    @GetMapping("/getCurrentMenuTree")
    public Response getCurrentMenuTree() {
        return menuMappingRoleService.getCurrentMenuTree();
    }

    /**
     * 获取当前用户 角色集 对应的按钮
     *
     * @return
     */
    @GetMapping("/getCurrentClickButtons/{menuId}")
    public Response getCurrentClickButtons(@PathVariable String menuId) {
        return commonService.getCurrentClickButtons(menuId);
    }

    /**
     * 用能监测(历史数据)-左侧(能源种类)
     * <p>
     * 能耗分析(能耗类比)-左侧(能源种类)
     */
    @GetMapping("/queryHistoryLeftData")
    public Response queryHistoryLeftData() {
        return energyMonitoringService.queryHistoryLeftData();
    }

    /**
     * 用能监测(历史数据)-左侧(展示结构树)
     * 能耗分析(能耗类比)-左侧(展示结构树)
     * 能耗分析(能耗时比)-左侧(展示结构树)
     */
    @PostMapping("/queryHistoryLeftTree")
    public Response queryHistoryLeftTree(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String energyTypeId = jsonObject.getString("energyTypeId");
        return energyMonitoringService.queryHistoryLeftTree(objType, objId, energyTypeId);
    }

    /**
     * 用能监测(历史数据)-参数选择(A相有功功率等)
     */
    @GetMapping("/queryParameter/{energyTypeId}")
    public Response queryParameter(@PathVariable String energyTypeId) {
        return energyMonitoringService.queryParameter(energyTypeId);
    }

    /**
     * energyParaId
     * energyParaName
     *
     * @return
     * @apiNote 按参数标识或者名称查询
     */
    @PostMapping("/queryEnergyParaIdOrEnergyParaName")
    public Response queryEnergyParaIdOrEnergyParaName(@RequestBody JSONObject data) {
        String key = data.getString("key");
        String energyTypeId = data.getString("energyTypeId");
        return commonService.queryEnergyParaIdOrEnergyParaName(key, energyTypeId);
    }

    /**
     * 用能监测(历史数据)-页面具体计算逻辑
     *
     * @param historyDataVO
     * @return
     */
    @PostMapping("/queryMonitorPageInfoData")
    public Response queryMonitorPageInfoData(@RequestBody HistoryDataVO historyDataVO) {
        return energyMonitoringService.queryPageInfoData(historyDataVO);
    }

    /**
     * 能耗分析(能耗类比)-页面具体计算逻辑
     *
     * @param historyDataVO
     * @return
     */
    @PostMapping("/queryAnalysisPageInfoData")
    public Response queryAnalysisPageInfoData(@RequestBody HistoryDataVO historyDataVO) {
        return energyMonitoringService.queryPageInfoData(historyDataVO);
    }


    /**
     * 综合展示--打开历史数据对话框
     *
     * @param historyInfoDataVO
     * @return
     */
    @PostMapping("/queryComprehensiveHistoryInfoData")
    public Response queryComprehensiveHistoryInfoData(@RequestBody HistoryInfoDataVO historyInfoDataVO) {
        return energyMonitoringService.queryHistoryInfoData(historyInfoDataVO);
    }

    /**
     * 用能监测(实时监测)--打开历史数据对话框
     *
     * @param historyInfoDataVO
     * @return
     */
    @PostMapping("/queryMonitorHistoryInfoData")
    public Response queryMonitorHistoryInfoData(@RequestBody HistoryInfoDataVO historyInfoDataVO) {
        return energyMonitoringService.queryHistoryInfoData(historyInfoDataVO);
    }

    /**
     * 获取当前用户可以看见的所有企业
     *
     * @param key
     * @return
     */
    @GetMapping("/getAllCurrentSite/{key}")
    public Response getAllCurrentSite(@PathVariable String key) {
        return commonService.getAllCurrentSiteResp(key);
    }

    @GetMapping("/getAllCurrentSite")
    public Response getAllCurrentSiteNull() {
        return commonService.getAllCurrentSiteResp(null);
    }

    /**
     * 查询除标煤之外的能源种类
     *
     * @return
     */
    @GetMapping("/queryEnergyTypes")
    public Response queryEnergyTypes() {
        return commonService.queryEnergyTypes();
    }

    @GetMapping("/getRtdbTenantId")
    public Response getRtdbTenantId() {
        return Response.ok("", commonService.getRtdbTenantId());
    }

    /**
     * 查询全部的能源种类
     *
     * @return
     */
    @GetMapping("/queryEnergyTypesAll")
    public Response queryEnergyTypesAll() {
        return commonService.queryEnergyTypesAll();
    }

    //@GetMapping("/getHtConfig")
    //public Response getHtConfig() {
    //    return Response.ok(editorServer.getHostAndPort());
    //}
}