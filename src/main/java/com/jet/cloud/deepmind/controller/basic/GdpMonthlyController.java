package com.jet.cloud.deepmind.controller.basic;

import com.jet.cloud.deepmind.entity.GdpMonthly;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.GdpMonthlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author maohandong
 * @create 2019/11/21 15:40
 * @desc 对象GDP管理
 */
@RestController
@RequestMapping("/gdpMonthly")
public class GdpMonthlyController {
    @Autowired
    private GdpMonthlyService gdpMonthlyService;

    /**
     * 查询
     *
     * @param vo
     * @return
     */
    @PostMapping("/query")
    public Response query(@RequestBody QueryVO vo) {
        return gdpMonthlyService.query(vo);
    }

    @GetMapping("/queryById/{id}")
    public Response queryById(@PathVariable Integer id) {
        return gdpMonthlyService.queryById(id);

    }

    /**
     * 新增或修改gdp
     *
     * @param gdpMonthly
     * @return
     */
    @PostMapping("/add")
    public Response add(@RequestBody @Valid GdpMonthly gdpMonthly) {
        return gdpMonthlyService.addOrEdit(gdpMonthly).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@RequestBody @Valid GdpMonthly gdpMonthly) {
        return gdpMonthlyService.addOrEdit(gdpMonthly).getResponse();
    }

    @GetMapping("/delete/{id}")
    public Response delete(@PathVariable Integer id) {
        return gdpMonthlyService.delete(id).getResponse();

    }

}
