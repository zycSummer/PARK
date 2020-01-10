package com.jet.cloud.deepmind.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.entity.Notice;
import com.jet.cloud.deepmind.entity.QNotice;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.NoticeRepo;
import com.jet.cloud.deepmind.service.NoticeService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

/**
 * @author maohandong
 * @create 2019/12/23 16:21
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepo noticeRepo;

    @Autowired
    private CurrentUser currentUser;

    @Override
    public Response query(QueryVO vo) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = vo.Pageable(sort);
        QNotice obj = QNotice.notice;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        String objType = key.getString("objType");
        pre = ExpressionUtils.and(pre, obj.objType.eq(objType));
        String objId = key.getString("objId");
        pre = ExpressionUtils.and(pre, obj.objId.eq(objId));
        Long start = key.getLongValue("start");
        Long end = key.getLongValue("end");
        pre = ExpressionUtils.and(pre, obj.createTime.between(DateUtil.longToLocalTime(start), DateUtil.longToLocalTime(end)));
        Page<Notice> list = noticeRepo.findAll(pre, pageable);
        Response ok = Response.ok(list.getContent(), list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    @Transactional
    public ServiceData addOrEdit(Notice notice) {
        try {
            String objType = notice.getObjType();
            String objId = notice.getObjId();
            LocalDateTime createTime = notice.getCreateTime();
            Notice old = noticeRepo.findByObjTypeAndObjIdAndCreateTime(objType, objId, createTime);
            if (notice.getId() == null) {
                if (old != null) {
                    return ServiceData.error("公告信息已存在", currentUser);
                }
                notice.setCreateNow();
                notice.setCreateUserId(currentUser.userId());
                noticeRepo.save(notice);
            } else {
                old.setNoticeContent(notice.getNoticeContent());
                old.setUpdateUserId(currentUser.userId());
                old.setUpdateNow();
                old.setMemo(notice.getMemo());
                noticeRepo.save(old);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增修改失败", e, currentUser);
        }
        return ServiceData.success("新增修改成功", currentUser);
    }

    @Override
    @Transactional
    public ServiceData delete(Integer id) {
        try {
            noticeRepo.deleteById(id);
            return ServiceData.success("删除公告信息成功", currentUser);
        } catch (Exception e) {
            return ServiceData.error("删除公告信息失败", e, currentUser);
        }
    }
}
