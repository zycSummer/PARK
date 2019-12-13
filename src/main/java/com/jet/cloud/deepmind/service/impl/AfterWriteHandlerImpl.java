package com.jet.cloud.deepmind.service.impl;

import com.alibaba.excel.event.WriteHandler;
import com.jet.cloud.deepmind.common.util.PoiUtils;
import org.apache.poi.ss.usermodel.*;


public class AfterWriteHandlerImpl implements WriteHandler {
    CellStyle columnTopStyle;//表头单元格样式
    CellStyle style;//列数据信息单元格样式

    @Override
    public void sheet(int sheetNo, Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();
        Sheet meter = workbook.getSheet("meter");
        if (meter != null) {
            meter.createFreezePane(0, 1, 0, 1);
            meter.setDisplayGridlines(false);
        }
        Sheet para = workbook.getSheet("para");
        Sheet obj = workbook.getSheet("obj");
        if (para != null) {
            para.createFreezePane(0, 1, 0, 1);
            para.setDisplayGridlines(false);
        }
        if (obj != null) {
            obj.createFreezePane(0, 1, 0, 1);
            obj.setDisplayGridlines(false);
        }
        //创建样式
        columnTopStyle = PoiUtils.getColumnTopStyle(workbook);//获取列头单元格样式
        style = PoiUtils.getStyle(workbook);//列数据信息单元格样式
    }

    @Override
    public void row(int rowNum, Row row) {
        //设置行高
        row.setHeight((short) (1.7 * 256));
    }

    @Override
    public void cell(int cellNum, Cell cell) {
        if (cell.getRowIndex() == 0) {
            cell.setCellStyle(columnTopStyle);
        } else {
            cell.setCellStyle(style);
        }
  /*      if (obj != null) {
            for (int rowNum = 0; rowNum < obj.getLastRowNum(); rowNum++) {
                int length = cell.getStringCellValue().getBytes().length;
                if (columnWidth < length) {
                    columnWidth = length;
                }
            }
        }*/
    }
}

