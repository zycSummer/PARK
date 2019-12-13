package com.jet.cloud.deepmind.config.security;

import com.jet.cloud.deepmind.config.security.auth.AccessSecurityInterceptor;
import com.jet.cloud.deepmind.config.security.auth.SelfAuthenticationProvider;
import com.jet.cloud.deepmind.config.security.handler.AjaxAccessDeniedHandler;
import com.jet.cloud.deepmind.config.security.handler.AjaxAuthenticationEntryPoint;
import com.jet.cloud.deepmind.config.security.handler.AjaxAuthenticationFailureHandler;
import com.jet.cloud.deepmind.config.security.handler.AjaxAuthenticationSuccessHandler;
import com.jet.cloud.deepmind.config.security.handler.AjaxLogoutHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author yhy
 * @create 2019-10-09 11:35
 */
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    @Autowired
    AjaxAuthenticationEntryPoint authenticationEntryPoint;  //  未登陆时返回 JSON 格式的数据给前端（否则为 html）

    @Autowired
    AjaxAuthenticationSuccessHandler authenticationSuccessHandler;  // 登录成功返回的 JSON 格式数据给前端（否则为 html）

    @Autowired
    AjaxAuthenticationFailureHandler authenticationFailureHandler;  //  登录失败返回的 JSON 格式数据给前端（否则为 html）

    @Autowired
    AjaxLogoutHandler logoutSuccessHandler;  // 注销成功返回的 JSON 格式数据给前端（否则为 登录时的 html）

    @Autowired
    AjaxAccessDeniedHandler accessDeniedHandler;    // 无权访问返回的 JSON 格式数据给前端（否则为 403 html 页面）

    @Autowired
    SelfAuthenticationProvider provider; // 自定义安全认证

    //@Autowired
    //UserGroupAuthenticationDetailsSource userGroupAuthenticationDetailsSource; // 自定义安全认证

    @Autowired
    AccessSecurityInterceptor accessSecurityInterceptor;


    /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");  //首页
        registry.addViewController("/admin/dashboard").setViewName("admin/main/main"); //首页
        registry.addViewController("/admin/map").setViewName("admin/energyMap/energyMap"); //能耗地图
        registry.addViewController("/admin/complex").setViewName("admin/complexDisplay/complexDisplay"); //综合展示
        registry.addViewController("/admin/balance").setViewName("admin/energyBalance/energyBalance"); //能源平衡
        registry.addViewController("/admin/monitor").setViewName("admin/energyMonitor/energyMonitor"); //用能监测 实时监测
        registry.addViewController("/admin/history").setViewName("admin/energyMonitor/historyData"); //用能监测 历史数据
        registry.addViewController("/admin/analogy").setViewName("admin/energyAnalysis/energyAnalogy"); //能耗分析 能耗类比
        registry.addViewController("/admin/ratio").setViewName("admin/energyAnalysis/energyRatio"); //能耗分析 能耗时比
        registry.addViewController("/admin/burden").setViewName("admin/energyProjects/burden");  //项目能耗  实时负荷
        registry.addViewController("/admin/alarmConfiguration").setViewName("admin/alarm/configuration");//报警管理  报警配置
        registry.addViewController("/admin/alarmQuery").setViewName("admin/alarm/query");//报警管理  报警查询
        registry.addViewController("/admin/energyCalendar").setViewName("admin/energyProjects/energyCalendar");//项目能耗 能耗日历
        registry.addViewController("/admin/loadRanking").setViewName("admin/energyProjects/loadRanking"); //项目能耗 负荷排名
        registry.addViewController("/admin/usageInformation").setViewName("admin/energyProjects/usageInformation"); //用量信息
        registry.addViewController("/admin/loadRanking").setViewName("admin/energyProjects/loadRanking"); //项目能耗 负荷排名
        registry.addViewController("/admin/usageInformation").setViewName("admin/energyProjects/usageInformation"); //用量信息
        registry.addViewController("/admin/carbonManagement").setViewName("admin/carbonManagement/carbonManagement"); //碳排管理
        registry.addViewController("/admin/fileManagement").setViewName("admin/fileManagement/fileManagement");//文档管理
        registry.addViewController("/admin/device").setViewName("admin/device/device");  //设备管理
        registry.addViewController("/admin/port").setViewName("admin/port/port");  //管理员端口管理
        registry.addViewController("/admin/port/monitor").setViewName("admin/port/monitor");  //管理员端口监听界面
        registry.addViewController("/admin/role").setViewName("admin/system/role");  //系统管理 角色管理
        registry.addViewController("/admin/user").setViewName("admin/system/user");  //系统管理 用户管理
        registry.addViewController("/admin/userGroup").setViewName("admin/system/userGroup"); //系统管理  用户组管理
        registry.addViewController("/admin/log").setViewName("admin/system/log"); // 系统管理 日志管理
        registry.addViewController("/admin/menu").setViewName("admin/system/menu"); // 系统管理  菜单管理
        registry.addViewController("/admin/complexConf").setViewName("admin/basicData/complexConf"); // 基础数据 对象综合展示画面配置
        registry.addViewController("/admin/monitorConf").setViewName("admin/basicData/monitorConf"); // 基础数据 对象组态画面配置
        registry.addViewController("/admin/objectPark").setViewName("admin/basicData/objectManagementPark");//基础数据  对象管理园区
        registry.addViewController("/admin/objectFirm").setViewName("admin/basicData/objectManagementCom");//基础数据  对象管理企业
        registry.addViewController("/admin/objectMeterConf").setViewName("admin/basicData/objectMeterConf");//基础数据  对象仪表配置
        registry.addViewController("/admin/objectDataSource").setViewName("admin/basicData/objectDataSource");//基础数据  对象数据源配置
        registry.addViewController("/admin/reportManagement").setViewName("admin/statisticsReport/reportManagement"); // 统计报表 报表管理
        registry.addViewController("/admin/reportQuery").setViewName("admin/statisticsReport/reportQuery"); // 统计报表 报表查询
        registry.addViewController("/admin/displayTreeConf").setViewName("admin/basicData/displayTreeConf"); // 基础数据 对象展示结构树配置
        registry.addViewController("/admin/energyPlan").setViewName("admin/basicData/energyPlan"); // 基础数据 对象用能计划管理
        registry.addViewController("/admin/GDPManagement").setViewName("admin/basicData/GDPManagement"); // 基础数据 对象GDP管理

        registry.addViewController("/login").setViewName("login/login");
