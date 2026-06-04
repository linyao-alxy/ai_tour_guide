package com.guide.rag.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

/**
 * .pdf 文档解析器
 */
@Slf4j
@Component
public class PdfParser implements DocumentParser {

    @Override
    public String supportedType() {
        return "PDF";
    }

    @Override
    public String parse(byte[] fileBytes, String fileName) throws Exception {
        try (var pdfDoc = Loader.loadPDF(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(pdfDoc);
            log.info("PDF解析完成: {} -> {} 字符", fileName, text.length());
            return text;
        }
    }
}
