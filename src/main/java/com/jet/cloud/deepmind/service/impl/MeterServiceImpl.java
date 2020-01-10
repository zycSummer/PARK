package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.MeterModel;
import com.jet.cloud.deepmind.model.QueryVO;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.MeterRepo;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.repository.SysEnergyTypeRepo;
import com.jet.cloud.deepmind.service.MeterService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yhy
 * @create 2019-11-13 14:29
 */
@Service
public class MeterServiceImpl implements MeterService {

    @Autowired
    private MeterRepo meterRepo;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private SysEnergyTypeRepo sysEnergyTypeRepo;

    @Autowired
    private ParkRepo parkRepo;

    @Autowired
    private SiteRepo siteRepo;

    @Override
    public Response getAllMeterBySiteResp(String objType, String objId, String key) {
        try {
            Response ok = Response.ok(getAllMeterBySite(objType, objId, key));
            ok.setQueryPara(objId, objType, key);
            return ok;
        } catch (Exception e) {
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objId, objType, key);
            return error;
        }
    }

    private List<Meter> getAllMeterBySite(String objType, String objId, String key) {

        if (StringUtils.isNullOrEmpty(key)) {
            return meterRepo.findAllByObjTypeAndObjIdOrderBySortIdAsc(objType, objId);
        } else {
            return meterRepo.findAllByObjTypeAndObjIdAndKeyOrderBySortIdAsc(objType, objId, "%" + key + "%");
        }

    }

    @Override
    public Response getCurrentSiteByEnergyTypeId(String objType, String objId, String energyTypeId, String key) {
        try {
            Response ok = Response.ok(getAllMeterByEnergyTypeId(objType, objId, energyTypeId, key));
            ok.setQueryPara(objId, objType, energyTypeId, key);
            return ok;
        } catch (Exception e) {
            Response error = Response.error(e.getMessage());
            error.setQueryPara(objId, objType, energyTypeId, key);
            return error;
        }
    }

    private List<Meter> getAllMeterByEnergyTypeId(String objType, String objId, String energyTypeId, String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return meterRepo.findAllByObjTypeAndObjIdAndEnergyTypeIdOrderBySortId(objType, objId, energyTypeId);
        } else {
            return meterRepo.findAllByObjTypeAndObjIdAndEnergyTypeIdAndKeyOrderBySortIdAsc(objType, objId, energyTypeId, "%" + key + "%");
        }
    }

    @Override
    public Response queryMeter(QueryVO vo) {
        Sort sort = new Sort(Sort.Direction.ASC, "sortId");
        Pageable pageable = vo.Pageable(sort);
        QMeter obj = QMeter.meter;
        Predicate pre = obj.isNotNull();
        JSONObject key = vo.getKey();
        String objType = key.getString("objType");
        String objId = key.getString("objId");
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        String meterId = key.getString("meterId");
        if (StringUtils.isNotNullAndEmpty(meterId)) {
            pre = ExpressionUtils.and(pre, obj.meterId.containsIgnoreCase(meterId));
        }
        String meterName = key.getString("meterName");
        if (StringUtils.isNotNullAndEmpty(meterName)) {
            pre = ExpressionUtils.and(pre, obj.meterName.containsIgnoreCase(meterName));
        }
        JSONArray energyTypeId = key.getJSONArray("energyTypeId");
        if (StringUtils.isNotNullAndEmpty(energyTypeId)) {
            pre = ExpressionUtils.and(pre, obj.energyTypeId.in(energyTypeId.toJavaList(String.class)));
        }
        Page<Meter> sitePage = meterRepo.findAll(pre, pageable);
        Response ok = Response.ok(sitePage.getContent(), sitePage.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }

    @Override
    public Response queryMeterById(Integer id) {
        Meter meter = meterRepo.findById(id).get();
        Response ok = Response.ok("查询仪表成功", meter);
        ok.setQueryPara(id);
        return ok;
    }

    @Transactional
    @Override
    public ServiceData addOrEditMeter(Meter meter) {
        try {
            String meterId = meter.getMeterId();
            String objType = meter.getObjType();
            String objId = meter.getObjId();
            Meter old = meterRepo.findByObjTypeAndObjIdAndMeterIdOrderBySortId(objType, objId, meterId);
            if (meter.getId() == null) {
                if (old != null) {
                    return ServiceData.error("新增的仪表对象已存在", currentUser);
                }
                meter.setCreateNow();
                meter.setCreateUserId(currentUser.userId());
                meterRepo.save(meter);
            } else {
                old.setMeterName(meter.getMeterName());
                old.setEnergyTypeId(meter.getEnergyTypeId());
                old.setSortId(meter.getSortId());
                old.setIsRanking(meter.getIsRanking());
                old.setMemo(meter.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                meterRepo.save(old);
            }
            return ServiceData.success("新增或更新成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Override
    public ServiceData delete(List<Integer> idList) {
        try {
            meterRepo.deleteAllByIdIn(idList);
            return ServiceData.success("删除成功", currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("删除失败", e, currentUser);
        }
    }

    @Override
    public void exportExcel(String objType, String objId, String meterId, String meterName, JSONArray energyTypeId, HttpServletResponse response, HttpServletRequest request) {
        ExcelWriter writer = null;
        String project = null;
        try {
            if (objType.equals("PARK")) {
                Park park = parkRepo.findByParkId(objId);
                project = "[" + park.getParkId() + "]" + park.getParkName();
            } else {
                Site site = siteRepo.findBySiteId(objId);
                project = "[" + site.getSiteId() + "]" + site.getSiteName();
            }
            String userAgent = request.getHeader("User-Agent");
            String fileName = project + "_仪表信息";
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Content-Disposition", "attachment; filename=" + StringUtils.resolvingScrambling(fileName, userAgent) + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            response.setDateHeader("Expires", 0);//设置日期头
            //实例化 ExcelWriter
            writer = EasyExcelFactory.getWriterWithTempAndHandler(null, outputStream, ExcelTypeEnum.XLSX, true, new AfterWriteHandlerImpl());
            Sheet sheet = new Sheet(1, 0);
            Table table = new Table(1);
            sheet.setSheetName("meter");
            // 动态添加 表头 headList --> 所有表头行集合
            List<List<String>> headList = new ArrayList<>();
            List<String> headTitle0 = new ArrayList<>();
            List<String> headTitle1 = new ArrayList<>();
            List<String> headTitle2 = new ArrayList<>();
            List<String> headTitle3 = new ArrayList<>();
            List<String> headTitle4 = new ArrayList<>();
            List<String> headTitle5 = new ArrayList<>();
            List<String> headTitle6 = new ArrayList<>();
            List<String> headTitle7 = new ArrayList<>();
            List<String> headTitle8 = new ArrayList<>();
            List<String> headTitle9 = new ArrayList<>();
            headTitle0.add("仪表标识");
            headTitle1.add("仪表名称");
            headTitle2.add("能源种类");
            headTitle3.add("排序标识");
            headTitle4.add("是否参与负荷排名");
            headTitle5.add("备注");
            headTitle6.add("创建者");
            headTitle7.add("创建时间");
            headTitle8.add("修改者");
            headTitle9.add("修改时间");
            headList.add(headTitle0);
            headList.add(headTitle1);
            headList.add(headTitle2);
            headList.add(headTitle3);
            headList.add(headTitle4);
            headList.add(headTitle5);
            headList.add(headTitle6);
            headList.add(headTitle7);
            headList.add(headTitle8);
            headList.add(headTitle9);
            sheet.setHead(headList);
            // 所有行的集合
            List<SysEnergyType> all = sysEnergyTypeRepo.findAll();
            Map<String, String> map = new HashMap<>();
            for (SysEnergyType sysEnergyType : all) {
                map.put(sysEnergyType.getEnergyTypeId(), sysEnergyType.getEnergyTypeName());
            }
            List<Meter> value = query(objType, objId, meterId, meterName, energyTypeId);
            List<List<Object>> list = new ArrayList<>();
            for (int i = 0; i < value.size(); i++) {
                List<Object> row = new ArrayList<>();
                Meter meter = value.get(i);
                row.add(meter.getMeterId());
                row.add(meter.getMeterName());
                row.add(map.get(meter.getEnergyTypeId()));
                row.add(meter.getSortId());
                Boolean isRanking = meter.getIsRanking();
                if (isRanking) {
                    row.add("是");
                } else {
                    row.add("否");
                }
                row.add(meter.getMemo());
                row.add(meter.getCreateUserId());
                LocalDateTime createTime = meter.getCreateTime();
                if (createTime != null) {
                    row.add(DateUtil.localDateTimeToString(createTime));
                } else {
                    row.add(createTime);
                }
                row.add(meter.getUpdateUserId());
                LocalDateTime updateTime = meter.getUpdateTime();
                if (updateTime != null) {
                    row.add(DateUtil.localDateTimeToString(updateTime));
                } else {
                    row.add(updateTime);
                }
                list.add(row);
            }
            writer.write1(list, sheet, table);
            writer.finish();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Meter> query(String objType, String objId, String meterId, String meterName, JSONArray energyTypeId) {
        QMeter obj = QMeter.meter;
        Predicate pre = obj.isNotNull();
        pre = ExpressionUtils.and(pre, obj.objId.containsIgnoreCase(objId));
        pre = ExpressionUtils.and(pre, obj.objType.containsIgnoreCase(objType));
        if (StringUtils.isNotNullAndEmpty(meterId)) {
            pre = ExpressionUtils.and(pre, obj.meterId.containsIgnoreCase(meterId));
        }
        if (StringUtils.isNotNullAndEmpty(meterName)) {
            pre = ExpressionUtils.and(pre, obj.meterName.containsIgnoreCase(meterName));
        }
        if (StringUtils.isNotNullAndEmpty(energyTypeId)) {
            pre = ExpressionUtils.and(pre, obj.energyTypeId.in(energyTypeId.toJavaList(String.class)));
        }
        List<Meter> all = (List<Meter>) meterRepo.findAll(pre);
        return all;
    }

    @Override
    public ServiceData importExcel(MultipartFile file, String objType, String objId) {
        String message = "";
        try {
            if (StringUtils.isNullOrEmpty(file)) {
                return ServiceData.error("请选择仪表文件", currentUser);
            }
            List<SysEnergyType> energyTypeList = sysEnergyTypeRepo.findAll();
            ArrayList<String> energyTypeIds = new ArrayList<>();
            for (SysEnergyType sysEnergyType : energyTypeList) {
                String energyTypeId = sysEnergyType.getEnergyTypeId();
                energyTypeIds.add(energyTypeId);
            }
            ExcelModelListener modelListener = ExcelModelListener.create(energyTypeIds);
            EasyExcelFactory.readBySax(file.getInputStream(), new Sheet(1, 1, MeterModel.class), modelListener);
            List<Meter> meterLists = modelListener.getMeterLists();
            List<Exception> exceptionList = modelListener.getExceptionList();
            List<String> msg = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(exceptionList)) {
                message += "excel批量导入失败";
                for (Exception exception : exceptionList) {
                    msg.add(exception.getMessage());
                }
                return new ServiceData(new Response(HttpConstants.IMPORTEXCELFAIL, msg), message, false);
            } else {
                if (meterLists.size() > 0) {
                    message += "excel批量导入成功";
                    meterRepo.deleteAllByObjTypeAndObjId(objType, objId);
                    meterRepo.flush();
                    for (Meter meterList : meterLists) {
                        meterList.setObjType(objType);
                        meterList.setObjId(objId);
                        meterList.setCreateNow();
                        meterList.setCreateUserId(currentUser.userId());
                    }
                    meterRepo.saveAll(meterLists);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
        return ServiceData.success(message, currentUser);
    }

}




