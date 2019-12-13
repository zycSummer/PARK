package com.jet.cloud.deepmind.controller;


import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.repository.CommonRepo;
import com.jet.cloud.deepmind.rtdb.model.QueryBody;
import com.jet.cloud.deepmind.rtdb.model.TimeUnit;
import com.jet.cloud.deepmind.rtdb.model.AggregatorDataResponse;
import com.jet.cloud.deepmind.rtdb.model.SampleDataResponse;
import com.jet.cloud.deepmind.rtdb.service.KairosdbClient;
import com.jet.cloud.deepmind.service.report.ReportManageService;
import com.jet.cloud.deepmind.service.report.ReportQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;


/**
 * @author yhy
 * @create 2019-10-10 11:46
 */
@RestController
@RequestMapping("/client")
public class TestController {


    @Autowired
    private KairosdbClient client;
    @Autowired
    private CommonRepo commonRepo;
    @Autowired
    private ReportManageService reportManageService;

    @PostMapping("/queryLast")
    public Response queryLast(@RequestBody List<String> pointIds) {
        try {
            List<SampleDataResponse> last = client.queryLast(pointIds, 1L);
            return Response.ok(last);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("查询错误：" + e.getMessage());
        }

    }


    @PostMapping("/query")
    public Response query(@RequestBody QueryBody queryBody) {
        try {
            AggregatorDataResponse response = client.queryAvg(queryBody.getPoints(), queryBody.getStartTime()
                    , queryBody.getEndTime(), queryBody.getInterval(), queryBody.getUnit(), 1, TimeUnit.HOURS);
            return Response.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("查询错误：" + e.getMessage());
        }

    }

    @GetMapping("/testRepo")
    public void testRepo() {
        commonRepo.queryAllButtonsByMenuUrl("/admin/analogy");
    }
}
