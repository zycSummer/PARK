package com.jet.cloud.deepmind.controller.basic;

import com.jet.cloud.deepmind.entity.Notice;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author maohandong
 * @create 2019/12/23 16:17
 * @desc 对象公告信息
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo){
        return noticeService.query(vo);
    }

    @PostMapping("/add")
    public Response add(@RequestBody Notice notice){
        return noticeService.addOrEdit(notice).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody Notice notice){
        return noticeService.addOrEdit(notice).getResponse();
    }

    @GetMapping("/delete/{id}")
    public Response delete(@PathVariable Integer id){
        return noticeService.delete(id).getResponse();
    }
}
