package com.jet.cloud.deepmind.service.application;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.SysParameter;
import com.jet.cloud.deepmind.repository.SysParameterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuyicheng
 * @create 2019/10/23 15:24
 * @desc
 */
@Component("sysParameter")
public class SysParameterBean {
    private String platformName;

    private Map<String, String> parameters;

    @Autowired
    private SysParameterRepo sysParameterRepo;

    @Autowired
    private CurrentUser currentUser;

    public String platformName() {
        return this.platformName;
    }

    public Map sysParameter() {
        return this.parameters;
    }

    public String parameterByKey(String key) {
        return this.parameters.get(key);
    }

    @PostConstruct
    public void initParameter() {
        platformName = sysParameterRepo.findByParaId("PlatformName").getParaId(); // 从数据库中取
        parameters = new HashMap(); //从数据库取
        List<SysParameter> parameterList = sysParameterRepo.findAll();
        for (SysParameter parameter : parameterList) {
            parameters.put(parameter.getParaId(), parameter.getParaValue());
        }
    }

    //系统参数全部重新从数据库加载
    public boolean reload() {
        initParameter();
        return true;
    }

    //从数据库更新一个给定的参数值
    public boolean updateParameter(String paraId) {
        String value = sysParameterRepo.findParaValue(paraId);
        if (value == null) return false;
        parameters.put(paraId, value);
        return true;
    }

    public Object currentLangPlateTitle(String key) {
        String arrStr = this.parameters.get(key);
        if (StringUtils.isEmpty(arrStr)) {
            return "";
        }
        return arrStr;
    }
}
