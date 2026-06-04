# AI数字人导游系统

基于大模型API + 本地RAG知识库 + 2D Live2D数字人的轻量化智慧文旅伴游系统。

## 技术栈

- **用户端**: UniApp (Vue3 + TypeScript) 
- **管理端**: Vue3 + Vite + Element Plus
- **后端**: Java Spring Boot 3.2 + Spring Cloud Gateway
- **数据库**: PostgreSQL 16 + Milvus 2.4 + Redis 7 + MinIO
- **AI服务**: 讯飞ASR/TTS + DeepSeek LLM + 阿里云Embedding + 和风天气

## 项目结构

```
ai-digital-guide/
├── tourist-app/          # 用户端 UniApp
├── admin-web/            # 管理端 Vue3
├── backend/              # Java后端微服务
├── docs/                 # 文档
├── scripts/              # 运维脚本
└── docker-compose.yml    # 基础设施编排
```

## 快速启动

### 1. 启动基础设施
```bash
docker-compose up -d
```

### 2. 初始化数据库
```bash
psql -h localhost -U guide -d ai_guide -f scripts/init-db.sql
```

### 3. 启动后端
```bash
cd backend
mvn clean install -DskipTests
java -jar gateway-tourist/target/gateway-tourist.jar
java -jar gateway-admin/target/gateway-admin.jar
# ... 其他服务
```

### 4. 启动管理端
```bash
cd admin-web
npm install
npm run dev
```

### 5. 启动用户端
```bash
cd tourist-app
npm install
npm run dev:mp-weixin
```
