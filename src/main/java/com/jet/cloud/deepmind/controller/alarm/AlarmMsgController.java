package com.jet.cloud.deepmind.controller.alarm;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.AlarmMsgService;
import com.jet.cloud.deepmind.service.scheduler.AlarmSchedulerTask;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yhy
 * @create 2019-11-14 09:54
 */
@RestController
@RequestMapping("/alarmMsg")
public class AlarmMsgController {


    @Autowired
    private AlarmMsgService alarmMsgService;
    @Autowired
    private AlarmSchedulerTask task;

    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return alarmMsgService.query(vo);
    }


    @GetMapping("/check")
    public void check() {
        task.check();
    }

    @PostMapping("/ack")
    public Response ack(@RequestBody List<Integer> ids) {
        return alarmMsgService.ack(ids).getResponse();
    }

    @PostMapping("/updateMemo")
    public Response updateMemo(@RequestBody JSONObject data) {
        List<Integer> ids = data.getJSONArray("ids").toJavaList(Integer.class);
        String memo = data.getString("memo");
        return alarmMsgService.updateMemo(ids, memo).getResponse();
    }
}
