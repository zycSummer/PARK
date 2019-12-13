package com.jet.cloud.deepmind.common.util;

import org.apache.poi.ss.usermodel.*;

public class PoiUtils {
	/*
     * 列头单元格样式
     */
	public static CellStyle getColumnTopStyle(Workbook workbook) {
		// 设置字体
		Font font = workbook.createFont();
		//设置字体大小
		font.setFontHeightInPoints((short) 11);
		//字体加粗
		font.setBold(true);
		//设置字体名字
		font.setFontName("等线");
		//设置样式;
		CellStyle style = workbook.createCellStyle();
		//设置单元格背景颜色
		style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		//solid 填充  foreground  前景色
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);
		//设置底边框颜色;
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		//设置左边框;
		style.setBorderLeft(BorderStyle.THIN);
		//设置左边框颜色;
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		//设置右边框;
		style.setBorderRight(BorderStyle.THIN);
		//设置右边框颜色;
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		//设置顶边框;
		style.setBorderTop(BorderStyle.THIN);
		//设置顶边框颜色;
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		//在样式用应用设置的字体;
		style.setFont(font);
		//设置自动换行;
		style.setWrapText(false);
		//设置水平对齐的样式为居中对齐;
		style.setAlignment(HorizontalAlignment.CENTER);
		//设置垂直对齐的样式为居中对齐;
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}

	/*
     * 列数据信息单元格样式
     */
	public static CellStyle getStyle(Workbook workbook) {
		// 设置字体
		Font font = workbook.createFont();
		//设置字体大小
		font.setFontHeightInPoints((short) 11);
		//设置字体名字
		font.setFontName("等线");
		//设置样式;
		CellStyle style = workbook.createCellStyle();
		//设置底边框;
		style.setBorderBottom(BorderStyle.THIN);
		//设置底边框颜色;
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		//设置左边框;
		style.setBorderLeft(BorderStyle.THIN);
		//设置左边框颜色;
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		//设置右边框;
		style.setBorderRight(BorderStyle.THIN);
		//设置右边框颜色;
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		//设置顶边框;
		style.setBorderTop(BorderStyle.THIN);
		//设置顶边框颜色;
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		//在样式用应用设置的字体;
		style.setFont(font);
		//设置自动换行;
		style.setWrapText(false);
		//设置水平对齐的样式为填充;
		style.setAlignment(HorizontalAlignment.CENTER);
		//设置垂直对齐的样式为居中对齐;
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}
}
