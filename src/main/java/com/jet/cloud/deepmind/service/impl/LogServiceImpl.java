package com.jet.cloud.deepmind.service.impl;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.ExcelUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.QSysLog;
import com.jet.cloud.deepmind.entity.SysLog;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.SysLogRowModel;
import com.jet.cloud.deepmind.model.Triple;
import com.jet.cloud.deepmind.repository.SysLogRepo;
import com.jet.cloud.deepmind.service.LogService;
import com.jet.cloud.deepmind.service.excel.CommonExcelHeadStyleHandler;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.jet.cloud.deepmind.common.util.StringUtils.isNotNullAndEmpty;

/**
 * @author yhy
 * @create 2019-10-14 13:37
 */
@Service
public class LogServiceImpl implements LogService {


    @Autowired
    private SysLogRepo sysLogRepo;
    @Autowired
    private CommonExcelHeadStyleHandler commonExcelHeadStyleHandler;

    @Override
    @Async
    public CompletableFuture<SysLog> save(SysLog log) {
        SysLog result = null;
        try {
            result = sysLogRepo.save(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public Response queryLog(QueryVO vo) {
        Page<SysLog> sysLogs = queryLogPage(vo);
        Response ok = Response.ok(sysLogs.getContent(), sysLogs.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    private Page<SysLog> queryLogPage(QueryVO vo) {
        Pageable pageable = vo.Pageable();
        QSysLog obj = QSysLog.sysLog;
        JSONObject key = vo.getKey();
        Predicate pre = obj.isNotNull();
        if (key != null) {
            String userId = key.getString("userId");
            if (isNotNullAndEmpty(userId)) {
                pre = ExpressionUtils.and(pre, obj.userId.containsIgnoreCase(userId));
            }
            Long beginTime = key.getLong("beginTime");
            Long endTime = key.getLong("endTime");
            if (beginTime != null && endTime != null) {
                pre = ExpressionUtils.and(pre, obj.operateTime.between(DateUtil.longToLocalTime(beginTime), DateUtil.longToLocalTime(endTime)));
            }
        }
        return sysLogRepo.findAll(pre, pageable);
    }


    @Override
    public void exportExcel(String userId, Long start, Long end, HttpServletRequest request, HttpServletResponse response) {

        QueryVO queryVO = new QueryVO();
        JSONObject o = new JSONObject();
        o.put("userId", userId);
        o.put("start", start);
        o.put("end", end);
        queryVO.setKey(o);
        queryVO.setLimit(Integer.MAX_VALUE);
        queryVO.setPage(1);
        Page<SysLog> sysLogs = queryLogPage(queryVO);

        try {
            ExcelWriter writer;
            List<SysLogRowModel> list = new ArrayList<>();
            for (SysLog sysLog : sysLogs.getContent()) {
                list.add(new SysLogRowModel(sysLog));
            }
            String title = (StringUtils.isNullOrEmpty(userId) ? "" : userId + " - ") + DateUtil.longToString(start) + " - " + DateUtil.longToString(end) + "系统日志导出";
            Triple<HttpServletRequest, HttpServletResponse, ServletOutputStream> triple = ExcelUtil.setExcelRespAndReqAndGetServletOutputStream(title + ExcelTypeEnum.XLSX.getValue(), request, response);
            response = triple.getMid();
            ServletOutputStream outputStream = triple.getRight();
            writer = new ExcelWriter(null, outputStream, ExcelTypeEnum.XLSX, true, commonExcelHeadStyleHandler);
            Sheet sheet = new Sheet(1, 0, SysLogRowModel.class);
            sheet.setSheetName("data");
            writer.write(list, sheet);
            writer.finish();
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
