package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.AlarmCondition;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.AlarmConditionRepo;
import com.jet.cloud.deepmind.service.AlarmConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author yhy
 * @create 2019-11-12 14:06
 */
@Service
public class AlarmConditionServiceImpl implements AlarmConditionService {

    @Autowired
    private AlarmConditionRepo alarmConditionRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public ServiceData add(AlarmCondition condition) {
        String content = "新增报警条件";
        try {
            AlarmCondition dbCondition = alarmConditionRepo.findByObjTypeAndObjIdAndAlarmIdAndAlarmConditionId(condition.getObjType()
                    , condition.getObjId(), condition.getAlarmId(), condition.getAlarmConditionId());

            if (dbCondition != null) {
                throw new Exception("报警标识重复：alarmConditionId=" + condition.getAlarmConditionId());
            }
            condition.setCreateNow();
            condition.setCreateUserId(currentUser.userId());
            alarmConditionRepo.save(condition);
            return ServiceData.success(content, currentUser);
        } catch (Exception e) {
            return ServiceData.error(content, e, currentUser);
        }

    }

    @Override
    public ServiceData update(AlarmCondition condition) {
        String content = "修改报警条件";

        try {
            Optional<AlarmCondition> optional = alarmConditionRepo.findById(condition.getId());

            if (!optional.isPresent()) {
                throw new Exception("报警标识重复：alarmConditionId=" + condition.getAlarmConditionId());
            }

            AlarmCondition dbCondition = optional.get();
            dbCondition.setValueType(condition.getValueType());
            dbCondition.setCondition1Op(condition.getCondition1Op());
            dbCondition.setCondition2Op(condition.getCondition2Op());
            dbCondition.setCondition1Value(condition.getCondition1Value());
            dbCondition.setCondition2Value(condition.getCondition2Value());
            dbCondition.setConditionsLogic(condition.getConditionsLogic());
            dbCondition.setAlarmMsg(condition.getAlarmMsg());
            dbCondition.setSortId(condition.getSortId());
            dbCondition.setMemo(condition.getMemo());
            dbCondition.setDataSource(condition.getDataSource());
            dbCondition.setUpdateNow();
            dbCondition.setUpdateUserId(currentUser.userId());
            alarmConditionRepo.save(dbCondition);
            return ServiceData.success(content, currentUser);
        } catch (Exception e) {
            return ServiceData.error(content, e, currentUser);
        }

    }

    @Override
    public Response getByIndex(String objType, String objId, String alarmId, String conditionId) {
        try {
            AlarmCondition condition = alarmConditionRepo.findByObjTypeAndObjIdAndAlarmIdAndAlarmConditionId(objType, objId, alarmId, conditionId);
            Response ok = Response.ok(condition);
            ok.setQueryPara(objType, objId, alarmId, conditionId);
            return ok;
        } catch (Exception e) {
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objType, objId, alarmId, conditionId);
            return error;
        }
    }

    @Override
    public ServiceData delete(String objType, String objId, String alarmId, String conditionId) {
        String content = "删除报警条件";
        try {
            alarmConditionRepo.deleteByObjTypeAndObjIdAndAlarmIdAndAlarmConditionId(objType, objId, alarmId, conditionId);
            return ServiceData.success(content, currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(content, currentUser);
        }
    }

    @Override
    public Response query(QueryVO vo) {

        try {
            JSONObject object = vo.getKey();
            String objType = object.getString("objType");
            String objId = object.getString("objId");
            String alarmId = object.getString("alarmId");
            String datasource = object.getString("datasource");

            Page<AlarmCondition> conditions;
            if (StringUtils.isNullOrEmpty(datasource)) {
                conditions = alarmConditionRepo.findAllByObjTypeAndObjIdAndAlarmId(objType, objId, alarmId, vo.Pageable());
            } else {
                conditions = alarmConditionRepo.findAllByObjTypeAndObjIdAndAlarmIdAndDataSourceContains(objType, objId, alarmId, datasource, vo.Pageable());
            }
            Response ok = Response.ok(conditions.getContent(), conditions.getTotalElements());
            ok.setQueryPara(vo);
            return ok;
        } catch (Exception e) {
            Response error = Response.error(e.getMessage());
            error.setQueryPara(vo);
            return error;
        }
    }
}
