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

    @Autowired
    AccessSecurityInterceptor accessSecurityInterceptor; // 资源访问过滤器

    /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");  //首页
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
                , "/api/v1/**"
                //APP
                ,"/app/v1/**").permitAll();
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

        //以下这句就可以控制单个用户只能创建一个session，也就只能在服务器登录一次
        //http.sessionManagement().maximumSessions(1).expiredUrl("/login");
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
