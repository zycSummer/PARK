package com.jet.cloud.deepmind.rtdb;

import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yhy
 * @create 2019-10-31 13:59
 */
@PropertySource("classpath:jet-rtdb.properties")
@Configuration
public class ClientConfig {

    @Value("${server.host}")
    private String host;

    @Value("${server.url.prefix}")
    private String urlPrefix;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
        return new RestTemplate(requestFactory);
    }

    public String getHost() {
        return host;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    //测点最新值
    public String getQueryLastSampleDataUrl() {
        return urlPrefix() + "/queryLastSampleData";
    }

    //测点历史值
    public String getQueryHisDataUrl() {
        return urlPrefix() + "/queryHisData";
    }

    public String urlPrefix() {
        return getHost() + getUrlPrefix();
    }

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    /**
     * 对tb_sys_energy_type进行CRUD记得更新缓存
     */
    @Bean(name = "getEnergy")
    public Map<String, Double> getEnergy() {
        List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findAll();
        Map<String, Double> mapEnergy = new HashMap<>();
        if (!sysEnergyTypes.isEmpty()) {
            for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                mapEnergy.put(sysEnergyType.getEnergyTypeId(), sysEnergyType.getStdCoalCoeff());
            }
        }
        return mapEnergy;
    }

    public String getQueryMaxDataUrl() {
        return urlPrefix() + "/queryMaxData";
    }

    public String getQueryMinDataUrl() {
        return urlPrefix() + "/queryMinData";
    }

    public String getQueryAvgDataUrl() {
        return urlPrefix() + "/queryAvgData";
    }

    public String getQueryDiffDataUrl() {
        return urlPrefix() + "/queryDiffData";
    }
}
