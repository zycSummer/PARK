package com.jet.cloud.deepmind.controller;

import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.DocumentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author maohandong
 * @create 2019/10/30 10:33
 * @desc 文档管理
 */
@RestController
@RequestMapping("/documentManagement")
public class DocumentManagementController {

    @Autowired
    private DocumentManagementService documentManagementService;

    /**
     * 文件的查询
     *
     * @param vo
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return documentManagementService.query(vo);
    }

    /**
     * @param file     上传的文件，参数为名为file
     * @param fileDesc 文件描述
     * @param objType  当前对象的类型
     * @param objId    当前对象的标识
     * @param memo     备注
     * @return
     */
    @PostMapping("/add")
    public Response add(@RequestParam("file") MultipartFile file, @RequestParam("fileDesc") String fileDesc,
                        @RequestParam("objType") String objType, @RequestParam("objId") String objId, @RequestParam(value = "memo", required = false) String memo) {
        return documentManagementService.add(file, fileDesc, objId, objType, memo).getResponse();
    }

    /**
     * @param id
     * @param fileDesc
     * @param memo
     * @return
     */
    @PostMapping("/edit")
    public Response edit(@RequestParam("id") Integer id, @RequestParam("fileDesc") String fileDesc,
                         @RequestParam(value = "memo", required = false) String memo) {
        return documentManagementService.edit(id, fileDesc, memo).getResponse();
    }


    /**
     * 文件的删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public Response delete(@PathVariable Integer id) {
        return documentManagementService.delete(id).getResponse();
    }

    /**
     * 文件的下载
     *
     * @param id
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/download/{id}")
    public Response download(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response){
        try {
            documentManagementService.download(id, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("文件下载失败",e);
        }
        return null;
    }
}
