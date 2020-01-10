package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.entity.EnergyMonthlyUsagePlan;
import com.jet.cloud.deepmind.entity.SysEnergyType;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.EnergyMonthlyUsagePlanRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.service.EnergyMonthlyUsagePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/11/21 10:00
 */
@Service
public class EnergyMonthlyUsagePlanServiceImpl implements EnergyMonthlyUsagePlanService {

    @Autowired
    private EnergyMonthlyUsagePlanRepo energyMonthlyUsagePlanRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Override
    public Response query(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        JSONObject key = vo.getKey();
        String objType = key.getString("objType");
        String objId = key.getString("objId");
        Long start = key.getLong("start");
        LocalDateTime localDateTime1 = DateUtil.longToLocalTime(start);
        int startYear = localDateTime1.getYear();
        int startMonth = localDateTime1.getMonth().getValue();
        Long end = key.getLong("end");
        LocalDateTime localDateTime2 = DateUtil.longToLocalTime(end);
        int endYear = localDateTime2.getYear();
        int endMonth = localDateTime2.getMonth().getValue();
        JSONArray energyTypeIds = key.getJSONArray("energyTypeIds");
        List<String> energyParaIdList = energyTypeIds.toJavaList(String.class);
        String sm = startMonth + "";
        String em = endMonth + "";
        if (startMonth < 10) {
            sm = "0" + startMonth;
        }
        if (endMonth < 10) {
            em = "0" + endMonth;
        }
        String startDate = startYear + "-" + sm;
        String endDate = endYear + "-" + em;
        Page<EnergyMonthlyUsagePlan> list = energyMonthlyUsagePlanRepo.findData(objType, objId, energyParaIdList, startDate, endDate, pageable);
        List<EnergyMonthlyUsagePlan> content = list.getContent();
        ArrayList<String> energyTypeIdList = new ArrayList<>();
        for (EnergyMonthlyUsagePlan energyMonthlyUsagePlan : content) {
            String energyTypeId = energyMonthlyUsagePlan.getEnergyTypeId();
            energyTypeIdList.add(energyTypeId);
        }
        List<SysEnergyType> sysEnergyTypes = sysEnergyTypeRepo.findByEnergyTypeIdIn(energyTypeIdList);
        HashMap<String, String> map = new HashMap<>();
        for (SysEnergyType sysEnergyType : sysEnergyTypes) {
            String energyTypeName = sysEnergyType.getEnergyTypeName();
            String energyTypeId = sysEnergyType.getEnergyTypeId();
            map.put(energyTypeId, energyTypeName);
        }
        for (EnergyMonthlyUsagePlan energyMonthlyUsagePlan : content) {
            String energyTypeName = map.get(energyMonthlyUsagePlan.getEnergyTypeId());
            energyMonthlyUsagePlan.setEnergyTypeName(energyTypeName);
        }
        Response ok = Response.ok(content, list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public Response queryById(Integer id) {
        EnergyMonthlyUsagePlan energyMonthlyUsagePlan = energyMonthlyUsagePlanRepo.findById(id).get();
        Response ok = Response.ok("查询成功", energyMonthlyUsagePlan);
        ok.setQueryPara(id);
        return ok;
    }

    @Transactional
    @Override
    public ServiceData addOrEdit(EnergyMonthlyUsagePlan energyMonthlyUsagePlan) {
        try {
            String energyTypeId = energyMonthlyUsagePlan.getEnergyTypeId();
            String objType = energyMonthlyUsagePlan.getObjType();
            String objId = energyMonthlyUsagePlan.getObjId();
            Integer year = energyMonthlyUsagePlan.getYear();
            Integer month = energyMonthlyUsagePlan.getMonth();
            EnergyMonthlyUsagePlan old = energyMonthlyUsagePlanRepo.findByObjTypeAndObjIdAndEnergyTypeIdAndYearAndMonth(objType, objId, energyTypeId, year, month);
            if (energyMonthlyUsagePlan.getId() == null) {
                if (old != null) {
                    return ServiceData.error("新增的能源月使用计划重复", currentUser);
                }
                //新增企业
                energyMonthlyUsagePlan.setCreateNow();
                energyMonthlyUsagePlan.setCreateUserId(currentUser.userId());
                energyMonthlyUsagePlanRepo.save(energyMonthlyUsagePlan);
            } else {
                old.setUsage(energyMonthlyUsagePlan.getUsage());
                old.setMemo(energyMonthlyUsagePlan.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                energyMonthlyUsagePlanRepo.save(old);
            }
            return ServiceData.success("新增或更新成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Override
    public ServiceData delete(Integer id) {
        try {
            energyMonthlyUsagePlanRepo.deleteById(id);
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除失败", e, currentUser);
        }
    }
}
