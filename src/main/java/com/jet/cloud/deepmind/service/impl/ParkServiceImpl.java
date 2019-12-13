package com.jet.cloud.deepmind.service.impl;

import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.entity.Park;
import com.jet.cloud.deepmind.entity.QPark;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.service.ParkService;
import com.querydsl.core.types.ExpressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.querydsl.core.types.Predicate;

import javax.transaction.Transactional;
import java.util.List;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author zhuyicheng
 * @create 2019/11/15 14:55
 * @desc 基础数据serviceImpl(园区)
 */
@Service
public class ParkServiceImpl implements ParkService {
    private static final Logger logger = LoggerFactory.getLogger(CoManageServiceImpl.class);

    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response queryPark(String parkId, String parkName) {
        try {
            QPark qPark = QPark.park;
            Predicate pre = qPark.isNotNull();
            if (isNotNullAndEmpty(parkId)) {
                pre = ExpressionUtils.and(pre, qPark.parkId.containsIgnoreCase(parkId));
            } else if (isNotNullAndEmpty(parkName)) {
                pre = ExpressionUtils.and(pre, qPark.parkName.containsIgnoreCase(parkName));
            }
            List<Park> parks = (List<Park>) parkRepo.findAll(pre);
            Response ok = Response.ok("查询园区成功", parks);
            ok.setQueryPara(parkId, parkName);
            return ok;
        } catch (Exception e) {
            logger.error("查询园区失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询园区失败", e);
            error.setQueryPara(parkId, parkName);
            return error;
        }
    }

    @Override
    public Response isExistPark() {
        List<Park> parks = parkRepo.findAll();
        if (!parks.isEmpty()) {
            Response error = Response.error("已经存在一个园区信息，不可再新增!");
            error.setQueryPara();
            return error;
        }
        Response ok = Response.ok("没有查到园区信息");
        ok.setQueryPara();
        return ok;
    }

    @Transactional
    @Override
    public ServiceData insertOrUpdatePark(Park park) {
        try {
            if (park.getId() == null) {
                park.setCreateNow();
                park.setCreateUserId(currentUser.userId());
                parkRepo.save(park);
            } else {
                Park old = parkRepo.findById(park.getId()).get();
                old.setParkId(park.getParkId());
                old.setParkName(park.getParkName());
                old.setRtdbTenantId(park.getRtdbTenantId());
                old.setLongitude(park.getLongitude());
                old.setLatitude(park.getLatitude());
                old.setScale(park.getScale());
                old.setMemo(park.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                parkRepo.save(old);
            }
            return ServiceData.success("新增或更新成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Override
    public Response queryParkById(Integer id) {
        Response ok = Response.ok("根据id查找园区成功", parkRepo.findById(id));
        ok.setQueryPara(id);
        return ok;
    }

    @Transactional
    @Override
    public ServiceData delete(String parkId) {
        try {
            parkRepo.deleteByParkId(parkId);
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除失败", e, currentUser);
        }
    }

    @Override
    public Response queryFirstPark() {
        Park firstPark = parkRepo.findFirstPark();
        return Response.ok("根据园区成功", firstPark);
    }
}
