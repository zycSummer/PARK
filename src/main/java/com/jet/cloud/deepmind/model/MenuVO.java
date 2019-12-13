package com.jet.cloud.deepmind.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.SysMenu;
import com.jet.cloud.deepmind.entity.SysMenuFunction;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 菜单管理vo
 *
 * @author yhy
 * @create 2019-10-18 14:32
 */
@Data
public class MenuVO implements Serializable {

    private static final long serialVersionUID = -5439896090880035779L;
    private String parentId;

    private String menuId;
    private String functionId;


    private String title;

    private String href;

    /**
     * function,menu
     */
    private String type;

    private String icon;

    private boolean checked;

    private List<MenuVO> children;

    private String sortId;

    public MenuVO(SysMenu menu) {
        this.menuId = menu.getMenuId();
        this.title = menu.getMenuName();
        this.href = menu.getUrl();
        this.type = "menu";
        this.icon = menu.getIcon();
        this.sortId = menu.getSortId();
    }

    public MenuVO(SysMenuFunction function) {
        this.menuId = function.getMenuId();
        this.functionId = function.getFunctionId();
        this.title = function.getFunctionName();
    }

    @JsonIgnore
    public boolean isButton() {
        return Objects.equals(this.type, "function");
    }

    public MenuVO(SysMenu menu, Set<String> checkedList) {
        this.parentId = menu.getParentId();
        this.menuId = menu.getMenuId();
        this.title = menu.getMenuName();
        this.href = menu.getUrl();
        this.type = "menu";
        this.icon = menu.getIcon();
        this.checked = getChecked(menu.getMenuId(), null, checkedList);
    }

    public MenuVO(SysMenuFunction function, Set<String> checkedList) {
        this.menuId = function.getMenuId();
        this.functionId = function.getFunctionId();
        this.title = function.getFunctionName();
        this.href = function.getUrl();
        this.type = "function";
        this.checked = getChecked(function.getMenuId(), function.getFunctionId(), checkedList);
    }

    public boolean getChecked(String menuId, String functionId, Set<String> checkedList) {
        final String tag = "@@";
        if (StringUtils.isNotNullAndEmpty(functionId)) {
            return checkedList.contains(menuId + tag + functionId);
        }
        return checkedList.contains(menuId);
    }
}
