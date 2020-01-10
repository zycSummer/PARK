package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jet.cloud.deepmind.common.CurrentUser;
import com.jet.cloud.deepmind.common.HttpConstants;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StreamUtils;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.OrgTreeDetailModel;
import com.jet.cloud.deepmind.model.OrgTreeDetailVOs;
import com.jet.cloud.deepmind.model.Response;
import com.jet.cloud.deepmind.model.ServiceData;
import com.jet.cloud.deepmind.repository.OrgTreeDetailRepo;
import com.jet.cloud.deepmind.repository.OrgTreeRepo;
import com.jet.cloud.deepmind.repository.ParkRepo;
import com.jet.cloud.deepmind.repository.SiteRepo;
import com.jet.cloud.deepmind.service.CommonService;
import com.jet.cloud.deepmind.service.TreeService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.awt.Color;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import static org.apache.poi.ss.usermodel.IndexedColors.BLACK;
import static org.apache.poi.ss.usermodel.IndexedColors.WHITE;

/**
 * @author zhuyicheng
 * @create 2019/11/10 13:17
 * @desc 结构树ServiceImpl
 */
@Service
public class TreeServiceImpl implements TreeService {
    private static final Logger logger = LoggerFactory.getLogger(TreeServiceImpl.class);

    @Autowired
    private OrgTreeRepo orgTreeRepo;
    @Autowired
    private OrgTreeDetailRepo orgTreeDetailRepo;
    @Autowired
    private CommonService commonService;
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private ParkRepo parkRepo;
    @Autowired
    private SiteRepo siteRepo;

    private final Color COLOR_BLUE = new Color(53, 111, 189);
    private final Color COLOR_YELLOW = new Color(255, 255, 0);
    private static final String tab = "        ";

