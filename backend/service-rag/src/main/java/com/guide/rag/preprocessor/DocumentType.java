package com.guide.rag.preprocessor;

/**
 * 文档内容类型枚举
 */
public enum DocumentType {
    /** 纯叙事文本 */
    NARRATIVE,
    /** 结构化数据(字段-值) */
    STRUCTURED,
    /** 表格数据 */
    TABLE
}
