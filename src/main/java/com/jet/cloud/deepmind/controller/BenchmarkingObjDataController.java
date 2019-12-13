package com.jet.cloud.deepmind.controller;

import com.jet.cloud.deepmind.entity.BenchmarkingObjData;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.BenchmarkingObjDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/12/11 10:28
 * @desc 对标对象指标数据
 */
@RestController
@RequestMapping("/benchmarkingObjData")
public class BenchmarkingObjDataController {
    @Autowired
    private BenchmarkingObjDataService benchmarkingObjDataService;

    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo){
        return benchmarkingObjDataService.query(vo);
    }

    @GetMapping("/queryById/{id}")
    public Response queryById(@PathVariable Integer id){
        return benchmarkingObjDataService.queryById(id);
    }

    @PostMapping("/add")
    public Response add(@RequestBody @Valid BenchmarkingObjData benchmarkingObjData){
        return benchmarkingObjDataService.addOrEdit(benchmarkingObjData).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody @Valid BenchmarkingObjData benchmarkingObjData){
        return benchmarkingObjDataService.addOrEdit(benchmarkingObjData).getResponse();
    }

    @GetMapping("/delete/{id}")
    public Response delete(@PathVariable Integer id){
        return benchmarkingObjDataService.delete(id).getResponse();
    }
}
