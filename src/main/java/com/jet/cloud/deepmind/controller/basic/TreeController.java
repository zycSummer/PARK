package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.OrgTree;
import com.jet.cloud.deepmind.entity.OrgTreeDetail;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zhuyicheng
 * @create 2019/11/20 13:14
 * @desc 结构树controller
 */
@RequestMapping("/tree")
@RestController
public class TreeController {
    @Autowired
    private TreeService treeService;

    /**
     * @apiNote 左侧导航栏查询
     */
    @PostMapping("/queryLeftNavigation")
    public Response queryLeftNavigation(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String orgTreeName = jsonObject.getString("orgTreeName");
        return treeService.queryLeftNavigation(objType, objId, orgTreeName);
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 左侧导航栏查询(启 / 停用)
     */
    @PostMapping("/startOrOver")
    public Response startOrOver(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String orgTreeId = jsonObject.getString("orgTreeId");
        Boolean isUse = jsonObject.getBoolean("isUse");// 传true/false
        return treeService.startOrOver(objType, objId, orgTreeId, isUse).getResponse();
    }

    /**
     * @param id
     * @return
     * @apiNote 根据id查询结构树
     */
    @GetMapping("/queryLeftNavigationById/{id}")
    public Response queryLeftNavigationById(@PathVariable Integer id) {
        return treeService.queryLeftNavigationById(id);
    }

    /**
     * @param orgTree (isUse传true/false)
     * @return
     * @apiNote 新增或更新展示结构树
     */
    @PostMapping("/insertOrUpdateOrgTree")
    public Response insertOrUpdateOrgTree(@RequestBody @Valid OrgTree orgTree) {
        return treeService.insertOrUpdateOrgTree(orgTree).getResponse();
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 删除展示结构树
     */
    @PostMapping("/deleteOrgTree")
    public Response deleteOrgTree(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String orgTreeId = jsonObject.getString("orgTreeId");
        return treeService.deleteOrgTree(objType, objId, orgTreeId).getResponse();
    }

    /**
     * @apiNote 展示结构树的明细信息
     */
    @PostMapping("/queryTreeInfoDetail")
    public Response queryTreeInfoDetail(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String orgTreeId = jsonObject.getString("orgTreeId");
        return treeService.queryTreeInfoDetail(objType, objId, orgTreeId);
    }

    /**
     * @param orgTreeDetail
     * @return
     * @apiNote 新增或更新展示结构树明细信息
     */
    @PostMapping("/insertOrUpdateOrgTreeDetail")
    public Response insertOrUpdateOrgTreeDetail(@RequestBody @Valid OrgTreeDetail orgTreeDetail) {
        return treeService.insertOrUpdateOrgTreeDetail(orgTreeDetail).getResponse();
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 删除展示结构树明细信息
     */
    @PostMapping("/deleteOrgTreeDetail")
    public Response deleteOrgTreeDetail(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String orgTreeId = jsonObject.getString("orgTreeId");
        String nodeId = jsonObject.getString("nodeId");
        return treeService.deleteOrgTreeDetail(objType, objId, orgTreeId, nodeId).getResponse();
    }

    /**
     * @param objType
     * @param objId
     * @param orgTreeId
     * @param response
     * @param request
     * @apiNote 导出
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam String objType, @RequestParam String objId, @RequestParam String orgTreeId,
                            @RequestParam String energyTypeName, HttpServletResponse response, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        try {
            treeService.exportExcel(objType, objId, orgTreeId, energyTypeName, response, userAgent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 结构树excel文件导入
     *
     * @param file
     * @param objType
     * @param objId
     * @param orgTreeId
     * @return
     */
    @PostMapping("/importExcel")
    public Response importExcel(MultipartFile file, String objType, String objId, String orgTreeId) {
        return treeService.importExcel(file, objType, objId, orgTreeId).getResponse();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "OrgTreeNodeTemplate.xlsx";
            //设置要下载的文件的名称
            response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            //通知客服文件的MIME类型
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //获取文件的路径
            String filePath = getClass().getResource("/file/" + fileName).getPath();
            FileInputStream input = new FileInputStream(filePath);
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            response.setHeader("Content-Length", String.valueOf(input.getChannel().size()));
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("应用导入模板下载失败！");
        }
    }
}
