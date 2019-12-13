package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.config.AppConfig;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;
import com.jet.cloud.deepmind.repository.EquipRepo;
import com.jet.cloud.deepmind.repository.EquipSysRepo;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.service.EquipManagerService;
import com.jet.cloud.deepmind.service.SiteService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuyicheng
 * @create 2019/12/11 13:36
 * @desc 对象设备系统信息表ServiceImpl
 */
@Service
public class EquipManagerServiceImpl implements EquipManagerService {
    private static final Logger log = LoggerFactory.getLogger(EquipManagerServiceImpl.class);

    private final String ERROR_MESSAGE = "在园区/当前企业唯一";

    @Autowired
    private EquipRepo equipRepo;
    @Autowired
    private EquipSysRepo equipSysRepo;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private SiteService siteService;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private SiteRepo siteRepo;

    @Override
    public Response queryEquipSys(String objType, String objId) {
        try {
            List<EquipSys> equipSysList = equipSysRepo.findByObjTypeAndObjIdOrderBySortId(objType, objId);
            Response ok = Response.ok("查询左侧设备系统列表成功", equipSysList);
            ok.setQueryPara(objType, objId);
            return ok;
        } catch (Exception e) {
            log.error("(查询左侧设备系统列表失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("查询左侧设备系统列表失败", e);
            error.setQueryPara(objType, objId);
            return error;
        }
    }

    @Override
    public Response queryEquipSysById(Integer id) {
        try {
            EquipSys equipSys = equipSysRepo.findById(id).get();
            Response ok = Response.ok("根据id查询左侧设备系统列表成功", equipSys);
            ok.setQueryPara(id);
            return ok;
        } catch (Exception e) {
            log.error("(根据id查询左侧设备系统列表失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("根据id查询左侧设备系统列表失败", e);
            error.setQueryPara(id);
            return error;
        }
    }

    @Transactional
    @Override
    public ServiceData insertOrUpdateEquipSys(EquipSys equipSys) {
        Integer id = equipSys.getId();
        try {
            if (id == null) {
                String objId = equipSys.getObjId();
                String objType = equipSys.getObjType();
                String equipSysId = equipSys.getEquipSysId();
                EquipSys old = equipSysRepo.findByObjTypeAndObjIdAndEquipSysId(objType, objId, equipSysId);
                if (old != null) return ServiceData.error(ERROR_MESSAGE, currentUser);
                equipSys.setCreateNow();
                equipSys.setCreateUserId(currentUser.userId());
                equipSysRepo.save(equipSys);
                return ServiceData.success("新增成功", currentUser);
            } else {
                EquipSys old = equipSysRepo.findById(id).get();
                old.setObjType(equipSys.getObjType());
                old.setObjId(equipSys.getObjId());
                old.setEquipSysId(equipSys.getEquipSysId());
                old.setEquipSysName(equipSys.getEquipSysName());
                old.setSortId(equipSys.getSortId());
                old.setMemo(equipSys.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(equipSys.getMemo());
                equipSysRepo.save(old);
                return ServiceData.success("更新成功", currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Transactional
    @Override
    public ServiceData deleteEquipSys(String objType, String objId, String equipSysId) {
        try {
            equipSysRepo.deleteEquipSys(objType, objId, equipSysId);
            return ServiceData.success("删除设备系统成功", currentUser);
        } catch (Exception e) {
            log.error("删除设备系统失败,e={}", e.getMessage());
            return ServiceData.error("删除设备系统失败", e, currentUser);
        }
    }

    @Override
    public Response queryEquip(QueryVO vo) {
        Page<Equip> list = queryEquipPage(vo);
        Response ok = Response.ok(list.getContent(), list.getTotalElements());
        ok.setQueryPara(vo);
        return ok;
    }


    public Page<Equip> queryEquipPage(QueryVO vo) {
        Sort sort = new Sort(Sort.Direction.ASC, "sortId");
        Pageable pageable = vo.Pageable(sort);
        QEquip obj = QEquip.equip;
        JSONObject key = vo.getKey();
        Predicate pre = obj.isNotNull();

        String objId = key.getString("objId");
        String objType = key.getString("objType");
        String equipSysId = key.getString("equipSysId");
        String equipId = key.getString("equipId");
        String equipName = key.getString("equipName");

        pre = ExpressionUtils.and(pre, obj.objId.eq(objId));
        pre = ExpressionUtils.and(pre, obj.objType.eq(objType));
        if (StringUtils.isNotNullAndEmpty(equipId)) {
            pre = ExpressionUtils.and(pre, obj.equipId.eq(equipId));
        }
        if (StringUtils.isNotNullAndEmpty(equipName)) {
            pre = ExpressionUtils.and(pre, obj.equipName.containsIgnoreCase(equipName));
        }
        if (StringUtils.isNotNullAndEmpty(equipSysId)) {
            pre = ExpressionUtils.and(pre, obj.equipSysId.eq(equipSysId));
        }
        Page<Equip> list = equipRepo.findAll(pre, pageable);
        for (Equip equip : list) {
            EquipSys equipSys = equipSysRepo.findByObjTypeAndObjIdAndEquipSysId(objType, objId, equip.getEquipSysId());
            equip.setEquipSysName("[" + equip.getEquipSysId() + "]" + equipSys.getEquipSysName());
        }
        return list;
    }

    @Override
    public ServiceData addOrEditEquip(MultipartFile file, Equip equip) {
        try {
            Integer id = equip.getId();
            String objType = equip.getObjType();
            String objId = equip.getObjId();
            String equipId = equip.getEquipId();
            String imgSuffix = null;
            String fileName = null;
            // 图片命名规则：对象类型_对象标识_设备标识
            if (file != null) {
                if (StringUtils.isNotNullAndEmpty(file.getOriginalFilename())) {
                    imgSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    fileName = objType + "_" + objId + "_" + equipId + imgSuffix;
                }
                siteService.uploadImage(file, fileName, appConfig.getEquipImagePath());
            }
            if (id == null) {
                Equip old = equipRepo.findByObjTypeAndObjIdAndEquipId(objType, objId, equipId);
                if (old != null) return ServiceData.error(ERROR_MESSAGE, currentUser);
                equip.setImgSuffix(imgSuffix);
                equip.setCreateNow();
                equip.setCreateUserId(currentUser.userId());
                equipRepo.save(equip);
                return ServiceData.success("新增成功", currentUser);
            } else {
                Equip old = equipRepo.findById(id).get();
                old.setObjType(objType);
                old.setObjId(objId);
                old.setEquipId(equipId);
                old.setEquipName(equip.getEquipName());
                old.setEquipSysId(equip.getEquipSysId());
                old.setManufacturer(equip.getManufacturer());
                old.setModel(equip.getModel());
                old.setLocation(equip.getLocation());
                LocalDate productionDate = equip.getProductionDate();
                if (productionDate != null) {
                    old.setProductionDate(DateUtil.localDateToString(productionDate));
                } else {
                    old.setProductionDate(null);
                }
                LocalDate firstUseDate = equip.getFirstUseDate();
                if (firstUseDate != null) {
                    old.setFirstUseDate(DateUtil.localDateToString(firstUseDate));
                } else {
                    old.setFirstUseDate(null);
                }
                old.setImgSuffix(imgSuffix);
                old.setSortId(equip.getSortId());
                old.setMemo(equip.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                equipRepo.save(old);
                return ServiceData.success("更新成功", currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error("新增或更新失败", e, currentUser);
        }
    }

    @Override
    public Response queryImage(String objType, String objId, String equipId) {
        Equip equip = equipRepo.findByObjTypeAndObjIdAndEquipId(objType, objId, equipId);
        if (StringUtils.isNotNullAndEmpty(equip.getImgSuffix())) {
            try {
                String str = StringUtils.imageToBase64Str(appConfig.getEquipImagePath()
                        + equip.getObjType() + "_" + equip.getObjId() + "_" + equip.getEquipId() + equip.getImgSuffix());
                equip.setImgBase64(str);
            } catch (Exception e) {
                ;
            }
        }
        Response ok = Response.ok("查找成功", equip);
        ok.setQueryPara(objType, objId, equipId);
        return ok;
    }

    @Transactional
    @Override
    public ServiceData deleteEquip(List<EquipVO> equipVOS) {
        try {
            for (EquipVO equipVO : equipVOS) {
                equipRepo.deleteEquip(equipVO.getObjType(), equipVO.getObjId(), equipVO.getEquipId());
            }
            return ServiceData.success("删除设备成功", currentUser);
        } catch (Exception e) {
            log.error("删除设备失败,e={}", e.getMessage());
            return ServiceData.error("删除设备失败", e, currentUser);
        }
    }

    @Override
    public void exportExcel(String objType, String objId, String equipSysId, String equipId, String equipName, HttpServletResponse response, HttpServletRequest request) {
        try {
            QueryVO vo = new QueryVO();
            vo.setPage(1);
            vo.setLimit(Integer.MAX_VALUE);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("objType", objType);
            jsonObject.put("objId", objId);
            jsonObject.put("equipSysId", equipSysId);
            jsonObject.put("equipId", equipId);
            jsonObject.put("equipName", equipName);
            vo.setKey(jsonObject);
            List<Equip> equips = queryEquipPage(vo).getContent();

            ExcelWriter writer = null;
            String fileName;
            if ("PARK".equals(objType)) {
                Park park = parkRepo.findByParkId(objId);
                fileName = "[" + park.getParkId() + "]" + park.getParkName() + "_设备信息";
            } else {
                Site site = siteRepo.findBySiteId(objId);
                fileName = "[" + site.getSiteId() + "]" + site.getSiteName() + "_设备信息";
            }
            String userAgent = request.getHeader("User-Agent");
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
            sheet.setSheetName("equip");

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
            List<String> headTitle10 = new ArrayList<>();
            List<String> headTitle11 = new ArrayList<>();
            List<String> headTitle12 = new ArrayList<>();
            List<String> headTitle13 = new ArrayList<>();

            headTitle0.add("设备标识");
            headTitle1.add("设备名称");
            headTitle2.add("所属系统");
            headTitle3.add("厂家");
            headTitle4.add("型号");
            headTitle5.add("位置");
            headTitle6.add("生产日期");
            headTitle7.add("投用日期");
            headTitle8.add("排序标识");
            headTitle9.add("备注");
            headTitle10.add("创建者");
            headTitle11.add("创建时间");
            headTitle12.add("修改者");
            headTitle13.add("修改时间");

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
            headList.add(headTitle10);
            headList.add(headTitle11);
            headList.add(headTitle12);
            headList.add(headTitle13);
            sheet.setHead(headList);

            // 所有行的集合
            List<List<Object>> list = new ArrayList<>();
            for (int i = 0; i < equips.size(); i++) {
                List<Object> row = new ArrayList<>();
                Equip equip = equips.get(i);
                row.add(equip.getEquipId());
                row.add(equip.getEquipName());
                row.add(equip.getEquipSysName());
                row.add(equip.getManufacturer());
                row.add(equip.getModel());
                row.add(equip.getLocation());
                row.add(equip.getProductionDate());
                row.add(equip.getFirstUseDate());
                row.add(equip.getSortId());
                row.add(equip.getMemo());
                row.add(equip.getCreateUserId());
                LocalDateTime createTime = equip.getCreateTime();
                if (createTime != null) {
                    row.add(DateUtil.localDateTimeToString(createTime));
                } else {
                    row.add(createTime);
                }
                row.add(equip.getUpdateUserId());
                LocalDateTime updateTime = equip.getUpdateTime();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.getOutputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    @Override
    public ServiceData importExcel(MultipartFile file, String objType, String objId) {
        String message = "";
        try {
            if (StringUtils.isNullOrEmpty(file)) {
                return ServiceData.error("请选择设备文件", currentUser);
            }
            ExcelEquipModelListener modelListener = ExcelEquipModelListener.create();
            EasyExcelFactory.readBySax(file.getInputStream(), new Sheet(1, 1, EquipModel.class), modelListener);
            List<Exception> exceptionList = modelListener.getExceptionList();
            List<Equip> equipLists = modelListener.getEquipLists();
            List<String> msg = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(exceptionList)) {
                message += "excel批量导入失败";
                for (Exception exception : exceptionList) {
                    msg.add(exception.getMessage());
                }
                return new ServiceData(new Response(HttpConstants.IMPORTEXCELFAIL, msg), message, false);
            } else {
                if (equipLists.size() > 0) {
                    message += "excel批量导入成功";
                    equipRepo.deleteAllByObjTypeAndObjId(objType, objId);
                    equipRepo.flush();
                    for (Equip equip : equipLists) {
                        equip.setObjType(objType);
                        equip.setObjId(objId);
                        equip.setCreateNow();
                        equip.setCreateUserId(currentUser.userId());
                    }
                    equipRepo.saveAll(equipLists);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
        return ServiceData.success(message, currentUser);
    }

    @Override
    public void download(HttpServletResponse response) {
        try {
            //获取要下载的模板名称
            String fileName = "EquipTemplate.xlsx";
            //设置要下载的文件的名称
            response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            //获取文件的路径
            String filePath = getClass().getResource("/file/" + fileName).getPath();
            FileInputStream input = new FileInputStream(filePath);
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[2048];
            int len;
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
            response.setHeader("Content-Length", String.valueOf(input.getChannel().size()));
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("应用导入模板下载失败！");
        }
    }
}
