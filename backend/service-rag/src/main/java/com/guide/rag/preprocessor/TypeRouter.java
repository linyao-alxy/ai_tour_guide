package com.guide.rag.preprocessor;

import org.springframework.stereotype.Component;

/**
 * 文档类型路由器 — 根据文件扩展名推断内容类型
 */
@Component
public class TypeRouter {

    public DocumentType route(String fileType, String text) {
        return switch (fileType.toUpperCase()) {
            case "XLSX" -> DocumentType.TABLE;
            case "DOCX" -> {
                // 检测是否为结构化数据：如果文本以"## "或"### "开头且包含大量 "- " 列表
                long listCount = text.lines().filter(l -> l.trim().startsWith("- ")).count();
                long totalLines = text.lines().count();
                yield (totalLines > 0 && (double) listCount / totalLines > 0.3)
                        ? DocumentType.STRUCTURED : DocumentType.NARRATIVE;
            }
            default -> DocumentType.NARRATIVE;
        };
    }
}
