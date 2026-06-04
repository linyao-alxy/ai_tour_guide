package com.guide.rag.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;

/**
 * .docx 文档解析器
 */
@Slf4j
@Component
public class DocxParser implements DocumentParser {

    @Override
    public String supportedType() {
        return "DOCX";
    }

    @Override
    public String parse(byte[] fileBytes, String fileName) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(fileBytes))) {
            doc.getParagraphs().forEach(p -> {
                String text = p.getText().trim();
                if (!text.isEmpty()) {
                    sb.append(text).append("\n");
                }
            });
            // 解析表格
            doc.getTables().forEach(table -> {
                sb.append("\n[表格]\n");
                table.getRows().forEach(row -> {
                    row.getTableCells().forEach(cell ->
                        sb.append(cell.getText().trim()).append("\t"));
                    sb.append("\n");
                });
            });
        }
        log.info("DOCX解析完成: {} -> {} 字符", fileName, sb.length());
        return sb.toString();
    }
}
