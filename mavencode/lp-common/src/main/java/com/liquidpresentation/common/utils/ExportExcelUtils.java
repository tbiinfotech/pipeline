package com.liquidpresentation.common.utils;

import java.awt.Color;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

import com.liquidpresentation.common.ExcelData;

public class ExportExcelUtils {
    public static void exportExcel(HttpServletResponse response, String fileName, ExcelData data) throws Exception {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename="+fileName);
        exportExcel(data, response.getOutputStream());
    }

    public static void exportExcel(ExcelData data, OutputStream out) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();
        try {
            String sheetName = data.getName();
            if (null == sheetName) {
                sheetName = "Sheet1";
            }
            XSSFSheet sheet = wb.createSheet(sheetName);
            writeExcel(wb, sheet, data);

            wb.write(out);
        } finally {
            wb.close();
        }
    }

    private static void writeExcel(XSSFWorkbook wb, Sheet sheet, ExcelData data) {

        int rowIndex = 0;

        rowIndex = writeTitlesToExcel(wb, sheet, data.getTitles());
        writeRowsToExcel(wb, sheet, data.getRows(), rowIndex);
        autoSizeColumns(sheet, data.getTitles().size() + 1);

    }

    private static int writeTitlesToExcel(XSSFWorkbook wb, Sheet sheet, List<String> titles) {
        int rowIndex = 0;
        int colIndex = 0;
        Font titleFont = wb.createFont();
        titleFont.setFontName("Calibri");
        titleFont.setBold(true);
        // titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.GREEN.getIndex());
        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillForegroundColor(new XSSFColor(new Color(204,255,204)));
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        setBorder(titleStyle, BorderStyle.NONE, new XSSFColor(new Color(0, 0, 0)));

        Row titleRow = sheet.createRow(rowIndex);
        // titleRow.setHeightInPoints(25);
        colIndex = 0;

        for (String field : titles) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(field);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }

        rowIndex++;
        return rowIndex;
    }

    private static int writeRowsToExcel(XSSFWorkbook wb, Sheet sheet, List<List<Object>> rows, int rowIndex) {
        int colIndex = 0;

        Font dataFont = wb.createFont();
        dataFont.setFontName("Calibri");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle dataStyle = wb.createCellStyle();
//        dataStyle.setWrapText(true);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFDataFormat dataFormat = wb.createDataFormat();
//        dataStyle.setDataFormat(dataFormat.getFormat("@"));
//        dataStyle.setFillForegroundColor((new XSSFColor(new Color(182, 184, 192))));
        dataStyle.setFillForegroundColor((new XSSFColor(new Color(255,153,204))));
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        dataStyle.setVerticalAlignment(XSSFCellStyle.SOLID_FOREGROUND);
        dataStyle.setFont(dataFont);
        setBorder(dataStyle, BorderStyle.NONE, new XSSFColor(new Color(0, 0, 0)));

        for (List<Object> rowData : rows) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);
            colIndex = 0;
            for (Object cellData : rowData) {
                Cell cell = dataRow.createCell(colIndex);
                if (cellData != null) {
                	if (cellData.toString().matches("^(-?\\d+)(\\.\\d+)?$")) {
                		dataStyle.setDataFormat(dataFormat.getFormat("0"));
                		cell.setCellStyle(dataStyle);
                		cell.setCellValue(Double.parseDouble(cellData.toString()));
                	} else if (cellData.toString().matches("%")){
                		dataStyle.setDataFormat(dataFormat.getFormat("0%"));
                		cell.setCellStyle(dataStyle);
                		cell.setCellValue(Double.parseDouble(cellData.toString()));
                	} else {
                		dataStyle.setDataFormat(dataFormat.getFormat("@"));
                		cell.setCellStyle(dataStyle);
                		cell.setCellValue(cellData.toString());
                	}
                } else {
                    cell.setCellValue("");
                }
                colIndex++;
            }
            rowIndex++;
        }
        return rowIndex;
    }

    private static void autoSizeColumns(Sheet sheet, int columnNumber) {

        for (int i = 0; i < columnNumber; i++) {
            int orgWidth = sheet.getColumnWidth(i);
            sheet.autoSizeColumn(i, true);
            int newWidth = (int) (sheet.getColumnWidth(i) + 100);
            if (newWidth > orgWidth) {
                sheet.setColumnWidth(i, newWidth);
            } else {
                sheet.setColumnWidth(i, orgWidth);
            }
        }
    }

    private static void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
        style.setBorderBottom(border);
        style.setBorderColor(BorderSide.TOP, color);
        style.setBorderColor(BorderSide.LEFT, color);
        style.setBorderColor(BorderSide.RIGHT, color);
        style.setBorderColor(BorderSide.BOTTOM, color);
    }
}
