package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.SysParameter;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.SysParameterRepo;
import com.jet.cloud.deepmind.rtdb.ClientConfig;
import com.jet.cloud.deepmind.service.SysParameterService;
import com.jet.cloud.deepmind.service.application.SysParameterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yhy
 * @create 2019-11-22 11:36
 */
@Service
public class SysParameterServiceImpl implements SysParameterService {


    @Autowired
    private SysParameterRepo sysParameterRepo;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private SysParameterBean sysParameterBean;

    @Autowired
    private ClientConfig clientConfig;

    @Override
    public Response query(String sysParaId) {
        List<SysParameter> list;
        if (StringUtils.isNullOrEmpty(sysParaId)) {
            list = sysParameterRepo.findAll();
        } else {
            list = sysParameterRepo.findAllByParaId(sysParaId);
        }
        Response ok = Response.ok(list);
        ok.setQueryPara(sysParaId);
        return ok;
    }

    @Override
    public ServiceData edit(SysParameter sysParameter) {

        String content = "修改系统参数";

        SysParameter old = sysParameterRepo.findByParaId(sysParameter.getParaId());
        if (old == null) {
            return ServiceData.error(content + ",系统参数标识不存在", currentUser);
        }
        old.setParaValue(sysParameter.getParaValue());
        old.setParaDesc(sysParameter.getParaDesc());
        old.setMemo(sysParameter.getMemo());
        old.setUpdateNow();
        old.setUpdateUserId(currentUser.userId());
        sysParameterRepo.save(old);
        //刷新缓存
        sysParameterBean.initParameter();
        clientConfig.getEnergy();
        return ServiceData.success(content, currentUser);
    }
}
