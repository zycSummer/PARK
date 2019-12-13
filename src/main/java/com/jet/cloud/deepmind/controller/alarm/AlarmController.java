package com.jet.cloud.deepmind.controller.alarm;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.Alarm;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author zhuyicheng
 * @create 2019/11/11 14:05
 * @desc 报警管理
 */
@RestController
@RequestMapping("/alarm")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    /**
     * @param vo
     * @return
     * @apiNote 左侧展示导航栏中所选对象已经创建的报警列表
     */
    @PostMapping("/queryLeftAlarmInfos")
    public Response queryLeftAlarmInfos(@RequestBody QueryVO vo) {
        return alarmService.queryLeftAlarmInfos(vo);
    }

    /**
     * 新增或者更新报警
     *
     * @param alarm
     * @return
     */
    @PostMapping("/add")
    public Response add(@RequestBody @Valid Alarm alarm) {
        return alarmService.insertOrUpdateAlarm(alarm).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody @Valid Alarm alarm) {
        return alarmService.insertOrUpdateAlarm(alarm).getResponse();
    }


    /**
     * @param id
     * @return
     * @apiNote 根据id查询报警
     */
    @GetMapping("/queryLeftAlarmById/{id}")
    public Response queryLeftAlarmById(@PathVariable Integer id) {
        return alarmService.queryLeftAlarmById(id);
    }

    /**
     * @param data
     * @return
     * @apiNote 删除报警(将报警条件也将一并删除)
     */
    @PostMapping("/delete")
    public Response delete(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String alarmId = data.getString("alarmId");
        return alarmService.delete(objType, objId, alarmId).getResponse();
    }

}
