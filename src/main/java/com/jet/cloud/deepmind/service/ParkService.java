package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.Park;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

/**
 * @author zhuyicheng
 * @create 2019/11/15 14:54
 * @desc 基础数据service(园区)
 */
public interface ParkService {
    Response queryPark(String parkId, String parkName);

    Response isExistPark();

    @Transactional
    ServiceData insertOrUpdatePark(Park park, MultipartFile file);

    Response queryParkById(Integer id);

    @Transactional
    ServiceData delete(String parkId);

    Response queryFirstPark();

}
