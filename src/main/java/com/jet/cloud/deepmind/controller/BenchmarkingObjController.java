package com.jet.cloud.deepmind.controller;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.BenchmarkingObj;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.BenchmarkingObjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/12/11 9:51
 * @desc 对标对象管理
 */
@RestController
@RequestMapping("/benchmarkingObj")
public class BenchmarkingObjController {

    @Autowired
    private BenchmarkingObjService benchmarkingObjService;

    /**
     * 查询
     *
     * @param data
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String benchmarkingObjName = data.getString("benchmarkingObjName");
        return benchmarkingObjService.query(objType, objId, benchmarkingObjName);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/queryById/{id}")
    public Response queryById(@PathVariable Integer id) {
        return benchmarkingObjService.queryById(id);
    }

    /**
     * 新增
     *
     * @param benchmarkingObj
     * @return
     */
    @PostMapping("/add")
    public Response add(@Valid @RequestBody BenchmarkingObj benchmarkingObj) {
        return benchmarkingObjService.addOrEdit(benchmarkingObj).getResponse();
    }

    /**
     * 修改
     *
     * @param benchmarkingObj
     * @return
     */
    @PostMapping("/edit")
    public Response edit(@Valid @RequestBody BenchmarkingObj benchmarkingObj) {
        return benchmarkingObjService.addOrEdit(benchmarkingObj).getResponse();
    }

    /**
     * 删除（删除tb_obj_benchmarking_obj、tb_obj_benchmarking_obj_data中的相关数据）
     *
     * @param data
     * @return
     */
    @PostMapping("/delete")
    public Response delete(@RequestBody JSONObject data) {
        String objType = data.getString("objType");
        String objId = data.getString("objId");
        String benchmarkingObjId = data.getString("benchmarkingObjId");
        return benchmarkingObjService.delete(objType, objId, benchmarkingObjId).getResponse();
    }
}
