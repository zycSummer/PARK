package com.jet.cloud.deepmind.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.jet.cloud.deepmind.common.CurrentUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.util.AntPathMatcher;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author yhy
 * @create 2019-10-14 13:26
 */

@Entity
@Getter
@Setter
@Table(name = "tb_sys_log")
public class SysLog {
    private static final long serialVersionUID = -2175641554516561573L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "identity")
    @GenericGenerator(name = "identity", strategy = "identity")
    @Column(name = "seq_id")
    protected Integer id;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private SysUser sysUser;

    @Column(name = "user_id")
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "operate_time")
    private LocalDateTime operateTime;


    @Column(name = "operate_ip")
    private String operateIp;
    /**
     * 操作菜单（[菜单标识]菜单名称）
     */
    private String menu;
    /**
     * 操作菜单功能（[菜单功能标识]菜单功能名称）
     */
    @Column(name = "`function`")
    private String function;

    /**
     * 1：post
     * 2:get
     * 3:delete
     * 4:put
     */
    @Column(name = "method")
    private String method;
    private String url;

    @Column(name = "operate_content")
    private String operateContent;

    /**
     * enum 'SUCCESS','FAIL'
     */
    @Column(name = "operate_result")
    private String result;
    @Column(name = "memo")
    private String memo;

    public SysLog() {

    }

    public SysLog(String operateContent, boolean result) {
        this.operateContent = operateContent;
        this.result = result ? "SUCCESS" : "FAIL";
    }

    public SysLog(String operateContent, Boolean result, String memo) {
        this.operateContent = operateContent;
        this.result = result ? "SUCCESS" : "FAIL";
        this.memo = memo;
    }

    public SysLog(String operateContent, CurrentUser user, boolean result) {
        this.operateContent = operateContent;
        this.result = result ? "SUCCESS" : "FAIL";
        String remoteURI = user.getRemoteURI();
        String operateObject = null;
        for (Map.Entry<String, SysMenuFunction> entry : user.buttonList().entrySet()) {
            if (Objects.equal(entry.getKey(), remoteURI)) {
                operateObject = entry.getValue().getFunctionName();
                this.function = "[" + entry.getValue().getFunctionId() + "]" + entry.getValue().getFunctionName();
                this.menu = "[" + entry.getValue().getMenuId() + "]" + entry.getValue().getSysMenu().getMenuName();
                break;
            }
        }
        for (Map.Entry<String, SysMenu> entry : user.menuList().entrySet()) {
            if (operateObject == null) {
                if (Objects.equal(entry.getKey(), remoteURI)) {
                    this.menu = "[" + entry.getValue().getMenuId() + "]" + entry.getValue().getMenuName();
                    break;
                }
            } else {
                if (remoteURI.contains(entry.getKey())) {
                    operateContent = entry.getValue().getMenuName() + "-" + operateObject;
                    break;
                }
            }
        }
        this.operateContent = operateContent;
    }

    public SysLog(AntPathMatcher antPathMatcher, String operateContent, CurrentUser user, boolean result) {
        this.operateContent = operateContent;
        this.result = result ? "SUCCESS" : "FAIL";
        String remoteURI = user.getRemoteURI();
        String operateObject = null;
        for (Map.Entry<String, SysMenuFunction> entry : user.buttonList().entrySet()) {
            if (antPathMatcher.match(entry.getKey(), remoteURI)) {
                operateObject = entry.getValue().getFunctionName();
                this.function = "[" + entry.getValue().getFunctionId() + "]" + entry.getValue().getFunctionName();
                this.menu = "[" + entry.getValue().getMenuId() + "]" + entry.getValue().getSysMenu().getMenuName();
                break;
            }
        }
        for (Map.Entry<String, SysMenu> entry : user.menuList().entrySet()) {
            if (operateObject == null) {
                if (antPathMatcher.match(entry.getKey(), remoteURI)) {
                    this.menu = "[" + entry.getValue().getMenuId() + "]" + entry.getValue().getMenuName();
                    break;
                }
            } else {
                if (remoteURI.contains(entry.getKey())) {
                    operateContent = entry.getValue().getMenuName() + "-" + operateObject;
                    break;
                }
            }
        }
        this.operateContent = operateContent;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
