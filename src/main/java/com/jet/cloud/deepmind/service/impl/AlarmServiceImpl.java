package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.entity.QAlarm;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.AlarmConditionRepo;
import com.jet.cloud.deepmind.repository.AlarmRepo;
import com.jet.cloud.deepmind.service.AlarmService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.regex.Pattern;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author zhuyicheng
 * @create 2019/11/11 14:11
 * @desc 报警管理serviceImpl
 */
@Service
public class AlarmServiceImpl implements AlarmService {
    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    private final String ALARMID_ERROR = "报警标识在园区/当前企业唯一";

    @Autowired
    private AlarmRepo alarmRepo;
    @Autowired
    private AlarmConditionRepo alarmConditionRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response queryLeftAlarmInfos(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        QAlarm obj = QAlarm.alarm;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        String objType = key.getString("objType");
        String objId = key.getString("objId");
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        String alarmName = key.getString("alarmName");
        if (isNotNullAndEmpty(alarmName)) {
            pre = ExpressionUtils.and(pre, obj.alarmName.containsIgnoreCase(alarmName));
        }
        Page<Alarm> alarms = alarmRepo.findAll(pre, pageable);
        Response ok = Response.ok(alarms.getContent(), alarms.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Transactional
    @Override
    public ServiceData insertOrUpdateAlarm(Alarm alarm) {
        /**
         * 报警标识只能是字母、数字、长度不超过10,在园区/当前企业唯一
         */
        try {
            String alarmId = alarm.getAlarmId();
            String objId = alarm.getObjId();
            String objType = alarm.getObjType();
            Alarm old = alarmRepo.findByObjTypeAndObjIdAndAlarmId(objType, objId, alarmId);
            if (alarm.getId() == null) {
                if (old != null) return ServiceData.error(ALARMID_ERROR, currentUser);
                alarm.setCreateNow();
                alarm.setCreateUserId(currentUser.userId());
                alarmRepo.save(alarm);
            } else {
                old.setAlarmId(alarm.getAlarmId());
                old.setAlarmName(alarm.getAlarmName());
                old.setAlarmType(alarm.getAlarmType());
                old.setIsUse(alarm.getIsUse());
                old.setMultiConditionsLogic(alarm.getMultiConditionsLogic());
                old.setMsgRecv(alarm.getMsgRecv());
                old.setMailRecv(alarm.getMailRecv());
                old.setMemo(alarm.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                alarmRepo.save(old);
            }
            return ServiceData.success("新增或更新成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Override
    public Response queryLeftAlarmById(Integer id) {
        try {
            Alarm alarm = alarmRepo.findById(id).get();
            Response ok = Response.ok("查询成功", alarm);
            ok.setQueryPara(id);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            Response error = Response.error("查询失败", e);
            error.setQueryPara(id);
            return error;
        }
    }

    @Transactional
    @Override
    public ServiceData delete(String objType, String objId, String alarmId) {
        try {
            alarmRepo.deleteByObjTypeAndObjIdAndAlarmId(objType, objId, alarmId);
            alarmConditionRepo.deleteByObjTypeAndObjIdAndAlarmId(objType, objId, alarmId);
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除失败", e, currentUser);
        }
    }

    public static void main(String[] args) {
        String alarmId = "111111111a";
        boolean matches = Pattern.matches("^[a-zA-Z0-9]{0,10}$", alarmId);
        System.out.println(matches);
    }
}






