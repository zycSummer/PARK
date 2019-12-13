package com.jet.cloud.deepmind.controller.basic;

import com.jet.cloud.deepmind.entity.Site;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/20 13:23
 * @desc 基础数据(企业)
 */
@RestController
@RequestMapping("/site")
public class SiteController {
    @Autowired
    private SiteService siteService;

    /**
     * 查询企业
     *
     * @param vo
     * @return 查询条件
     * 企业标识
     * 企业名称
     * 是否上线：可选项为 空、 是-Y、否-N，默认为空，单选
     */
    @PostMapping("/querySite")
    public Response querySite(@RequestBody QueryVO vo) {
        return siteService.querySite(vo);
    }


    /**
     * @return
     * @apiNote 对象管理(根据id查找企业)
     */
    @GetMapping("/querySiteById/{id}")
    public Response querySiteById(@PathVariable Integer id) {
        return siteService.querySiteById(id);
    }

    /**
     * @return
     * @apiNote 对象管理(新增或者修改企业)
     */
    @PostMapping("/add")
    public Response add(MultipartFile file, @Valid Site site) {
        return siteService.addOrEditSite(file, site).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(MultipartFile file, @Valid Site site) {
        return siteService.addOrEditSite(file, site).getResponse();
    }

    /**
     * @return
     * @apiNote 对象管理(根据企业标识删除园区)
     */
    @GetMapping("/delete/{siteId}")
    public Response delete(@PathVariable String siteId) {
        return siteService.delete(siteId).getResponse();
    }


}
