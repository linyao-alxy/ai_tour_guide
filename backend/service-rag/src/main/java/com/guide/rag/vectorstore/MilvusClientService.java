package com.guide.rag.vectorstore;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Milvus 向量数据库客户端
 */
@Slf4j
@Service
public class MilvusClientService {

    private MilvusServiceClient client;

    @Value("${milvus.host:localhost}")
    private String host;
    @Value("${milvus.port:19530}")
    private int port;
    @Value("${milvus.collection:scenic_knowledge}")
    private String collectionName;

    @PostConstruct
    public void init() {
        client = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .build()
        );
        initCollection();
    }

    @PreDestroy
    public void destroy() {
        if (client != null) client.close();
    }

    /**
     * 初始化集合
     */
    private void initCollection() {
        // 检查集合是否存在
        R<Boolean> hasCollection = client.hasCollection(
                HasCollectionParam.newBuilder().withCollectionName(collectionName).build());
        if (hasCollection.getData()) {
            log.info("Milvus集合 {} 已存在", collectionName);
            return;
        }

        // 定义字段
        List<CreateCollectionParam.FieldType> fields = new ArrayList<>();
        fields.add(CreateCollectionParam.FieldType.newBuilder()
                .withName("id").withDataType(DataType.Int64).withPrimaryKey(true).withAutoID(true).build());
        fields.add(CreateCollectionParam.FieldType.newBuilder()
                .withName("chunk_text").withDataType(DataType.VarChar).withMaxLength(65535).build());
        fields.add(CreateCollectionParam.FieldType.newBuilder()
                .withName("embedding").withDataType(DataType.FloatVector).withDimension(1024).build());
        fields.add(CreateCollectionParam.FieldType.newBuilder()
                .withName("doc_id").withDataType(DataType.Int64).build());
        fields.add(CreateCollectionParam.FieldType.newBuilder()
                .withName("scenic_id").withDataType(DataType.Int64).build());
        fields.add(CreateCollectionParam.FieldType.newBuilder()
                .withName("chunk_type").withDataType(DataType.VarChar).withMaxLength(20).build());

        // 创建集合
        client.createCollection(CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName).withFieldTypes(fields).build());

        // 创建索引
        client.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldName("embedding")
                .withIndexType(io.milvus.common.clientenum.IndexType.IVF_FLAT)
                .withMetricType(io.milvus.common.clientenum.MetricType.IP)
                .withExtraParam("{\"nlist\":1024}")
                .build());

        // 加载集合
        client.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName).build());

        log.info("Milvus集合 {} 创建成功", collectionName);
    }

    /**
     * 插入向量
     */
    public long insert(List<Float> embedding, String chunkText, long docId, long scenicId, String chunkType) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("embedding", List.of(embedding)));
        fields.add(new InsertParam.Field("chunk_text", List.of(chunkText)));
        fields.add(new InsertParam.Field("doc_id", List.of(docId)));
        fields.add(new InsertParam.Field("scenic_id", List.of(scenicId)));
        fields.add(new InsertParam.Field("chunk_type", List.of(chunkType)));

        R<io.milvus.grpc.MutationResult> result = client.insert(
                InsertParam.newBuilder().withCollectionName(collectionName).withFields(fields).build());

        return result.getData().getIDs().getIntId().getData(0);
    }

    /**
     * 向量相似度搜索
     */
    public List<Map<String, Object>> search(List<Float> queryVector, int topK) {
        List<String> outputFields = List.of("chunk_text", "doc_id", "chunk_type");

        R<SearchResults> result = client.search(
                SearchParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withVectorFieldName("embedding")
                        .withVectors(List.of(queryVector))
                        .withOutFields(outputFields)
                        .withTopK(topK)
                        .withMetricType(io.milvus.common.clientenum.MetricType.IP)
                        .withParams("{\"nprobe\":16}")
                        .build());

        SearchResultsWrapper wrapper = new SearchResultsWrapper(result.getData().getResults());
        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 0; i < wrapper.getRowRecords().size(); i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("score", wrapper.getIDScore(0).get(i).getScore());
            item.put("chunk_text", wrapper.getFieldData("chunk_text", i));
            item.put("doc_id", wrapper.getFieldData("doc_id", i));
            item.put("chunk_type", wrapper.getFieldData("chunk_type", i));
            results.add(item);
        }
        return results;
    }
}
