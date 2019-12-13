package com.jet.cloud.deepmind.service;

import com.jet.cloud.deepmind.entity.SysLog;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;

/**
 * Class LogService
 *
 * @package
 */
public interface LogService {

    CompletableFuture<SysLog> save(SysLog log);

    Response queryLog(QueryVO vo);

    void exportExcel(String userId, Long start, Long end, HttpServletRequest request, HttpServletResponse response);
}
