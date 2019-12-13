package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.BenchmarkingObjData;
import com.jet.cloud.deepmind.entity.QBenchmarkingObjData;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.BenchmarkingObjDataRepo;
import com.jet.cloud.deepmind.service.BenchmarkingObjDataService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author maohandong
 * @create 2019/12/11 10:34
 */
@Service
public class BenchmarkingObjDataServiceImpl implements BenchmarkingObjDataService {

    @Autowired
    private BenchmarkingObjDataRepo benchmarkingObjDataRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response query(QueryVO vo) {
        Sort sort = new Sort(Sort.Direction.DESC, "year");
        Pageable pageable = vo.Pageable(sort);
        QBenchmarkingObjData obj = QBenchmarkingObjData.benchmarkingObjData;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        String objType = key.getString("objType");
        String objId = key.getString("objId");
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        String benchmarkingObjId = key.getString("benchmarkingObjId");
        pre = ExpressionUtils.and(pre, obj.benchmarkingObjId.containsIgnoreCase(benchmarkingObjId));
        Integer startYear = key.getInteger("start");
        Integer endYear = key.getInteger("end");
        pre = ExpressionUtils.and(pre, obj.year.between(startYear, endYear));
        Page<BenchmarkingObjData> list = benchmarkingObjDataRepo.findAll(pre, pageable);
        Response ok = Response.ok(list.getContent(), list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public Response queryById(Integer id) {
        BenchmarkingObjData benchmarkingObjData = benchmarkingObjDataRepo.findById(id).get();
        Response ok = Response.ok("查询对标对象指标数据成功", benchmarkingObjData);
        ok.setQueryPara(id);
        return ok;
    }

    @Override
    @Transactional
    public ServiceData addOrEdit(BenchmarkingObjData benchmarkingObjData) {
        try {
            String objType = benchmarkingObjData.getObjType();
            String objId = benchmarkingObjData.getObjId();
            String benchmarkingObjId = benchmarkingObjData.getBenchmarkingObjId();
            Integer year = benchmarkingObjData.getYear();
            BenchmarkingObjData old = benchmarkingObjDataRepo.findByObjTypeAndObjIdAndBenchmarkingObjIdAndYear(objType, objId, benchmarkingObjId, year);
            if (benchmarkingObjData.getId() == null){
                if (old != null){
                    return ServiceData.error("对标对象指标数据在园区/当前企业已存在", currentUser);
                }
                benchmarkingObjData.setCreateNow();
                benchmarkingObjData.setCreateUserId(currentUser.userId());
                benchmarkingObjDataRepo.save(benchmarkingObjData);
            }else {
                old.setGdpElectricity(benchmarkingObjData.getGdpElectricity());
                old.setGdpWater(benchmarkingObjData.getGdpWater());
                old.setGdpStdCoal(benchmarkingObjData.getGdpStdCoal());
                old.setAddValueStdCoal(benchmarkingObjData.getAddValueStdCoal());
                old.setMemo(benchmarkingObjData.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                benchmarkingObjDataRepo.save(old);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改失败",e, currentUser);
        }
        return ServiceData.success("新增或修改成功", currentUser);
    }

    @Override
    public ServiceData delete(Integer id) {
        try {
            benchmarkingObjDataRepo.deleteById(id);
            return ServiceData.success("删除对标对象指标数据成功",currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除对标对象指标数据失败",e,currentUser);
        }
    }
}
