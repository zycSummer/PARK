package com.jet.cloud.deepmind.common.util;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.entity.ReportObjDetail;
import com.jet.cloud.deepmind.model.ReportObjDetailVos;
import com.jet.cloud.deepmind.model.Triple;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author yhy
 * @create 2019-12-03 10:21
 */
public class ExcelUtil {

    private static final String tab = "         ";

    public static Triple<HttpServletRequest, HttpServletResponse, ServletOutputStream> setExcelRespAndReqAndGetServletOutputStream(String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAgent = request.getHeader("User-Agent");
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "max-age=0");
        response.setDateHeader("Expires", 0);//设置日期头
        response.setHeader("Content-disposition", "attachment; filename=" + StringUtils.resolvingScrambling(fileName, userAgent));
        return new Triple<>(request, response, outputStream);
    }

    public static List<ReportObjDetailVos> queryTreeInfoDetails(List<ReportObjDetail> reportObjDetailList) {
        List<ReportObjDetailVos> reportObjDetailVos = new ArrayList<>();
        try {
            Map<String, String> map = new HashMap<>();
            if (reportObjDetailList != null && !reportObjDetailList.isEmpty()) {
                for (ReportObjDetail reportObjDetail : reportObjDetailList) {
                    String nodeId = reportObjDetail.getNodeId();
                    String nodeName = reportObjDetail.getNodeName();
                    map.put(nodeId, nodeName);
                }
                for (ReportObjDetail reportObjDetail : reportObjDetailList) {
                    String parentName = map.get(reportObjDetail.getParentId());
                    reportObjDetail.setParentName(parentName);
                }
                Multimap<String, ReportObjDetailVos> OrgTreeDetailMultimap = ArrayListMultimap.create();
                for (ReportObjDetail reportObjDetail : reportObjDetailList) {
                    String parentId = reportObjDetail.getParentId();
                    if (parentId == null || "".equals(parentId)) {
                        reportObjDetailVos.add(new ReportObjDetailVos(reportObjDetail, 0));
                    } else {
                        OrgTreeDetailMultimap.put(reportObjDetail.getParentId(), new ReportObjDetailVos(reportObjDetail, 0));
                    }
                }
                for (ReportObjDetailVos reportObjDetailVo : reportObjDetailVos) {
                    addChild(reportObjDetailVo, OrgTreeDetailMultimap, 10, 0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return reportObjDetailVos;
    }

    private static  <T> void addChild(T t, Multimap<String, T> dataMultimap, int size, Integer flag) {
        if (t instanceof ReportObjDetailVos) {
            ReportObjDetailVos reportObjDetailVos = (ReportObjDetailVos) t;
            if (size > 0 && reportObjDetailVos != null) {
                reportObjDetailVos.setChildren(new ArrayList<>());
                Collection<ReportObjDetailVos> objs = (Collection<ReportObjDetailVos>) dataMultimap.get(reportObjDetailVos.getNodeId());
                if (objs.size() > 0) {
                    flag = flag + 1;
                    for (ReportObjDetailVos subModel : objs) {
                        Integer deep = subModel.getDeep();
                        deep = deep + flag;
                        subModel.setDeep(deep);
                        addChild((T) subModel, dataMultimap, --size, flag);
                        reportObjDetailVos.getChildren().add(subModel);
                    }
                } else {
                    reportObjDetailVos.setChildren(null);
                }
            }
        }
    }

    public static void iter(List<ReportObjDetailVos> res, List<ReportObjDetailVos> treeInfoDetails) {
        if (treeInfoDetails != null && treeInfoDetails.size() > 0) {
            for (ReportObjDetailVos treeInfoDetail : treeInfoDetails) {
                res.add(new ReportObjDetailVos(treeInfoDetail));
                iter(res, treeInfoDetail.getChildren());
            }
        }
    }

    public static String setName(String nodeName, Integer deep) {
        if (deep == null || deep == 0) {
            return nodeName;
        }
        String prefix = "";
        for (Integer i = 0; i < deep; i++) {
            prefix += tab;
        }
        return prefix + nodeName;
    }

}
