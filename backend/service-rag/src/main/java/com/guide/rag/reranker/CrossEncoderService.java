package com.guide.rag.reranker;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 轻量级Cross-Encoder重排序
 * 基于关键词匹配+位置权重进行二次排序
 */
@Component
public class CrossEncoderService {

    /**
     * 重排序: 结合向量相似度 + 关键词匹配加权
     */
    public List<Map<String, Object>> rerank(String query, List<Map<String, Object>> candidates) {
        // 提取查询关键词
        Set<String> queryWords = new HashSet<>(Arrays.asList(query.split("")));

        candidates.forEach(item -> {
            double vectorScore = ((Double) item.get("score")).doubleValue();
            String chunkText = (String) item.get("chunk_text");

            // 关键词匹配加分
            double keywordBonus = 0;
            for (String word : queryWords) {
                if (word.length() >= 2 && chunkText.contains(word)) {
                    keywordBonus += 0.05;
                }
            }

            // 综合得分 = 向量分 * 0.7 + 关键词分 * 0.3
            item.put("final_score", vectorScore * 0.7 + keywordBonus * 0.3);
        });

        // 按最终得分排序
        candidates.sort((a, b) ->
                Double.compare((Double) b.get("final_score"), (Double) a.get("final_score")));

        return candidates;
    }
}
