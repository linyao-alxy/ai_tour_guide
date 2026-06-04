package com.guide.rag.parser;

/**
 * 文档解析器接口
 */
public interface DocumentParser {
    /** 支持的文档类型 */
    String supportedType();
    /** 解析文档，返回纯文本 */
    String parse(byte[] fileBytes, String fileName) throws Exception;
}
