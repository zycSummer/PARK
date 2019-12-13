package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.jet.cloud.deepmind.common.util.DateUtil;
import com.jet.cloud.deepmind.common.util.StringUtils;
import com.jet.cloud.deepmind.entity.*;
import com.jet.cloud.deepmind.model.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/***
 *  监听器
 */
public class ExcelEquipModelListener extends AnalysisEventListener {
    private Set<EquipModel> equipModels = new LinkedHashSet<>();
    private List<Equip> equipLists;
    private List<Exception> exceptionList;

    public static ExcelEquipModelListener create() {
        return new ExcelEquipModelListener();
    }


    public ExcelEquipModelListener() {
    }

    public List<Equip> getEquipLists() {
        return equipLists;
    }

    public void setEquipLists(List<Equip> equipLists) {
        this.equipLists = equipLists;
    }

    public List<Exception> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(Exception e) {
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        }
        this.exceptionList.add(e);
    }

    /**
     * 解析监听器，每解析一行会回调invoke()方法。整个excel解析结束会执行doAfterAllAnalysed()方法
     *
     * @param o
     * @param analysisContext
     */
    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        equipModels.add((EquipModel) o);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        int sheetNo = context.getCurrentSheet().getSheetNo();
        setEquipLists(turnEquipDetail(sheetNo));
    }

    private List<Equip> turnEquipDetail(int sheetNo) {
        ArrayList<Equip> list = new ArrayList<>();
        int countRow = 1;
        for (EquipModel equipModel : equipModels) {
            countRow++;
            try {
                Equip equip = new Equip();
                String equipId = equipModel.getEquipId();
                if (StringUtils.isNotNullAndEmpty(equipId)) {
                    if (equipId.matches("^[A-Za-z0-9]{0,20}$")) {
                        equip.setEquipId(equipId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的equipId只能输入字母和数字且不能大于20位");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的equipId不能为空");
                }
                String equipName = equipModel.getEquipName();
                if (StringUtils.isNotNullAndEmpty(equipName)) {
                    if (equipName.length() <= 30) {
                        equip.setEquipName(equipName);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的equipName长度不超过30");
                    }
                } else {
                    throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的equipName不能为空");
                }

                String equipSysId = equipModel.getEquipSysId();
                if (StringUtils.isNullOrEmpty(equipSysId)) {
                    if (equipSysId.matches("^[A-Za-z0-9]{0,20}$")) {
                        equip.setEquipSysId(equipSysId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的equipSysId只能输入字母和数字且不能大于20位");
                    }
                } else {
                    equip.setEquipSysId(equipSysId);
                }

                String manufacturer = equipModel.getManufacturer();
                equip.setManufacturer(manufacturer);

                String model = equipModel.getModel();
                equip.setModel(model);

                String location = equipModel.getLocation();
                equip.setLocation(location);

                String productionDate = equipModel.getProductionDate();
                equip.setProductionDate(productionDate);

                String firstUseDate = equipModel.getFirstUseDate();
                equip.setFirstUseDate(firstUseDate);

                String sortId = equipModel.getSortId();
                if (sortId != null) {
                    if (sortId.matches("^[A-Za-z0-9]{0,10}$")) {
                        equip.setSortId(sortId);
                    } else {
                        throw new Exception("第【" + sheetNo + "】张表的第【" + countRow + "】行的sortId只能输入字母和数字且不能大于10位");
                    }
                } else {
                    equip.setSortId(sortId);
                }
                equip.setMemo(equip.getMemo());
                list.add(equip);
            } catch (Exception e) {
                e.printStackTrace();
                setExceptionList(e);
            }
        }
        return list;
    }
}