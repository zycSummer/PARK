package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import java.util.List;

/**
 * Class AlarmMsgService
 *
 * @package
 */
public interface AlarmMsgService {
    Response query(QueryVO vo);

    ServiceData ack(List<Integer> ids);

    ServiceData updateMemo(List<Integer> ids, String memo);
}