    @Override
    public Response queryLeftNavigation(String objType, String objId, String orgTreeName) {
        try {
            List<SysEnergyType> sysEnergyTypes = commonService.queryEnergyTypesAll().getData();
            Map<String, String> map = new HashMap<>();
            if (StringUtils.isNotNullAndEmpty(sysEnergyTypes)) {
                for (SysEnergyType sysEnergyType : sysEnergyTypes) {
                    map.put(sysEnergyType.getEnergyTypeId(), sysEnergyType.getEnergyTypeName());
                }
            }
            List<OrgTree> orgTrees;
            if (orgTreeName == null) {
                orgTrees = orgTreeRepo.findByObjTypeAndObjIdOrderBySortId(objType, objId);
            } else {
                orgTrees = orgTreeRepo.findByObjTypeAndObjIdAndOrgTreeNameLikeOrderBySortId(objType, objId, "%" + orgTreeName + "%");
            }
            if (StringUtils.isNotNullAndEmpty(orgTrees)) {
                for (OrgTree orgTree : orgTrees) {
                    String energyTypeId = orgTree.getEnergyTypeId();
                    String energyTypeName = map.get(energyTypeId);
                    orgTree.setEnergyTypeName(energyTypeName);
                }
            }
            Response ok = Response.ok("查询成功", orgTrees);
            ok.setQueryPara(objId, objType, orgTreeName);
            return ok;
        } catch (Exception e) {
            logger.error("左侧导航栏查询失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("左侧导航栏查询失败", e);
            error.setQueryPara(objId, objType, orgTreeName);
            return error;
        }
    }

    @Transactional
    @Override
    public ServiceData startOrOver(String objType, String objId, String orgTreeId, Boolean isUse) {
        try {
            orgTreeRepo.updateIsuse(objType, objId, orgTreeId, isUse);
        } catch (Exception e) {
            logger.error("更新左侧导航栏查询失败,e={}", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("更新左侧导航栏查询失败", e, currentUser);
        }
        return ServiceData.success("更新左侧导航栏查询成功", currentUser);
    }

    @Transactional
    @Override
    public ServiceData insertOrUpdateOrgTree(OrgTree orgTree) {
        try {
            Integer id = orgTree.getId();
            String objType = orgTree.getObjType();
            String objId = orgTree.getObjId();
            String orgTreeId = orgTree.getOrgTreeId();
            OrgTree old = orgTreeRepo.findByObjTypeAndObjIdAndOrgTreeId(objType, objId, orgTreeId);
            if (id == null) {
                if (old != null) return ServiceData.error("展示结构树标识已存在！", currentUser);
                orgTree.setCreateNow();
                orgTree.setCreateUserId(currentUser.userId());
                orgTreeRepo.save(orgTree);
            } else {
                old.setObjType(objType);
                old.setObjId(objId);
                old.setOrgTreeId(orgTreeId);
                old.setEnergyTypeId(orgTree.getEnergyTypeId());
                old.setOrgTreeName(orgTree.getOrgTreeName());
                old.setIsUse(orgTree.getIsUse());
                old.setSortId(orgTree.getSortId());
                old.setMemo(orgTree.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                orgTreeRepo.save(old);
            }
        } catch (Exception e) {
            logger.error("同步左侧导航栏查询失败", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("同步左侧导航栏查询失败", e, currentUser);
        }
        return ServiceData.success("同步左侧导航栏查询成功", currentUser);
    }

    @Transactional
    @Override
    public ServiceData deleteOrgTree(String objType, String objId, String orgTreeId) {
        try {
            orgTreeRepo.deleteByObjTypeAndObjIdAndOrgTreeId(objType, objId, orgTreeId);
            orgTreeDetailRepo.deleteByObjTypeAndObjIdAndOrgTreeId(objType, objId, orgTreeId);
        } catch (Exception e) {
            logger.error("删除左侧导航栏查询失败", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("删除左侧导航栏查询失败", e, currentUser);
        }
        return ServiceData.success("删除左侧导航栏查询成功", currentUser);
    }

    @Override
    public Response queryLeftNavigationById(Integer id) {
        OrgTree orgTree = orgTreeRepo.findById(id).get();
        Response ok = Response.ok("查询成功", orgTree);
        ok.setQueryPara(id);
        return ok;
    }

    @Override
    public List<OrgTreeDetailVOs> queryTreeInfoDetails(String objType, String objId, String orgTreeId) {
        List<OrgTreeDetailVOs> orgTreeDetailVOS = new ArrayList<>();
        try {
            List<OrgTreeDetail> orgTreeDetails = orgTreeDetailRepo.findByObjTypeAndObjIdAndOrgTreeIdOrderBySortId(objType, objId, orgTreeId);
            Map<String, String> map = new HashMap<>();
            if (orgTreeDetails != null && !orgTreeDetails.isEmpty()) {
                for (OrgTreeDetail orgTreeDetail : orgTreeDetails) {
                    String nodeId = orgTreeDetail.getNodeId();
                    String nodeName = orgTreeDetail.getNodeName();
                    map.put(nodeId, nodeName);
                }
                for (OrgTreeDetail orgTreeDetail : orgTreeDetails) {
                    String parentName = map.get(orgTreeDetail.getParentId());
                    orgTreeDetail.setParentName(parentName);
                }
                Multimap<String, OrgTreeDetailVOs> OrgTreeDetailMultimap = ArrayListMultimap.create();
                for (OrgTreeDetail orgTreeDetail : orgTreeDetails) {
                    String parentId = orgTreeDetail.getParentId();
                    if (parentId == null || "".equals(parentId)) {
                        orgTreeDetailVOS.add(new OrgTreeDetailVOs(orgTreeDetail, 0));
                    } else {
                        OrgTreeDetailMultimap.put(orgTreeDetail.getParentId(), new OrgTreeDetailVOs(orgTreeDetail, 0));
                    }
                }
                for (OrgTreeDetailVOs orgTreeDetailVO : orgTreeDetailVOS) {
                    addChild(orgTreeDetailVO, OrgTreeDetailMultimap, 10, 0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return orgTreeDetailVOS;
    }


    @Override
    public Response queryTreeInfoDetail(String objType, String objId, String orgTreeId) {
        try {
            List<OrgTreeDetailVOs> orgTreeDetailVOs = queryTreeInfoDetails(objType, objId, orgTreeId);
            Response ok = Response.ok("查询成功", orgTreeDetailVOs);
            ok.setQueryPara(objId, objType, orgTreeId);
            return ok;
        } catch (Exception e) {
            logger.error("展示结构树的明细信息失败,e={}", e.getMessage());
            e.printStackTrace();
            Response error = Response.error("展示结构树的明细信息失败", e);
            error.setQueryPara(objId, objType, orgTreeId);
            return error;
        }
    }

    @Transactional
    @Override
    public ServiceData insertOrUpdateOrgTreeDetail(OrgTreeDetail orgTreeDetail) {
        try {
            Integer id = orgTreeDetail.getId();
            if (id == null) {
                orgTreeDetail.setCreateNow();
                orgTreeDetail.setCreateUserId(currentUser.userId());
                orgTreeDetailRepo.save(orgTreeDetail);
            } else {
                OrgTreeDetail old = orgTreeDetailRepo.findById(id).get();
                old.setObjType(orgTreeDetail.getObjType());
                old.setObjId(orgTreeDetail.getObjId());
                old.setOrgTreeId(orgTreeDetail.getOrgTreeId());
                old.setNodeId(orgTreeDetail.getNodeId());
                old.setNodeName(orgTreeDetail.getNodeName());
                old.setParentId(orgTreeDetail.getParentId());
                old.setSortId(orgTreeDetail.getSortId());
                old.setDataSource(orgTreeDetail.getDataSource());
                old.setMemo(orgTreeDetail.getMemo());
                old.setUpdateNow();
                old.setUpdateUserId(currentUser.userId());
                orgTreeDetailRepo.save(old);
            }
            return ServiceData.success("同步成功", currentUser);
        } catch (Exception e) {
            logger.error("同步失败", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("同步失败", e, currentUser);
        }
    }

    @Transactional
    @Override
    public ServiceData deleteOrgTreeDetail(String objType, String objId, String orgTreeId, String nodeId) {
        try {
            orgTreeDetailRepo.deleteByObjTypeAndObjIdAndOrgTreeIdAndNodeId(objType, objId, orgTreeId, nodeId);
        } catch (Exception e) {
            logger.error("删除展示结构树明细信息失败", e.getMessage());
            e.printStackTrace();
            return ServiceData.error("删除展示结构树明细信息失败", e, currentUser);
        }
        return ServiceData.success("删除展示结构树明细信息成功", currentUser);
    }

    @Override
    public void exportExcel(String objType, String objId, String orgTreeId, String energyTypeName, HttpServletResponse response, String userAgent) throws Exception {
        List<OrgTreeDetailVOs> orgTreeDetailVOs = queryTreeInfoDetails(objType, objId, orgTreeId);
        response.setContentType("application/msexcel;charset=UTF-8");

        OrgTree orgTree = orgTreeRepo.findByObjTypeAndObjIdAndOrgTreeId(objType, objId, orgTreeId);
        String title;

        if ("PARK".equals(objType)) {
            Park park = parkRepo.findByParkId(objId);
            title = "[" + park.getParkId() + "]" + park.getParkName() + "_" + "[" + energyTypeName + "]" + "[" + orgTreeId + "]" + "[" + orgTree.getOrgTreeName() + "]";
        } else {
            Site site = siteRepo.findBySiteId(objId);
            title = "[" + site.getSiteId() + "]" + site.getSiteName() + "_" + "[" + energyTypeName + "]" + "[" + orgTreeId + "]" + "[" + orgTree.getOrgTreeName() + "]";
        }
        String fileName = StringUtils.resolvingScrambling(title + ".xlsx", userAgent);

        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        int maxColumn = setExcelHeaderData(workbook, sheet, title);

        if (orgTreeDetailVOs == null || orgTreeDetailVOs.size() == 0) {
            //无数据导出
            try {
                workbook.write(response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            StreamUtils.closeWorkbook(workbook);
        }
        int rowIndex = 1;
        setExcelData(rowIndex, orgTreeDetailVOs, sheet, workbook);
        sheet.setDisplayGridlines(false);
        try {
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        StreamUtils.closeWorkbook(workbook);
    }

    private void setExcelData(int rowIndex, List<OrgTreeDetailVOs> orgTreeDetailVOs, Sheet sheet, Workbook workbook) {
        List<OrgTreeDetailVOs> res = new ArrayList<>();
        CellStyle colStyle = getCellStyle2(workbook, BLACK);
        iter(res, orgTreeDetailVOs);
        for (OrgTreeDetailVOs vo : res) {
            vo.setNodeName(setName(vo.getNodeName(), vo.getDeep()));
        }
        for (OrgTreeDetailVOs pojo : res) {
            Row row = sheet.createRow(rowIndex);
            Cell cell0 = row.createCell(0, CellType.STRING);
            cell0.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getNodeName()) ? "" : pojo.getNodeName());
            cell0.setCellStyle(colStyle);

            Cell cell1 = row.createCell(1, CellType.STRING);
            cell1.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getNodeId()) ? "" : pojo.getNodeId());
            cell1.setCellStyle(colStyle);

            Cell cell2 = row.createCell(2, CellType.STRING);
            cell2.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getParentId()) ? "" : pojo.getParentId());
            cell2.setCellStyle(colStyle);

            Cell cell3 = row.createCell(3, CellType.STRING);
            cell3.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getSortId()) ? "" : pojo.getSortId());
            cell3.setCellStyle(colStyle);

            Cell cell4 = row.createCell(4, CellType.STRING);
            cell4.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getDataSource()) ? "" : pojo.getDataSource());
            cell4.setCellStyle(colStyle);

            Cell cell5 = row.createCell(5, CellType.STRING);
            cell5.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getMemo()) ? "" : pojo.getMemo());
            cell5.setCellStyle(colStyle);

            Cell cell6 = row.createCell(6, CellType.STRING);
            cell6.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getCreateUserId()) ? "" : pojo.getCreateUserId());
            cell6.setCellStyle(colStyle);

            LocalDateTime createTime = pojo.getCreateTime();
            Cell cell7;
            if (createTime != null) {
                cell7 = row.createCell(7, CellType.STRING);
                cell7.setCellValue(org.springframework.util.StringUtils.isEmpty(DateUtil.localDateTimeToString(pojo.getCreateTime())) ? "" : DateUtil.localDateTimeToString(pojo.getCreateTime()));
            } else {
                cell7 = row.createCell(7, CellType.STRING);
                cell7.setCellValue("");
            }
            cell7.setCellStyle(colStyle);

            Cell cell8 = row.createCell(8, CellType.STRING);
            cell8.setCellValue(org.springframework.util.StringUtils.isEmpty(pojo.getUpdateUserId()) ? "" : pojo.getUpdateUserId());
            cell8.setCellStyle(colStyle);

            Cell cell9;
            LocalDateTime updateTime = pojo.getUpdateTime();
            if (updateTime != null) {
                cell9 = row.createCell(9, CellType.STRING);
                cell9.setCellValue(org.springframework.util.StringUtils.isEmpty(DateUtil.localDateTimeToString(pojo.getUpdateTime())) ? "" : DateUtil.localDateTimeToString(pojo.getCreateTime()));
            } else {
                cell9 = row.createCell(9, CellType.STRING);
                cell9.setCellValue("");
            }
            cell9.setCellStyle(colStyle);
            rowIndex++;
        }
    }

    private String setName(String nodeName, Integer deep) {
        if (deep == null || deep == 0) return nodeName;
        String prefix = "";
        for (Integer i = 0; i < deep; i++) {
            prefix += tab;
        }
        return prefix + nodeName;
    }

    private void iter(List<OrgTreeDetailVOs> res, List<OrgTreeDetailVOs> orgTreeDetailVOs) {
        if (orgTreeDetailVOs != null && orgTreeDetailVOs.size() > 0) {
            for (OrgTreeDetailVOs pojo : orgTreeDetailVOs) {
                res.add(new OrgTreeDetailVOs(pojo));
                iter(res, pojo.getChildren());
            }
        }
    }

    private int setExcelHeaderData(Workbook workbook, Sheet sheet, String fileName) {
        // 合并单元格(4个参数，分别为起始行，结束行，起始列，结束列)
        // 行和列都是从0开始计数，且起始结束都会合并
     /*   CellRangeAddress region = new CellRangeAddress(0, 0, 0, 10);
        sheet.addMergedRegion(region);*/
        sheet.createFreezePane(0, 1, 0, 1);
        CellStyle colStyle = getCellStyle(workbook, COLOR_YELLOW, BLACK);
        Row row = sheet.createRow(0);

        Cell col1 = row.createCell(0);
        col1.setCellValue("节点名称");
        col1.setCellStyle(colStyle);

        Cell col2 = row.createCell(1);
        col2.setCellValue("节点ID");
        col2.setCellStyle(colStyle);

        Cell col3 = row.createCell(2);
        col3.setCellValue("父节点ID");
        col3.setCellStyle(colStyle);

        Cell col4 = row.createCell(3);
        col4.setCellValue("排序");
        col4.setCellStyle(colStyle);

        Cell col5 = row.createCell(4);
        col5.setCellValue("数据源");
        col5.setCellStyle(colStyle);

        Cell col6 = row.createCell(5);
        col6.setCellValue("备注");
        col6.setCellStyle(colStyle);

        Cell col7 = row.createCell(6);
        col7.setCellValue("创建者");
        col7.setCellStyle(colStyle);

        Cell col8 = row.createCell(7);
        col8.setCellValue("创建时间");
        col8.setCellStyle(colStyle);

        Cell col9 = row.createCell(8);
        col9.setCellValue("修改者");
        col9.setCellStyle(colStyle);

        Cell col10 = row.createCell(9);
        col10.setCellValue("修改时间");
        col10.setCellStyle(colStyle);

       /* Row title = sheet.createRow(0);
        Cell titleCell = title.createCell(0, CellType.STRING);
        titleCell.setCellValue(fileName);
        titleCell.setCellStyle(getCellStyle(workbook, COLOR_YELLOW, BLACK));*/
        return 10;
    }

    /**
     * 设置单元格颜色
     *
     * @param workbook
     * @param color
     * @return
     */
    private CellStyle getCellStyle(Workbook workbook, Color color, IndexedColors fontColor) {
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        titleStyle.setFillForegroundColor(new XSSFColor(color));//设置背景色
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//背景色填充模式为全填充模式
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置底边框;
        titleStyle.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        titleStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        //设置左边框;
        titleStyle.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        titleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        //设置右边框;
        titleStyle.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        titleStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //设置顶边框;
        titleStyle.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        titleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        Font font = workbook.createFont();
        font.setColor(fontColor.getIndex());
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(font);
        return titleStyle;
    }

    /**
     * 设置单元格颜色
     *
     * @param workbook
     * @param color
     * @return
     */
    private CellStyle getCellStyle2(Workbook workbook, IndexedColors fontColor) {
        XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
        //设置底边框;
        titleStyle.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        titleStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        //设置左边框;
        titleStyle.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        titleStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        //设置右边框;
        titleStyle.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        titleStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //设置顶边框;
        titleStyle.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        titleStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        Font font = workbook.createFont();
        font.setColor(fontColor.getIndex());
        titleStyle.setFont(font);
        return titleStyle;
    }


    @Transactional
    @Override
    public ServiceData importExcel(MultipartFile file, String objType, String objId, String orgTreeId) {
        String message = "";
        try {
            ExcelModelListener modelListener = ExcelModelListener.create();
            EasyExcelFactory.readBySax(file.getInputStream(), new com.alibaba.excel.metadata.Sheet(1, 1, OrgTreeDetailModel.class), modelListener);
            List<OrgTreeDetail> orgTreeDetails = modelListener.getOrgTreeDetails();
            List<Exception> exceptionList = modelListener.getExceptionList();
            List<String> msg = new ArrayList<>();
            if (StringUtils.isNotNullAndEmpty(exceptionList)) {
                message += "excel批量导入失败";
                for (Exception exception : exceptionList) {
                    msg.add(exception.getMessage());
                }
                return new ServiceData(new Response(HttpConstants.IMPORTEXCELFAIL, msg), message, false);
            } else {
                if (orgTreeDetails.size() > 0) {
                    message += "excel批量导入成功";
                    orgTreeDetailRepo.deleteByObjTypeAndObjIdAndOrgTreeId(objType, objId, orgTreeId);
                    orgTreeDetailRepo.flush();
                    for (OrgTreeDetail orgTreeDetail : orgTreeDetails) {
                        orgTreeDetail.setObjType(objType);
                        orgTreeDetail.setObjId(objId);
                        orgTreeDetail.setOrgTreeId(orgTreeId);
                        orgTreeDetail.setCreateNow();
                        orgTreeDetail.setCreateUserId(currentUser.userId());
                    }
                    orgTreeDetailRepo.saveAll(orgTreeDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceData.error(e.getMessage(), currentUser);
        }
        return ServiceData.success(message, currentUser);
    }

    public <T> void addChild(T t, Multimap<String, T> dataMultimap, int size, Integer flag) {
        if (t instanceof OrgTreeDetailVOs) {
            OrgTreeDetailVOs orgTreeDetailVOs = (OrgTreeDetailVOs) t;
            if (size > 0 && orgTreeDetailVOs != null) {
                orgTreeDetailVOs.setChildren(new ArrayList<>());
                Collection<OrgTreeDetailVOs> objs = (Collection<OrgTreeDetailVOs>) dataMultimap.get(orgTreeDetailVOs.getNodeId());
                if (objs.size() > 0) {
                    flag = flag + 1;
                    for (OrgTreeDetailVOs subModel : objs) {
                        Integer deep = subModel.getDeep();
                        deep = deep + flag;
                        subModel.setDeep(deep);
                        addChild((T) subModel, dataMultimap, --size, flag);
                        orgTreeDetailVOs.getChildren().add(subModel);
                    }
                } else {
                    orgTreeDetailVOs.setChildren(null);
                }
            }
        }
    }
}
