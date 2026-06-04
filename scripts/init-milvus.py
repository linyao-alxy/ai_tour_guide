from pymilvus import connections, Collection, CollectionSchema, FieldSchema, DataType, IndexType

# 连接Milvus
connections.connect(host="localhost", port="19530")

# 定义字段
fields = [
    FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
    FieldSchema(name="chunk_text", dtype=DataType.VARCHAR, max_length=65535),
    FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=1024),
    FieldSchema(name="doc_id", dtype=DataType.INT64),
    FieldSchema(name="scenic_id", dtype=DataType.INT64),
    FieldSchema(name="chunk_type", dtype=DataType.VARCHAR, max_length=20),
]

schema = CollectionSchema(fields, description="景区知识库向量集合")
collection = Collection(name="scenic_knowledge", schema=schema)

# 创建索引
index_params = {
    "index_type": "IVF_FLAT",
    "metric_type": "IP",
    "params": {"nlist": 1024}
}
collection.create_index(field_name="embedding", index_params=index_params)

# 加载集合
collection.load()

print("Milvus集合 scenic_knowledge 初始化完成!")
print(f"当前实体数: {collection.num_entities}")
