package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.entity.SysParameter;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.SysParameterRepo;
import com.jet.cloud.deepmind.service.SysParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统参数
 *
 * @author yhy
 * @create 2019-11-22 11:28
 */
@RestController
@RequestMapping("/sysParameter")
public class SysParameterController {

    @Autowired
    private SysParameterService sysParameterService;

    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String sysParaId = data.getString("sysParaId");
        return sysParameterService.query(sysParaId);
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody SysParameter sysParameter) {
        return sysParameterService.edit(sysParameter).getResponse();
    }

}
