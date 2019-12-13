package com.jet.cloud.deepmind.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.VResult;
import com.jet.cloud.deepmind.model.SolutionMsgVO;
import com.jet.cloud.deepmind.service.AlarmSender;
import com.jet.cloud.deepmind.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/25 9:54
 * @desc huawei api接口
 */
@RequestMapping("/api/v1")
@RestController
public class ApiController {
    @Autowired
    private ApiService apiService;
    @Autowired
    private AlarmSender alarmSender;
    /**
     * @param jsonObject
     * @return
     * @apiNote 查询仪表基础信息
     */
    @PostMapping("/meter/query")
    public VResult queryMeter(@RequestBody JSONObject jsonObject) {
        String parkId = jsonObject.getString("parkId");
        String meterId = jsonObject.getString("meterId");
        return apiService.queryMeter(parkId, meterId);
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 查询仪表所有参数最新采集数据
     */
    @PostMapping("/meter-last-value/query")
    public VResult queryMeterLastValue(@RequestBody JSONObject jsonObject) {
        String parkId = jsonObject.getString("parkId");
        String meterId = jsonObject.getString("meterId");
        return apiService.queryMeterLastValue(parkId, meterId);
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 查询仪表指定参数历史数据
     */
    @PostMapping("/meter-his-value/query")
    public VResult queryMeterHisValue(@RequestBody JSONObject jsonObject) {
        String parkId = jsonObject.getString("parkId");
        String meterId = jsonObject.getString("meterId");
        String paraId = jsonObject.getString("paraId");
        Long startTime = jsonObject.getLong("startTime");
        Long endTime = jsonObject.getLong("endTime");
        Integer interval = jsonObject.getInteger("interval");
        return apiService.queryMeterHisValue(parkId, meterId, paraId, startTime, endTime, interval);
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 查询仪表日小时用量数据
     */
    @PostMapping("/meter-usage-value/query")
    public VResult queryMeterUsageValue(@RequestBody JSONObject jsonObject) {
        String parkId = jsonObject.getString("parkId");
        String meterId = jsonObject.getString("meterId");
        String date = jsonObject.getString("date");
        return apiService.queryMeterUsageValue(parkId, meterId, date);
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 查询仪表明天最大负荷预测值
     */
    @PostMapping("/meter-next-day-max-load-value/forecast")
    public VResult queryMeterNextMaxLoadValue(@RequestBody JSONObject jsonObject) {
        String parkId = jsonObject.getString("parkId");
        String meterId = jsonObject.getString("meterId");
        return apiService.queryMeterNextMaxLoadValue(parkId, meterId);
    }

    @GetMapping("/send")
    public void send() {
        List<SolutionMsgVO> solutionMsgVOS = new ArrayList<>();
        SolutionMsgVO solutionMsgVO = new SolutionMsgVO();
        solutionMsgVO.setParkId("LYGSHCYJD");
        solutionMsgVO.setDiagnosisId("a03");
        solutionMsgVO.setDiagnosisDesc("冷冻主机启停诊断。");
        solutionMsgVO.setDiagnosisTime(1574305067000L);
        solutionMsgVO.setProblemDesc("1#主机开启时间为09:00，而正常开启时间为9:30，主机开启时间过早。");
        solutionMsgVO.setSolution("调整1#主机开机时间，按照设定时间开启，节约电能。");
        solutionMsgVOS.add(solutionMsgVO);
        alarmSender.sendHttpSolution(solutionMsgVOS);
    }
}
