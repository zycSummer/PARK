package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.Park;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.ParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author zhuyicheng
 * @create 2019/11/15 14:51
 * @desc 基础数据(园区)
 */
@RestController
@RequestMapping("/park")
public class ParkController {
    @Autowired
    private ParkService parkService;

    /**
     * @return
     * @apiNote 对象管理(查询园区)
     * 查询条件
     * 园区标识
     * 园区名称
     */
    @PostMapping("/queryPark")
    public Response queryPark(@RequestBody JSONObject data) {
        String parkId = data.getString("parkId");
        String parkName = data.getString("parkName");
        return parkService.queryPark(parkId, parkName);
    }

    /**
     * @apiNote 点击新增按钮，先检查 tb_park表中是否有记录，如果有，则提示“已经存在一个园区信息，不可再新增!”；如果没有则弹出新增对话框。
     */
    @GetMapping("/isExistPark")
    public Response isExistPark() {
        return parkService.isExistPark();
    }

    /**
     * @apiNote 对象管理(新增或者更新园区)
     */
    @PostMapping("/add")
    public Response add(@Valid Park park, MultipartFile file) {
        return parkService.insertOrUpdatePark(park, file).getResponse();
    }

    @PostMapping("/edit")
    public Response edit(@Valid Park park, MultipartFile file) {
        return parkService.insertOrUpdatePark(park, file).getResponse();
    }

    /**
     * @return
     * @apiNote 对象管理(根据id查找园区)
     */
    @GetMapping("/queryParkById/{id}")
    public Response queryParkById(@PathVariable Integer id) {
        return parkService.queryParkById(id);
    }

    /**
     * @return
     * @apiNote 对象管理(根据园区标识删除园区)
     */
    @GetMapping("/delete/{parkId}")
    public Response delete(@PathVariable String parkId) {
        return parkService.delete(parkId).getResponse();
    }

    /**
     * @return
     * @apiNote 查询园区
     */
    @GetMapping("/queryFirstPark")
    public Response queryFirstPark() {
        return parkService.queryFirstPark();
    }
}
