package com.jet.cloud.deepmind.service.excel;

import com.alibaba.excel.event.WriteHandler;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

/**
 * @author yhy
 * @create 2019-12-03 13:31
 */
@Component
public class CommonExcelHeadStyleHandler implements WriteHandler {
    @Override
    public void sheet(int i, Sheet sheet) {

    }

    @Override
    public void row(int i, Row row) {

    }

    @Override
    public void cell(int i, Cell cell) {
        Workbook workbook = cell.getSheet().getWorkbook();
        Row row = cell.getRow();
        //设置表头样式
        if (row.getRowNum() == 0) {
            row.getCell(i).setCellStyle(createStyle(workbook, true));
        } else {
            row.getCell(i).setCellStyle(createStyle(workbook, false));
        }
    }

    /**
     * 实际中如果直接获取原单元格的样式进行修改, 最后发现是改了整行的样式, 因此这里是新建一个样* 式
     */
    private CellStyle createStyle(Workbook workbook, boolean isHead) {
        CellStyle cellStyle = workbook.createCellStyle();
        // 下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        // 水平对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        if (isHead) {
            //foreground  前景色
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        // 垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

}
