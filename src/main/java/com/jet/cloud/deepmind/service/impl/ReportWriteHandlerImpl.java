package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.event.WriteHandler;
import com.jet.cloud.deepmind.common.util.PoiUtils;
import org.apache.poi.ss.usermodel.*;

/**
 * @author maohandong
 * @create 2019/11/27 14:02
 */
public class ReportWriteHandlerImpl implements WriteHandler {
    CellStyle columnTopStyle;//表头单元格样式
    CellStyle style;//列数据信息单元格样式

    @Override
    public void sheet(int sheetNo, Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        Sheet report = workbook.getSheet("report");
        report.createFreezePane(2, 4, 2, 4);
        report.setDisplayGridlines(false);
        //创建样式
        columnTopStyle = PoiUtils.getColumnTopStyle(workbook);//获取列头单元格样式
        style = PoiUtils.getStyle(workbook);//列数据信息单元格样式
    }

    @Override
    public void row(int rowNum, Row row) {
        Workbook workbook = row.getSheet().getWorkbook();
        //设置行高
        row.setHeight((short) (1.7 * 256));
    }

    @Override
    public void cell(int cellNum, Cell cell) {
        if (cell.getRowIndex() == 0 || cell.getRowIndex() == 1 || cell.getRowIndex() == 2) {
            cell.setCellStyle(columnTopStyle);
        } else {
            cell.setCellStyle(style);
        }
    }
}
