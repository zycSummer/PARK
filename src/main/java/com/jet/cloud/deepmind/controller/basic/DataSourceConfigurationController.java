package com.jet.cloud.deepmind.controller.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.entity.DataSource;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.service.DataSourceConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/11/20 9:36
 * @desc 对象数据源配置Controller
 */
@RestController
@RequestMapping("/datasource")
public class DataSourceConfigurationController {
    @Autowired
    private DataSourceConfigurationService dataSourceConfigurationService;

    /**
     * @return
     * @apiNote 查询条件(默认全选)
     */
    @GetMapping("/queryCondition")
    public Response queryCondition() {
        return dataSourceConfigurationService.queryCondition();
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 对象数据源配置查询
     */
    @PostMapping("/queryDataSource")
    public Response queryDataSource(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        JSONArray jsonArray = jsonObject.getJSONArray("energyTypeIds");//默认全选
        List<String> energyTypeIds = jsonArray.toJavaList(String.class);
        return dataSourceConfigurationService.queryDataSource(objType, objId, energyTypeIds);
    }

    /**
     * @param id
     * @return
     * @apiNote 根据id查询数据源
     */
    @GetMapping("/queryDataSourceById/{id}")
    public Response queryDataSourceById(@PathVariable Integer id) {
        return dataSourceConfigurationService.queryDataSourceById(id);
    }

    /**
     * @param dataSource
     * @return
     * @apiNote 新增数据源
     */
    @PostMapping("/insert")
    public Response insert(@RequestBody DataSource dataSource) {
        return dataSourceConfigurationService.insertOrUpdateDataSource(dataSource).getResponse();
    }

    /**
     * @param dataSource
     * @return
     * @apiNote 更新数据源
     */
    @PostMapping("/update")
    public Response update(@RequestBody DataSource dataSource) {
        return dataSourceConfigurationService.insertOrUpdateDataSource(dataSource).getResponse();
    }

    /**
     * @param jsonObject
     * @return
     * @apiNote 删除数据源
     */
    @PostMapping("/deleteDataSource")
    public Response deleteDataSource(@RequestBody JSONObject jsonObject) {
        String objType = jsonObject.getString("objType");
        String objId = jsonObject.getString("objId");
        String energyTypeId = jsonObject.getString("energyTypeId");
        String energyParaId = jsonObject.getString("energyParaId");
        return dataSourceConfigurationService.deleteDataSource(objType, objId, energyTypeId, energyParaId).getResponse();
    }
}
