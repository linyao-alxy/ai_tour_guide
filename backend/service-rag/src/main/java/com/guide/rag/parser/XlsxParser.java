package com.guide.rag.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * .xlsx Excel解析器 — 将表格转换为Markdown格式
 */
@Slf4j
@Component
public class XlsxParser implements DocumentParser {

    @Override
    public String supportedType() {
        return "XLSX";
    }

    @Override
    public String parse(byte[] fileBytes, String fileName) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(fileBytes))) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() == 0) continue;

                sb.append("## Sheet: ").append(sheet.getSheetName()).append("\n\n");
                List<String> headers = extractHeaders(sheet);

                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;

                    // 取第一个非空单元格作为"标题"
                    Cell firstCell = row.getCell(0);
                    if (firstCell == null) continue;
                    String title = getCellValue(firstCell).trim();
                    if (title.isEmpty()) continue;

                    sb.append("### ").append(title).append("\n");
                    for (int c = 1; c < headers.size(); c++) {
                        Cell cell = row.getCell(c);
                        String value = cell != null ? getCellValue(cell).trim() : "";
                        if (!value.isEmpty()) {
                            sb.append("- ").append(headers.get(c)).append(": ").append(value).append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }
        }
        log.info("XLSX解析完成: {} -> {} 字符", fileName, sb.length());
        return sb.toString();
    }

    private List<String> extractHeaders(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell).trim());
            }
        }
        return headers;
    }

    private String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                double val = cell.getNumericCellValue();
                yield val == Math.floor(val) ? String.valueOf((long) val) : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
