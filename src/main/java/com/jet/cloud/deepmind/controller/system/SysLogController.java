package com.jet.cloud.deepmind.controller.system;


import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.LogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 系统日志
 *
 * @author yhy
 * @create 2019-10-24 09:12
 */
@RestController
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private LogService logService;

    @ApiOperation(value = "查询")
    @PostMapping("/query")
    public Response queryLog(@RequestBody QueryVO vo) {
        return logService.queryLog(vo);
    }

    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam(required = false) String userId, @RequestParam Long start, @RequestParam Long end, HttpServletRequest request, HttpServletResponse response) {
        logService.exportExcel(userId, start, end, request, response);
    }

}
