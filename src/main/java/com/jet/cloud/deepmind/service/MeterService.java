package com.jet.cloud.deepmind.service;

import com.alibaba.fastjson.JSONArray;
import com.jet.cloud.deepmind.entity.Meter;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;


/**
 * Class MeterService
 *
 * @package
 */
public interface MeterService {

    /**
     * 获取当前企业配置的仪表
     */
    Response getAllMeterBySiteResp(String objType, String objId, String key);

    /**
     * 根据当前企业和能源种类获取配置的仪表
     */
    Response getCurrentSiteByEnergyTypeId(String objType, String objId, String energyTypeId, String key);


    Response queryMeter(QueryVO vo);

    @Transactional
    ServiceData addOrEditMeter(Meter meter);

    @Transactional
    ServiceData delete(List<Integer> idList);

    ServiceData importExcel(MultipartFile file, String objType, String objId);

    Response queryMeterById(Integer id);

    void exportExcel(String objType, String objId, String meterId, String meterName, JSONArray energyTypeId, HttpServletResponse response, HttpServletRequest request);
}
