package com.jet.cloud.deepmind.controller.alarm;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.AlarmCondition;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.AlarmConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author yhy
 * @create 2019-11-12 10:56
 */
@RestController
@RequestMapping("/alarmCondition")
public class AlarmConditionController {


    @Autowired
    private AlarmConditionService alarmConditionService;


    /**
     * @param alarmCondition { "alarmConditionId": "add2", "alarmId": "ALARMCONDITION", "alarmMsg": "alarm", "condition1Op": "<", "condition1Value": 0, "condition2Op": ">", "condition2Value": 0, "conditionsLogic": null, "dataSource": "P", "memo": "add", "objId": "LYGSHCYJD", "objType": "PARK", "sortId": "1001", "valueType": "LAST_VALUE" }
     * @return
     */
    @PostMapping("/add")
    public Response add(@RequestBody @Valid AlarmCondition alarmCondition) {
        return alarmConditionService.add(alarmCondition).getResponse();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid AlarmCondition alarmCondition) {
        return alarmConditionService.update(alarmCondition).getResponse();
    }

    @PostMapping("/getByIndex")
    public Response getByIndex(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String alarmId = data.getString("alarmId");
        String conditionId = data.getString("conditionId");
        return alarmConditionService.getByIndex(objType, objId, alarmId, conditionId);
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String alarmId = data.getString("alarmId");
        String conditionId = data.getString("conditionId");
        return alarmConditionService.delete(objType, objId, alarmId, conditionId).getResponse();
    }

    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return alarmConditionService.query(vo);
    }
}
