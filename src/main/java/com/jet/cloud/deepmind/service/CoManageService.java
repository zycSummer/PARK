package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.Response;

/**
 * @author maohandong
 * @create 2019/10/28 9:58
 */
public interface CoManageService {
    Response getData(String objType, String objId, String time, String timeType);
}
