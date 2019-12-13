package com.jet.cloud.deepmind.controller.view;

import com.jet.cloud.deepmind.annotation.PrivilegeCheck;
import com.jet.cloud.deepmind.common.Constants;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * 路由
 *
 * @author yhy
 * @create 2019-11-20 10:19
 */
@Controller
@PrivilegeCheck
public class ViewController {

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView o = new ModelAndView();
        o.setViewName("index");
        return o;
    }

    /**
     * 对标管理 指标排名
     */
    @GetMapping("/admin/indexRanking")
    public ModelAndView indexRanking() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/benchmarking/indexRanking");
        return o;
    }

    /**
     * 对标管理 指标数据
     */
    @GetMapping("/admin/indexData")
    public ModelAndView indexData() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/benchmarking/indexData");
        return o;
    }

    /**
     * 能耗地图
     */
    @GetMapping("/admin/map")
    public ModelAndView energyMap() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyMap/energyMap");
        return o;
    }

      /**
     * 综合展示
     */
    @GetMapping("/admin/complex")
    public ModelAndView complexAdmin() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/complexDisplay/complexDisplay");
        return o;
    }

    /**
     * 能源平衡
     */
    @GetMapping("/admin/balance")
    public ModelAndView energyBalance() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyBalance/energyBalance");
        return o;
    }


    /**
     * 用能监测 实时监测
     */
    @GetMapping("/admin/monitor")
    public ModelAndView energyMonitor() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyMonitor/energyMonitor");
        return o;
    }

    /**
     * 用能监测 历史数据
     */
    @GetMapping("/admin/history")
    public ModelAndView historyData() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyMonitor/historyData");
        return o;
    }

    /**
     * 能耗分析 能耗类比
     */
    @GetMapping("/admin/analogy")
    public ModelAndView energyAnalogy() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyAnalysis/energyAnalogy");
        return o;
    }

    /**
     * 能耗分析 能耗时比
     */
    @GetMapping("/admin/ratio")
    public ModelAndView energyRatio() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyAnalysis/energyRatio");
        return o;
    }

    /**
     * 项目能耗  实时负荷
     */
    @GetMapping("/admin/burden")
    public ModelAndView burden() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyProjects/burden");
        return o;
    }

    /**
     * 报警管理  报警配置
     */
    @GetMapping("/admin/alarmConfiguration")
    public ModelAndView workorderDeal() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/alarm/configuration");
        return o;
    }

    /**
     * 报警管理  报警查询
     */
    @GetMapping("/admin/alarmQuery")
    public ModelAndView alarmQuery() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/alarm/query");
        return o;
    }

    /**
     * 项目能耗 能耗日历
     */
    @GetMapping("/admin/energyCalendar")
    public ModelAndView energyCalendar() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyProjects/energyCalendar");
        return o;
    }

    /**
     * 项目能耗 负荷排名
     */
    @GetMapping("/admin/loadRanking")
    public ModelAndView loadRanking() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyProjects/loadRanking");
        return o;
    }

    /**
     * 用量信息
     */
    @GetMapping("/admin/usageInformation")
    public ModelAndView usageInformation() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/energyProjects/usageInformation");
        return o;
    }

    /**
     * 碳排管理
     */
    @GetMapping("/admin/carbonManagement")
    public ModelAndView carbonManagement() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/carbonManagement/carbonManagement");
        return o;
    }

    /**
     * 文档管理
     */
    @GetMapping("/admin/fileManagement")
    public ModelAndView screenDisplay() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/fileManagement/fileManagement");
        return o;
    }


    /**
     * 设备管理
     */
    @GetMapping("/admin/device")
    public ModelAndView device() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/device/device");
        return o;
    }

    /**
     * 系统管理 角色管理
     */
    @GetMapping("/admin/role")
    public ModelAndView role() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/role");
        return o;
    }

    /**
     * 系统管理 用户管理
     */
    @GetMapping("/admin/user")
    public ModelAndView user() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/user");
        return o;
    }

    /**
     * 系统管理  用户组管理
     */
    @GetMapping("/admin/userGroup")
    public ModelAndView energyConsume() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/userGroup");
        return o;
    }
           /**
     * 系统管理  能源种类配置
     */
    @GetMapping("/admin/energyType")
    public ModelAndView energyType() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/energyType");
        return o;
    }
    
     /**
     * 系统管理  能耗强度等级配置
     */
    @GetMapping("/admin/energyGrade")
    public ModelAndView energyGrade() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/energyGrade");
        return o;
    }
       /**
     * 系统管理  系统参数管理
     */
    @GetMapping("/admin/sysPara")
    public ModelAndView sysPara() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/sysPara");
        return o;
    }
    /**
     * 系统管理 日志管理
     */
    @GetMapping("/admin/sysLog")
    public ModelAndView log() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/log");
        return o;
    }

    /**
     * 系统管理  菜单管理
     */
    @GetMapping("/admin/menu")
    public ModelAndView menu() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/system/menu");
        return o;
    }

    /**
     * 基础数据 对象综合展示画面配置
     */
    @GetMapping("/admin/complexConf")
    public ModelAndView complexConf() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/complexConf");
        return o;
    }

    /**
     * 基础数据 对象组态画面配置
     */
    @GetMapping("/admin/monitorConf")
    public ModelAndView monitorConf() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/monitorConf");
        return o;
    }

    /**
     * 基础数据  对象管理园区
     */
    @GetMapping("/admin/park")
    public ModelAndView objectManagementPark() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/objectManagementPark");
        return o;
    }

    /**
     * 基础数据  对象管理企业
     */
    @GetMapping("/admin/site")
    public ModelAndView objectManagementCom() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/objectManagementCom");
        return o;
    }

    /**
     * 基础数据  对象仪表配置
     */
    @GetMapping("/admin/meter")
    public ModelAndView objectMeterConf() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/objectMeterConf");
        return o;
    }

    /**
     * 基础数据  对象数据源配置
     */
    @GetMapping("/admin/dataSource")
    public ModelAndView objectDataSource() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/objectDataSource");
        return o;
    }

    /**
     * 统计报表 报表管理
     */
    @GetMapping("/admin/reportManagement")
    public ModelAndView reportManagement() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/statisticsReport/reportManagement");
        return o;
    }

    /**
     * 统计报表 报表查询
     */
    @GetMapping("/admin/reportQuery")
    public ModelAndView reportQuery() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/statisticsReport/reportQuery");
        return o;
    }

    /**
     * 基础数据 对象展示结构树配置
     */
    @GetMapping("/admin/displayTreeConf")
    public ModelAndView displayTreeConf() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/displayTreeConf");
        return o;
    }

    /**
     * 基础数据 对象用能计划管理
     */
    @GetMapping("/admin/energyPlan")
    public ModelAndView energyPlanView() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/energyPlan");
        return o;
    }

    /**
     * 基础数据 对象GDP管理
     */
    @GetMapping("/admin/GDPManagement")
    public ModelAndView GDPManagementView() {
        ModelAndView o = new ModelAndView();
        o.setViewName("admin/basicData/GDPManagement");
        return o;
    }

    @GetMapping("/login")
    public ModelAndView loginAgain(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Constants.SESSION_USER_ID) != null) {
            return new ModelAndView("401");
        } else {
            return new ModelAndView("login/login");
        }
    }

    @GetMapping("/404")
    public ModelAndView configurationEditor() {
        ModelAndView o = new ModelAndView();
        o.setViewName("/error");
        return o;
    }

}