//        registry.addViewController("/api/list").setViewName("/api/apiList");  //api 用户
//        registry.addViewController("/api/association").setViewName("/apiAssociation/association"); //api用户关联
        registry.addViewController("/404").setViewName("/error");

    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.authenticationProvider(provider);
        auth.eraseCredentials(false);           // 不删除凭据，以便记住用户
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
        http.addFilterAt(accessSecurityInterceptor, FilterSecurityInterceptor.class);
        http.authorizeRequests().antMatchers("/public/**"
                //HUAWEI-API
                , "/api/v1/**").permitAll();
        http.httpBasic().authenticationEntryPoint(authenticationEntryPoint)

                .and()
                .authorizeRequests()

                .anyRequest()
                .authenticated()// 其他 url 需要身份认证
                .and()
                .formLogin().loginPage("/login") //开启登录
                .successHandler(authenticationSuccessHandler) // 登录成功
                .failureHandler(authenticationFailureHandler)
                //.defaultSuccessUrl("/") // 登录失败
                //.authenticationDetailsSource(userGroupAuthenticationDetailsSource)
                .permitAll()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login")
                .addLogoutHandler(logoutSuccessHandler)
                //.deleteCookies("JSESSIONID")
                .permitAll();
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler); // 无权访问 JSON 格式的数据
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/web/images/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/fusiongis/**").addResourceLocations("file:fusiongis/");
        registry.addResourceHandler("/admin/**").addResourceLocations("file:client/")
                .addResourceLocations("file:instance/")
                .addResourceLocations("file:instance/storage/")
                .addResourceLocations("file:instance/custom/previews/");
    }
}
