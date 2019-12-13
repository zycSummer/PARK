package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.entity.AlarmMsg;
import com.jet.cloud.deepmind.entity.QAlarmMsg;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.AlarmMsgRepo;
import com.jet.cloud.deepmind.service.AlarmMsgService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author yhy
 * @create 2019-11-14 10:33
 */
@Service
public class AlarmMsgServiceImpl implements AlarmMsgService {


    @Autowired
    private AlarmMsgRepo alarmMsgRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response query(QueryVO vo) {
        QAlarmMsg obj = QAlarmMsg.alarmMsg;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        String objId = key.getString("objId");
        String objType = key.getString("objType");
        List<Boolean> ackStatusList = key.getJSONArray("ackStatusList").toJavaList(Boolean.class);
        LocalDateTime start = DateUtil.longToLocalTime(key.getLongValue("start"));
        LocalDateTime end = DateUtil.longToLocalTime(key.getLongValue("end"));
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        pre = ExpressionUtils.and(pre, obj.alarmTime.between(start, end));
        String alarmName = key.getString("alarmName");
        if (isNotNullAndEmpty(alarmName)) {
            pre = ExpressionUtils.and(pre, obj.alarmName.containsIgnoreCase(alarmName));
        }
        if (isNotNullAndEmpty(ackStatusList)) {
            pre = ExpressionUtils.and(pre, obj.isAck.in(ackStatusList));
        }
        Page<AlarmMsg> alarms = alarmMsgRepo.findAll(pre, vo.Pageable(Sort.by(Sort.Direction.DESC, "alarmTime")));
        Response ok = Response.ok(alarms.getContent(), alarms.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public ServiceData ack(List<Integer> ids) {
        try {
            List<AlarmMsg> list = alarmMsgRepo.findAllById(ids);
            List<AlarmMsg> res = new ArrayList<>();
            StringJoiner j = new StringJoiner(",");
            for (AlarmMsg msg : list) {
                if (msg.getIsAck() == null || msg.getIsAck()) continue;
                msg.setIsAck(true);
                msg.setUpdateNow();
                msg.setUpdateUserId(currentUser.userId());
                res.add(msg);
                j.add(msg.toAlarmMsgString());
            }
            alarmMsgRepo.saveAll(res);
            return ServiceData.success("确认报警信息", j.toString(), currentUser);
        } catch (Exception e) {
            return ServiceData.error("确认报警信息", e, currentUser);
        }

    }

    @Override
    public ServiceData updateMemo(List<Integer> ids, String memo) {
        try {
            List<AlarmMsg> list = alarmMsgRepo.findAllById(ids);
            StringJoiner j = new StringJoiner(",");
            for (AlarmMsg msg : list) {
                msg.setUpdateNow();
                msg.setUpdateUserId(currentUser.userId());
                msg.setMemo(memo);
                j.add(msg.toAlarmMsgString());
            }
            alarmMsgRepo.saveAll(list);
            return ServiceData.success("添加报警备注", j.toString(), currentUser);
        } catch (Exception e) {
            return ServiceData.error("添加报警备注", e, currentUser);
        }
    }
}
