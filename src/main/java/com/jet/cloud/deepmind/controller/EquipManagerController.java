package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.Equip;
import com.jet.cloud.deepmind.entity.EquipSys;
import com.jet.cloud.deepmind.model.EquipVO;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.EquipManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:37
 * @desc 设备管理Controller
 */
@RestController
@RequestMapping("/equip")
public class EquipManagerController {
    @Autowired
    private EquipManagerService equipManagerService;

    /**
     * @param jsonObject
     * @return
     * @apiNote 查询左侧设备系统列表
     */
    @PostMapping("/queryEquipSys")
    public Response queryEquipSys(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        return equipManagerService.queryEquipSys(objType, objId);
    }

    /**
     * @param id
     * @return
     * @apiNote 根据id查询左侧设备系统列表
     */
    @GetMapping("/queryEquipSysById/{id}")
    public Response queryEquipSysById(@PathVariable Integer id) {
        return equipManagerService.queryEquipSysById(id);
    }

    /**
     * @param equipSys
     * @return
     * @apiNote 新增设备系统
     */
    @PostMapping("/insertEquipSys")
    public Response insertEquipSys(@RequestBody @Valid EquipSys equipSys) {
        return equipManagerService.insertOrUpdateEquipSys(equipSys).getResponse();
    }

    /**
     * @param equipSys
     * @return
     * @apiNote 更新设备系统
     */
    @PostMapping("/updateEquipSys")
    public Response updateEquipSys(@RequestBody @Valid EquipSys equipSys) {
        return equipManagerService.insertOrUpdateEquipSys(equipSys).getResponse();
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 删除设备系统
     */
    @PostMapping("/deleteEquipSys")
    public Response deleteEquipSys(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String equipSysId = jsonObject.getString("equipSysId");
        return equipManagerService.deleteEquipSys(objType, objId, equipSysId).getResponse();
    }

    /**
     * @param vo
     * @return
     * @apiNote 查询右侧设备列表
     */
    @PostMapping("/queryEquip")
    public Response queryEquip(@RequestBody QueryVO vo) {
        return equipManagerService.queryEquip(vo);
    }

    /**
     * @return
     * @apiNote 查看图片
     */
    @PostMapping("/queryImage")
    public Response queryImage(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String equipId = jsonObject.getString("equipId");
        return equipManagerService.queryImage(objType, objId, equipId);
    }

    /**
     * @return
     * @apiNote 新增设备列表
     */
    @PostMapping("/addEquip")
    public Response addEquip(MultipartFile file, @Valid Equip equip) {
        return equipManagerService.addOrEditEquip(file, equip).getResponse();
    }

    /**
     * @return
     * @apiNote 更新设备列表
     */
    @PostMapping("/editEquip")
    public Response editEquip(MultipartFile file, @Valid Equip equip) {
        return equipManagerService.addOrEditEquip(file, equip).getResponse();
    }

    /**
     * @param equipVOS
     * @return
     * @apiNote 删除多设备
     */
    @PostMapping("/deleteEquip")
    public Response deleteEquip(@RequestBody List<EquipVO> equipVOS) {
        return equipManagerService.deleteEquip(equipVOS).getResponse();
    }

    /**
     * @param objType
     * @param objId
     * @param equipSysId
     * @param equipId
     * @param equipName
     * @param response
     * @param request
     * @apiNote导出设备
     */
    @GetMapping("/exportExcel")
    public void exportExcel(String objType, String objId, String equipSysId, String equipId, String equipName, HttpServletResponse response, HttpServletRequest request) {
        equipManagerService.exportExcel(objType, objId, equipSysId, equipId, equipName, response, request);
    }

    /**
     * 导入设备信息
     *
     * @param file
     * @param objType
     * @param objId
     * @return
     */
    @PostMapping("/importExcel")
    public Response importExcel(MultipartFile file, String objType, String objId) {
        return equipManagerService.importExcel(file, objType, objId).getResponse();
    }

    /**
     * 文件下载
     *
     * @param response
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        equipManagerService.download(response);
    }
}
