package com.jet.cloud.deepmind.controller.system;

import com.jet.cloud.deepmind.entity.SysMenu;
import com.jet.cloud.deepmind.entity.SysMenuFunction;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.MenuFunctionService;
import com.jet.cloud.deepmind.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 菜单和按钮
 *
 * @author yhy
 * @create 2019-11-19 15:21
 */
@RestController
@RequestMapping("/menuAndFunction")
public class MenuAndFunctionController {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuFunctionService menuFunctionService;

    @GetMapping("/getAllMenu")
    public Response getAllMenu() {
        return menuService.getAllMenu();
    }

    @GetMapping("/getFunctionsByMenuId/{menuId}")
    public Response getFunctionsByMenuId(@PathVariable String menuId) {
        return menuFunctionService.getFunctionsByMenuId(menuId);
    }

    @PostMapping("/updateMenu")
    public Response updateMenu(@RequestBody SysMenu menu) {
        return menuService.updateMenu(menu).getResponse();
    }

    @PostMapping("/updateFunction")
    public Response updateFunction(@RequestBody SysMenuFunction function) {
        return menuFunctionService.updateFunction(function).getResponse();
    }

    @PostMapping("/add")
    public Response addFunction(@RequestBody SysMenuFunction function) {
        return menuFunctionService.addFunction(function).getResponse();
    }
}
