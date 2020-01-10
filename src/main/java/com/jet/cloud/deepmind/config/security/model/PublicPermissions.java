package com.jet.cloud.deepmind.config.security.model;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jet.cloud.deepmind.config.security.model.Permission.PERMISSION_GET;
import static com.jet.cloud.deepmind.config.security.model.Permission.PERMISSION_POST;

/**
 * @author yhy
 * @create 2019-10-15 14:50
 */
@Component
public class PublicPermissions {

    public List<Permission> getPublicPermission() {

        return Lists.newArrayList(

                /**
                 * todo 方便开发 先允许全部请求通过
                 */
                //PERMISSION_GET("/**"),
                //PERMISSION_POST("/**"),
                /**
                 * =========================================
                 */
                PERMISSION_POST("/login"),
                PERMISSION_GET("/login"),
                //PERMISSION_GET("/"),
                PERMISSION_GET("/null"),
                PERMISSION_GET("/system/**"),
                PERMISSION_GET("/public/**"),

                //sockjs xhr_streaming
                PERMISSION_POST("/jet-stomp-websocket/**"),

                //HTweb-静态资源
                PERMISSION_POST("/htweb/**"),
                //HTweb-请求前缀
                PERMISSION_POST("/htwebPre/**"),

                //HUAWEI-API
                PERMISSION_POST("/api/v1/**"),

                //APP
                PERMISSION_POST("/app/v1/**"),

                //公共接口
                PERMISSION_GET("/common/getHtConfig"), //获取ht的ip和端口
                PERMISSION_GET("/energyMap/getSiteList"), //获取当前用户所用site
                PERMISSION_GET("/common/queryHistoryLeftData"), //获取能源种类
                PERMISSION_POST("/energyMonitoring/queryLeftHtImg"), //获取组态树
                PERMISSION_POST("/common/queryHistoryLeftTree"),//获取tree
                PERMISSION_GET("/common/queryParameter/*"), //获取参数
                PERMISSION_GET("/common/getRtdbTenantId"),//获取园区所对应的实时库租户标识
                PERMISSION_GET("/common/queryParkOrSite"), //获取所有的园区和site
                PERMISSION_POST("/meter/getAllCurrentSite"),//根据所选择的企业获取仪表数据
                PERMISSION_POST("/common/queryEnergyParaIdOrEnergyParaName"), //根据所选择的仪表获取参数数据
                PERMISSION_POST("/reportQuery/queryReport"), //报表下拉
                PERMISSION_POST("/meter/getCurrentSiteByEnergyTypeId"), //根据企业获取仪表数据
                PERMISSION_GET("/user/getAllUserGroup"), //获取所有的用户组
                PERMISSION_GET("/user/getAllRoles"), //获取所有角色
                PERMISSION_GET("/role/getMenuAndButtons"), //根据角色获取菜单按钮权限
                PERMISSION_GET("/common/getCurrentMenuTree"), //登陆后拉取页面菜单
                PERMISSION_GET("/energyMap/getDetail/*"), //登陆后拉取页面菜单
                PERMISSION_POST("/energyBalance/getObjectClassType"), //能源平衡获取对象类别
                PERMISSION_GET("/common/getAllCurrentSite/*"), //获取当前企业
                PERMISSION_GET("/common/getAllCurrentSite"),//获取当前企业
                PERMISSION_GET("/common/queryEnergyTypes"), //查询除标煤之外的能源种类
                PERMISSION_GET("/user/resetPwd/*"),//重置密码
                PERMISSION_GET("/userGroup/queryParkAndSite"),//获取所有用户组对应的园区和site
                PERMISSION_GET("/userGroup/queryParkAndSiteByUserGroupId/*"),//获取当前用户组对应的园区和site
                PERMISSION_GET("/userGroup/queryByUserGroupId/*"),//获取当前用户组信息

                PERMISSION_GET("/datasource/queryCondition"),//数据源 获取能源种类
                PERMISSION_GET("/park/isExistPark"),//园区
                PERMISSION_GET("/common/queryEnergyTypesAll"),//结构树 获取能源种类
                PERMISSION_POST("/tree/queryTreeInfoDetail"),//结构树 展示结构树明细表格
                //PERMISSION_POST("/energyMonitoring/queryRightHtImg"),//组态画面
                PERMISSION_POST("/common/queryMonitorHistoryInfoData"),//组态画面 历史数据弹框
                PERMISSION_POST("/comprehensiveShow/queryAllSiteImg"),//综合展示 左侧列表
                PERMISSION_POST("/menuAndFunction/add"),//综合展示画面
                PERMISSION_POST("/common/queryComprehensiveHistoryInfoData"),//综合展示画面 历史数据弹框
                PERMISSION_POST("/comprehensiveShow/querySiteImg"),//综合展示主画面
                PERMISSION_POST("/reportObjDetail/query"),//展示对象表格
                PERMISSION_GET("/datasource/queryDataSourceById/*"),//获取能源种类详情
                PERMISSION_GET("/park/queryFirstPark"),//获取企业信息
                PERMISSION_GET("/site/querySiteById/*"),//获取企业信息
                PERMISSION_GET("/benchmarkingObj/queryById/*"),  //对象指标
                PERMISSION_GET("/benchmarkingObjData/queryById/*"), //对象指标数据
                PERMISSION_GET("/alarm/queryLeftAlarmById/*"),//获取单个报警信息
                PERMISSION_POST("/user/updatePwd"), //修改密码
                PERMISSION_POST("/alarmCondition/getByIndex"), //获取单条报警条件
                PERMISSION_GET("/role/getMenuAndButtons/*"), //根据id查询角色按钮权限
                PERMISSION_GET("/equip/queryEquipSysById/*"), //根据id查询左侧设备系统列表
                PERMISSION_GET("/park/queryParkById/*") //对象管理(根据id查找园区)

        );
    }

    /**
     * 获取没有通配符的公共连接资源
     */
    public List<String> getPublicUrlWithoutWildcard() {
        List<String> res = new ArrayList<>();
        for (Permission permission : getPublicPermission()) {
            String str = permission.getUrl().replaceAll("\\*", "");
            res.add(str);
        }
        //res.add("/admin/");
        return res;
    }
}
