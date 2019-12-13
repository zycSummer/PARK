package com.jet.cloud.deepmind.controller.basic;

import com.jet.cloud.deepmind.entity.EnergyMonthlyUsagePlan;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.EnergyMonthlyUsagePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/21 9:57
 * @desc 基础数据(对象用能计划管理)
 */
@RestController
@RequestMapping("/energyMonthlyUsagePlan")
public class EnergyMonthlyUsagePlanController {

    @Autowired
    private EnergyMonthlyUsagePlanService energyMonthlyUsagePlanService;

    /**
     * 查询
     *
     * @param vo
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return energyMonthlyUsagePlanService.query(vo);
    }

    /**
     * 根据id查询用能计划
     *
     * @param id
     * @return
     */
    @GetMapping("/queryById/{id}")
    public Response queryById(@PathVariable Integer id) {
        return energyMonthlyUsagePlanService.queryById(id);
    }

    /**
     * 新增用能计划
     *
     * @param energyMonthlyUsagePlan
     * @return
     */
    @PostMapping("/add")
    public Response add(@RequestBody @Valid EnergyMonthlyUsagePlan energyMonthlyUsagePlan) {
        return energyMonthlyUsagePlanService.addOrEdit(energyMonthlyUsagePlan).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody @Valid EnergyMonthlyUsagePlan energyMonthlyUsagePlan) {
        return energyMonthlyUsagePlanService.addOrEdit(energyMonthlyUsagePlan).getResponse();
    }

    /**
     * 根据id删除用能计划
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public Response delete(@PathVariable Integer id) {
        return energyMonthlyUsagePlanService.delete(id).getResponse();

    }
}
