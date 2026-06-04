package com.guide.rag.parser;

import com.guide.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 解析器工厂 — 根据文件类型路由到对应解析器
 */
@Component
@RequiredArgsConstructor
public class ParserFactory {

    private final List<DocumentParser> parsers;

    public DocumentParser getParser(String fileType) {
        return parsers.stream()
                .filter(p -> p.supportedType().equalsIgnoreCase(fileType))
                .findFirst()
                .orElseThrow(() -> new BusinessException("不支持的文档类型: " + fileType));
    }
}
