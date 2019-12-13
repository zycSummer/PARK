package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.BenchmarkingObj;
import com.jet.cloud.deepmind.entity.QBenchmarkingObj;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.BenchmarkingObjDataRepo;
import com.jet.cloud.deepmind.repository.BenchmarkingObjRepo;
import com.jet.cloud.deepmind.service.BenchmarkingObjService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author maohandong
 * @create 2019/12/11 9:54
 */
@Service
public class BenchmarkingObjServiceImpl implements BenchmarkingObjService {
    @Autowired
    private BenchmarkingObjRepo benchmarkingObjRepo;
    @Autowired
    private BenchmarkingObjDataRepo benchmarkingObjDataRepo;
    @Autowired
    private CurrentUser currentUser;
    @Override
    public Response query(String objType, String objId, String benchmarkingObjName) {
        Sort sort = new Sort(Sort.Direction.ASC, "sortId");
        QBenchmarkingObj obj = QBenchmarkingObj.benchmarkingObj;
        Predicate pre = obj.isNotNull();
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        if (benchmarkingObjName != null && !benchmarkingObjName.equals("")) {
            pre = ExpressionUtils.and(pre, obj.benchmarkingObjName.containsIgnoreCase(benchmarkingObjName));
        }
        List<BenchmarkingObj> all = (List<BenchmarkingObj>) benchmarkingObjRepo.findAll(pre, sort);
        Response ok = Response.ok(all);
        ok.setQueryPara(objId, objType, benchmarkingObjName);
        return ok;
    }

    @Override
    public Response queryById(Integer id) {
        BenchmarkingObj benchmarkingObj = benchmarkingObjRepo.findById(id).get();
        Response ok = Response.ok("根据id查询对标对象成功",benchmarkingObj);
        ok.setQueryPara(id);
        return ok;
    }

    @Override
    public ServiceData addOrEdit(BenchmarkingObj benchmarkingObj) {
        try {
            String objType = benchmarkingObj.getObjType();
            String objId = benchmarkingObj.getObjId();
            String benchmarkingObjId = benchmarkingObj.getBenchmarkingObjId();
            BenchmarkingObj old = benchmarkingObjRepo.findByObjTypeAndObjIdAndBenchmarkingObjId(objType, objId, benchmarkingObjId);
            if (benchmarkingObj.getId() == null) {
                if (old != null) {
                    return ServiceData.error("对标对象标识在园区/当前企业唯一", currentUser);
                }
                benchmarkingObj.setCreateNow();
                benchmarkingObj.setCreateUserId(currentUser.userId());
                benchmarkingObjRepo.save(benchmarkingObj);
            }else {
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                old.setMemo(benchmarkingObj.getMemo());
                old.setBenchmarkingObjName(benchmarkingObj.getBenchmarkingObjName());
                old.setBenchmarkingObjType(benchmarkingObj.getBenchmarkingObjType());
                old.setSortId(benchmarkingObj.getSortId());
                benchmarkingObjRepo.save(old);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或修改失败", currentUser);
        }
        return ServiceData.success("新增或修改成功", currentUser);
    }

    @Override
    @Transactional
    public ServiceData delete(String objType, String objId, String benchmarkingObjId) {
        try {
            benchmarkingObjRepo.deleteByObjTypeAndObjIdAndBenchmarkingObjId(objType,objId,benchmarkingObjId);
            benchmarkingObjDataRepo.deleteAllByObjTypeAndObjIdAndBenchmarkingObjId(objType,objId,benchmarkingObjId);
            return ServiceData.success("删除成功",currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除失败",e,currentUser);
        }
    }
}
