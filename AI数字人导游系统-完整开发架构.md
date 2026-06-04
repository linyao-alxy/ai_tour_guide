# AI数字人导游系统 — 完整开发架构文档

> **版本**: v2.0 | **日期**: 2026-06-04 | **定位**: 一般项目（纯软件，零硬件依赖）

---

## 目录

1. [项目概述](#一项目概述)
2. [系统架构](#二系统架构)
3. [技术选型](#三技术选型)
4. [项目目录结构](#四项目目录结构)
5. [核心模块详细设计](#五核心模块详细设计)
6. [数字人地区特色定制系统](#六数字人地区特色定制系统)
7. [知识库多格式文档处理](#七知识库多格式文档处理)
8. [数据库设计](#八数据库设计)
9. [API接口设计](#九api接口设计)
10. [部署方案](#十部署方案)
11. [分阶段实施计划](#十一分阶段实施计划)
12. [成本估算](#十二成本估算)

---

## 一、项目概述

### 1.1 项目定位

基于**大模型API + 本地RAG知识库 + 2D Live2D数字人**的轻量化智慧文旅伴游系统。面向一般景区，以全部云API替代GPU自建、以客户端Live2D渲染替代视频流、以GPS地理围栏替代iBeacon硬件，实现**纯软件零硬件、开箱即用**的极简部署。

### 1.2 核心差异化

| 竞争力 | 实现方式 |
|--------|---------|
| 零硬件 | 无iBeacon，纯GPS地理围栏，无需实地部署硬件 |
| 低成本 | 零GPU实例，全部AI走云API，Live2D客户端渲染免视频流 |
| 高准确率 | 本地Milvus向量库 + 多路召回 + Cross-Encoder重排 |
| 地区定制 | Live2D分层服饰系统，不同景区不同形象/音色/方言 |
| 天气服务 | 接入和风天气API，实时天气+极端天气主动提醒 |
| 适老安全 | 跌倒检测 + GPS定位 + 一键呼救闭环 |

### 1.3 两端隔离总览

```
用户端 (C端 Tourist)              管理端 (B端 Admin)
─────────────────────           ─────────────────────
UniApp 移动App                  Vue3 Web后台
微信登录/手机号                  账号密码+RBAC
Gateway-A :8080                 Gateway-B :8081
游客API (只读知识库)             管理API (读写知识库)
        ↓                               ↓
        同一后端服务集群，不同鉴权+不同Schema
```

### 1.4 功能矩阵

| 功能模块 | 用户端 (C) | 管理端 (B) |
|---------|:----------:|:----------:|
| 语音/文字双通道交互 | ✅ | ❌ |
| Live2D数字人实时渲染 | ✅ | ✅ (预览) |
| GPS地理围栏触发讲解 | ✅ | ❌ |
| 智能问答（RAG） | ✅ | ❌ |
| 个性化行程推荐 | ✅ | ❌ |
| 实时天气+极端天气提醒 | ✅ | ❌ |
| 跌倒检测+一键呼救 | ✅ | ✅ (监控) |
| 知识库文档上传/向量化 | ❌ | ✅ |
| 知识库条目增删改查 | ❌ | ✅ |
| FAQ热词干预 | ❌ | ✅ |
| 数字人形象/服饰/音色配置 | ❌ | ✅ |
| 地区特色模板管理 | ❌ | ✅ |
| 地理围栏配置 | ❌ | ✅ |
| 游客情感分析报告 | ❌ | ✅ |
| 数据大屏实时看板 | ❌ | ✅ |
| 应急告警接收 | ❌ | ✅ |

---

## 二、系统架构

### 2.1 整体架构图

```
┌──────────────────────────────────────────────────────────┐
│              用户端 (C端 Tourist)                          │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │   UniApp 移动App (Vue3 + TypeScript)               │  │
│  │                                                    │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────┐ │  │
│  │  │ 语音交互  │ │ Live2D   │ │ GPS定位  │ │安全   │ │  │
│  │  │ ASR→LLM  │ │ 本地渲染  │ │ 地理围栏  │ │守护   │ │  │
│  │  │          │ │ TTS驱口型 │ │ 触发讲解  │ │跌倒   │ │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └───────┘ │  │
│  │                                                    │  │
│  │  ┌──────────────────────────────────────────────┐  │  │
│  │  │           天气卡片 (和风天气)                  │  │  │
│  │  └──────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────┘  │
│                          │                               │
│   鉴权: 微信/手机号        │  Gateway-A :8080              │
└──────────────────────────┼───────────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────┐
│                    Java 后端服务层                          │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │          Spring Boot 3 微服务集群                    │  │
│  │                                                    │  │
│  │  ┌─────────────────────┐  ┌─────────────────────┐  │  │
│  │  │  C端服务群           │  │  B端服务群           │  │  │
│  │  │ · ASR/LLM/TTS代理   │  │ · 知识库管理         │  │  │
│  │  │ · RAG检索引擎       │  │ · 数字人资产管理     │  │  │
│  │  │ · 天气代理          │  │ · 数据分析+报告      │  │  │
│  │  │ · 地理围栏判断      │  │ · 数据大屏           │  │  │
│  │  │ · 行程推荐          │  │ · 应急监控           │  │  │
│  │  │ · 应急响应          │  │ · 景区+围栏配置      │  │  │
│  │  │ · 鉴权服务          │  │ · 鉴权服务           │  │  │
│  │  └─────────────────────┘  └─────────────────────┘  │  │
│  │          │                        │                 │  │
│  │    只读知识库              读写知识库+分析数据        │  │
│  └────────────────────────────────────────────────────┘  │
│                          │                               │
└──────────────────────────┼───────────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────┐
│              管理端 (B端 Admin)                            │
│    鉴权: 账号密码+RBAC  │  Gateway-B :8081                 │
│  ┌────────────────────────────────────────────────────┐  │
│  │    Vue3 + Element Plus Web后台 (独立部署)            │  │
│  │                                                    │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────┐ │  │
│  │  │ 知识库   │ │ 形象配置  │ │ 数据分析  │ │数据   │ │  │
│  │  │ 上传xlsx │ │ 服饰/发型 │ │ 情感报告  │ │大屏   │ │  │
│  │  │ docx/pdf │ │ 音色/方言 │ │ 热词统计  │ │实时   │ │  │
│  │  └──────────┘ └──────────┘ └──────────┘ └───────┘ │  │
│  │  ┌──────────┐ ┌──────────┐                        │  │
│  │  │ 应急监控  │ │ 景区配置  │                        │  │
│  │  │ 告警面板  │ │ 围栏管理  │                        │  │
│  │  └──────────┘ └──────────┘                        │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘

                           │
┌──────────────────────────┼───────────────────────────────┐
│                 云API层 (全部按量付费)                       │
│                                                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │ 讯飞ASR  │ │DeepSeek  │ │ 讯飞TTS  │ │ 和风天气  │   │
│  │流式语音识别│ │ LLM API │ │流式合成  │ │ 实时预报  │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
│  ┌──────────┐ ┌──────────┐                             │
│  │ 阿里云   │ │ 高德地图  │                             │
│  │Embedding │ │ 定位SDK  │                             │
│  └──────────┘ └──────────┘                             │
└──────────────────────────────────────────────────────────┘

                           │
┌──────────────────────────┼───────────────────────────────┐
│              自建数据层 (Docker Compose 一键部署)           │
│                                                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │PostgreSQL│ │  Milvus  │ │  Redis   │ │  MinIO   │   │
│  │业务数据   │ │ 向量检索  │ │缓存/Sess │ │文档/模型  │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
│  ┌──────────┐                                           │
│  │RabbitMQ  │                                           │
│  │异步任务   │                                           │
│  └──────────┘                                           │
└──────────────────────────────────────────────────────────┘
```

### 2.2 数据流图

#### 对话全链路

```
游客语音 ─→ UniApp录音 ─→ Gateway-A ─→ ASR代理 ─→ 讯飞ASR (WebSocket)
                                                         │
                                                    识别文字
                                                         │
                                                         ▼
                                              ┌── RAG检索 ──┐
                                              │ Milvus向量  │
                                              │ + CrossEncoder│
                                              └──────┬──────┘
                                                     │
                                                 知识块+Prompt
                                                     │
                                                     ▼
                                            LLM代理 ─→ DeepSeek (SSE)
                                                     │
                                              SSE流式文字返回
                                                     │
                              ┌──────────────────────┼──────────────────────┐
                              ▼                      ▼                      ▼
                         客户端显示文字        TTS代理 ─→ 讯飞TTS        天气上下文注入
                              │                      │                      │
                              │                 流式音频URL                  │
                              │                      │                      │
                              └──────────────────────┼──────────────────────┘
                                                     ▼
                                          UniApp播放TTS音频
                                                     │
                                                     ▼
                                          Live2D SDK 驱动口型+表情
                                          (客户端本地渲染，无视频流)
```

#### 天气获取链路

```
App启动 / 定时轮询(15min)
        │
        ▼
GET /api/v1/tourist/weather/current?scenic_id=1
        │
        ▼
Gateway-A ─→ 天气代理 ─→ 和风天气API
        │                   │
        │              实时 + 逐小时 + 7天预报
        │                   │
        ▼                   ▼
   Redis缓存(30min TTL)   返回客户端
        │                   │
        ▼                   ▼
  下次命中缓存         天气卡片展示 + 数字人口播
  (节约API调用)         极端天气主动提醒
```

#### 地理围栏"移步换景"链路

```
UniApp 定时上报GPS (5s间隔)
        │
        ▼
POST /api/v1/tourist/position/report
  { "lat": 31.42, "lng": 120.11 }
        │
        ▼
   GeofenceService
   ├── 查询当前景区所有围栏
   ├── 计算游客所在围栏
   └── 返回当前景点ID
        │
        ▼
   ┌── 未进入任何围栏 ── 无操作（漫游模式）
   │
   └── 进入新围栏 ── 自动触发对应景点讲解
        (与RAG联动获取该景点知识)
```

---

## 三、技术选型

### 3.1 前端

| 端 | 框架 | 语言 | UI库 | 关键依赖 |
|----|------|------|------|---------|
| **用户端** | UniApp (Vue3) | TypeScript | uni-ui | Live2D Cubism SDK Web, 高德地图SDK, pinia |
| **管理端** | Vue3 + Vite | TypeScript | Element Plus | ECharts, Axios, Pinia, Vue Router, 高德地图JS API |

### 3.2 后端

| 层次 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Spring Boot | 3.2+ | JDK 17+ |
| 微服务 | Spring Cloud Gateway + Nacos | 2023.x | 双Gateway隔离C/B端 |
| ORM | MyBatis-Plus | 3.5+ | 简化CRUD |
| 鉴权 | Spring Security + JWT + RBAC | - | C端微信OAuth, B端账号密码 |
| 异步响应 | Spring WebFlux + SSE | - | LLM流式输出 |
| 文档解析 | Apache POI + PDFBox | 5.x / 3.x | .docx/.xlsx/.pdf 全覆盖 |
| 向量库 | Milvus Java SDK | 2.4+ | 自建Docker |
| 对象存储 | MinIO Java SDK | 8.x | 自建Docker |
| 缓存 | Redis + Redisson | 7.x | Session + 热点知识 + 天气缓存 |
| 消息队列 | RabbitMQ | 3.x | 文档向量化异步任务 |
| HTTP客户端 | WebClient (Spring) | - | 流式调用LLM/天气API |
| 地图服务 | 高德地图 Web API | - | 地理围栏+路径规划 |

### 3.3 AI服务（全部云API，零GPU自建）

| 能力 | 提供商 | 接口方式 | 关键特性 | 免费额度 |
|------|--------|---------|---------|---------|
| ASR | 科大讯飞 | WebSocket流式 | 噪声抑制、打断、方言 | 5万次/天 |
| LLM | DeepSeek | HTTP SSE流式 | 首字<500ms, 128K ctx | 500万tokens |
| TTS | 科大讯飞 | HTTP流式 | 首帧<150ms, 多音色 | 5万次/天 |
| Embedding | 阿里云 DashScope | HTTP | 1024维稠密向量 | 50万tokens/天 |
| 天气 | 和风天气 | HTTP | 实时+逐小时+7天 | 1000次/天 |
| 地图 | 高德地图 | JS SDK + Web API | GPS+围栏+路径 | 5000次/天 |
| 数字人渲染 | **客户端Live2D** (非API) | 本地SDK | TTS音频驱动口型 | 完全本地 |

### 3.4 数据库（全自建 Docker Compose）

| 数据库 | 版本 | 用途 |
|--------|------|------|
| PostgreSQL | 16 Alpine | 业务数据主库 |
| Milvus | 2.4 | 向量检索 |
| Redis | 7 Alpine | 缓存/Session/天气缓存 |
| MinIO | Latest | 文档/Live2D模型/音频 |
| RabbitMQ | 3 Management | 异步任务队列 |

---

## 四、项目目录结构

```
ai-digital-guide/
│
├── tourist-app/                         # 用户端 UniApp ★
│   ├── pages/
│   │   ├── index/                       # 首页·用户画像采集
│   │   │   └── index.vue
│   │   ├── guide/                       # 数字人伴游主界面 ★核心
│   │   │   ├── index.vue               # 主容器
│   │   │   ├── components/
│   │   │   │   ├── Live2DCanvas.vue    # Live2D WebGL画布
│   │   │   │   ├── ChatPanel.vue       # 对话气泡列表
│   │   │   │   ├── VoiceButton.vue     # 语音输入按钮
│   │   │   │   ├── QuickMenu.vue       # 快捷问题
│   │   │   │   ├── WeatherCard.vue     # 天气卡片 ★新增
│   │   │   │   └── MapMini.vue         # 地图缩略图+位置
│   │   │   └── composables/
│   │   │       ├── useLive2D.ts        # Live2D SDK封装
│   │   │       ├── useVoice.ts         # 录音+ASR交互
│   │   │       ├── useChat.ts          # 对话状态管理
│   │   │       ├── useWeather.ts       # 天气数据管理 ★新增
│   │   │       └── useGPS.ts           # GPS定位+围栏判定 ★新
│   │   ├── map/                         # 路线规划与导航
│   │   │   └── index.vue
│   │   ├── profile/                     # 个人中心
│   │   │   └── index.vue
│   │   └── safety/                      # 适老安全
│   │       └── index.vue
│   ├── api/
│   │   ├── chat.ts
│   │   ├── weather.ts                   # ★新增
│   │   ├── geofence.ts                  # ★新增
│   │   ├── itinerary.ts
│   │   └── safety.ts
│   ├── stores/                          # Pinia
│   │   ├── user.ts
│   │   ├── chat.ts
│   │   ├── weather.ts                   # ★新增
│   │   ├── avatar.ts
│   │   └── position.ts
│   ├── utils/
│   │   ├── audio-recorder.ts
│   │   └── fall-detector.ts             # 加速度计跌倒检测
│   ├── static/
│   │   └── live2d/                      # Live2D模型文件（按景区存放）
│   │       ├── lingshan/
│   │       ├── jiangnan/
│   │       ├── tangfeng/
│   │       └── default/
│   ├── manifest.json
│   ├── pages.json
│   └── App.vue
│
├── admin-web/                           # 管理端 Vue3 Web ★
│   ├── src/
│   │   ├── views/
│   │   │   ├── login/
│   │   │   │   └── LoginView.vue
│   │   │   ├── knowledge/               # 知识库管理
│   │   │   │   ├── DocumentUpload.vue   # 上传(xlsx/docx/pdf)
│   │   │   │   ├── KnowledgeList.vue    # 条目CRUD
│   │   │   │   ├── FaqManager.vue       # FAQ热词干预
│   │   │   │   └── VectorStatus.vue     # 向量化监控
│   │   │   ├── avatar/                  # 数字人形象配置
│   │   │   │   ├── AppearanceConfig.vue # 服饰/发型/配饰
│   │   │   │   ├── VoiceConfig.vue      # 音色/方言
│   │   │   │   └── RegionTemplate.vue   # 地区特色模板
│   │   │   ├── dashboard/               # 数据大屏
│   │   │   │   ├── RealTimeBoard.vue
│   │   │   │   ├── HeatMap.vue
│   │   │   │   └── WordCloud.vue
│   │   │   ├── analytics/               # 游客分析
│   │   │   │   ├── SentimentReport.vue
│   │   │   │   └── TrendAnalysis.vue
│   │   │   ├── emergency/               # 应急监控
│   │   │   │   └── AlertMonitor.vue
│   │   │   └── scenic/                  # 景区配置
│   │   │       ├── ScenicInfo.vue       # 基本信息
│   │   │       └── GeofenceConfig.vue   # 地图围栏配置 ★新
│   │   ├── api/
│   │   ├── stores/
│   │   └── router/
│   └── package.json
│
├── backend/                             # Java Spring Boot 3 ★
│   ├── gateway-tourist/                 # C端网关 :8080
│   │   ├── src/main/java/com/guide/gateway/
│   │   │   ├── GatewayTouristApp.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── TouristAuthFilter.java
│   │   │   └── routes/
│   │   │       └── TouristRouteConfig.java
│   │   └── pom.xml
│   │
│   ├── gateway-admin/                   # B端网关 :8081
│   │   └── ... (独立鉴权RBAC过滤器)
│   │
│   ├── service-common/                  # 公共模块
│   │   └── src/main/java/com/guide/common/
│   │       ├── entity/
│   │       ├── dto/
│   │       ├── exception/
│   │       ├── result/                  # 统一响应 R<T>
│   │       └── utils/
│   │
│   ├── service-auth/                    # 鉴权服务
│   │   └── src/main/java/com/guide/auth/
│   │       ├── controller/
│   │       │   ├── TouristAuthController.java
│   │       │   └── AdminAuthController.java
│   │       ├── service/
│   │       │   ├── TouristAuthService.java
│   │       │   └── AdminAuthService.java
│   │       └── config/
│   │           └── JwtTokenProvider.java
│   │
│   ├── service-rag/                     # RAG知识库服务 ★
│   │   └── src/main/java/com/guide/rag/
│   │       ├── controller/
│   │       │   ├── RagQueryController.java       # 游客问答
│   │       │   └── KnowledgeAdminController.java # 管理端CRUD
│   │       ├── service/
│   │       │   ├── RagRetrievalService.java      # 多路召回+重排
│   │       │   ├── KnowledgeIngestService.java   # 文档摄取
│   │       │   └── VectorIndexService.java       # Milvus索引
│   │       ├── parser/                           # 多格式解析器
│   │       │   ├── DocumentParser.java           # 接口
│   │       │   ├── DocxParser.java               # .docx
│   │       │   ├── XlsxParser.java               # .xlsx
│   │       │   ├── PdfParser.java                # .pdf
│   │       │   └── ParserFactory.java
│   │       ├── preprocessor/                     # 预处理管道
│   │       │   ├── TypeRouter.java               # 文档类型路由
│   │       │   ├── TableToMarkdown.java          # 表格→MD
│   │       │   └── FieldExtractor.java           # 字段提取
│   │       ├── chunker/
│   │       │   ├── ChunkStrategy.java
│   │       │   ├── SemanticChunker.java          # 叙事语义分块
│   │       │   ├── TableChunker.java             # 表格行级分块
│   │       │   └── ChunkerFactory.java
│   │       ├── embedding/
│   │       │   └── EmbeddingService.java         # 阿里云嵌入API
│   │       ├── vectorstore/
│   │       │   └── MilvusClientService.java      # Milvus SDK
│   │       └── reranker/
│   │           └── CrossEncoderService.java
│   │
│   ├── service-ai-proxy/                # AI代理服务 ★
│   │   └── src/main/java/com/guide/aiproxy/
│   │       ├── controller/
│   │       │   ├── AsrController.java            # ASR代理
│   │       │   ├── LlmController.java            # LLM代理
│   │       │   ├── TtsController.java            # TTS代理
│   │       │   └── WeatherController.java        # 天气代理 ★新增
│   │       └── service/
│   │           ├── AsrProxyService.java
│   │           ├── LlmProxyService.java
│   │           ├── TtsProxyService.java
│   │           └── WeatherProxyService.java      # ★新增 和风天气
│   │
│   ├── service-geofence/                # 地理围栏服务 ★新
│   │   └── src/main/java/com/guide/geofence/
│   │       ├── controller/
│   │       │   └── GeofenceController.java       # 围栏查询+GPS上报
│   │       └── service/
│   │           ├── GeofenceService.java          # 围栏判定核心
│   │           └── GpsService.java               # GPS坐标处理
│   │
│   ├── service-itinerary/               # 行程推荐
│   │   └── src/main/java/com/guide/itinerary/
│   │       ├── controller/
│   │       │   └── ItineraryController.java
│   │       └── service/
│   │           ├── UserProfileService.java
│   │           ├── RoutePlannerService.java      # Dijkstra
│   │           └── WeatherAwareService.java      # ★新增 天气感知路径
│   │
│   ├── service-safety/                  # 安全应急
│   │   └── src/main/java/com/guide/safety/
│   │       ├── controller/
│   │       │   └── SafetyController.java
│   │       └── service/
│   │           ├── FallDetectionService.java
│   │           └── EmergencyAlertService.java
│   │
│   ├── service-analytics/               # 数据分析 (B端)
│   │   └── src/main/java/com/guide/analytics/
│   │       ├── controller/
│   │       │   └── AnalyticsController.java
│   │       └── service/
│   │           ├── SentimentAnalysisService.java
│   │           ├── HotWordService.java
│   │           └── ReportGenerator.java
│   │
│   ├── service-avatar/                  # 数字人资产管理 (B端)
│   │   └── src/main/java/com/guide/avatar/
│   │       ├── controller/
│   │       │   └── AvatarAdminController.java
│   │       └── service/
│   │           ├── CostumeService.java
│   │           ├── VoiceService.java
│   │           └── RegionTemplateService.java
│   │
│   └── pom.xml                          # 父POM
│
├── docs/
│   ├── api-spec-tourist.md
│   ├── api-spec-admin.md
│   ├── live2d-costume-guide.md
│   └── architecture.md
│
├── scripts/
│   ├── init-db.sql                      # PostgreSQL初始化
│   └── init-milvus.py                   # Milvus Collection初始化
│
├── docker-compose.yml
│
└── README.md
```

---

## 五、核心模块详细设计

### 5.1 用户画像采集（C端首页）

**入口**：首次打开App → 简短引导对话或标签自选

```
用户选择标签: [历史文化] [自然风光] [亲子出游] [银发慢行]
                      │
                      ▼
           POST /api/v1/tourist/user-profile
           {
             "preferences": ["history", "nature", "family"],
             "pace": "slow",
             "accessibility": true
           }
                      │
                      ▼
            返回画像ID → 存入Pinia → 后续对话自动带画像上下文
```

### 5.2 AI对话全链路（C端核心）

```
┌─── 客户端 ───┐     ┌──────── 后端 ────────┐     ┌── 云API ──┐
               │     │                       │     │          │
按住说话按钮 ──┼────→│ Gateway-A → ASR代理 ──────→│ 讯飞ASR  │
               │     │                 ← 文字 ────│(WebSocket)│
               │     │     ↓                   │     │          │
               │     │  RAG检索                 │     │          │
               │     │  Milvus向量 + CrossEncoder│     │          │
               │     │     ↓                   │     │          │
               │     │  构建Prompt              │     │          │
               │     │  (知识+历史+画像+天气)    │     │          │
               │     │     ↓                   │     │          │
显示文字 ←─────┼─────│  SSE流式 ← LLM代理 ────────→│ DeepSeek │
               │     │                       │     │          │
TTS音频 ←─────┼─────│  ← TTS代理 ────────────────→│ 讯飞TTS  │
               │     │                       │     │          │
Live2D口型     │     │                       │     │          │
(本地渲染)     │     │                       │     │          │
```

**时延预算**（目标 < 2s）：

| 环节 | 预算 | 实现方式 |
|------|------|---------|
| ASR | ≤200ms | 讯飞流式，边说边传 |
| RAG检索 | ≤250ms | Milvus索引 + 重排 |
| LLM首字 | ≤500ms | DeepSeek SSE流式 |
| TTS首帧 | ≤150ms | 讯飞流式TTS |
| Live2D口型 | 本地 | 实时，不计网络延迟 |
| **总计** | **≤1100ms** | 远低于3s红线 |

**核心代码结构**（LlmProxyService.java）：

```java
@RestController
@RequestMapping("/api/v1/tourist")
public class LlmController {

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest req) {
        // 1. RAG检索（非阻塞）
        List<KnowledgeChunk> chunks = ragService.retrieve(req.getQuery(), 5);

        // 2. 获取天气上下文
        WeatherInfo weather = weatherService.getCached(req.getScenicId());

        // 3. 获取用户画像
        UserProfile profile = userProfileService.get(req.getUserId());

        // 4. 构建Prompt
        String prompt = promptBuilder.build(profile, chunks, req.getHistory(), weather);

        // 5. 流式调用LLM
        return llmClient.streamChat(prompt)
            .map(token -> ServerSentEvent.<String>builder()
                .event("token")
                .data("{\"text\":\"" + token + "\"}")
                .build())
            .doOnComplete(() -> {
                // 6. LLM完成后异步触发TTS
                ttsService.streamSynthesize(accumulatedText, req.getVoiceId());
            });
    }
}
```

### 5.3 天气提示模块 ★

**实现方案**：

```
天气获取策略:
  ├── 客户端首次进入伴游页面 → 主动请求
  ├── 定时轮询 (15分钟) → 更新实时天气
  └── 后端Redis缓存 (30分钟TTL) → 减少API调用

极端天气提醒:
  ├── 未来2小时内有降雨 → 数字人口播提醒
  ├── 高温 > 35°C → 提醒防暑
  ├── 大风 > 6级 → 提醒避开户外高处
  └── 空气质量差 → 提醒佩戴口罩
```

**UniApp天气卡片组件** (`WeatherCard.vue`)：

```
┌─────────────────────────────┐
│ ☀️ 灵山胜境 · 晴 28°C       │
│ 今日: 晴转多云 22°C ~ 30°C  │
│ 湿度: 55%  风速: 3级        │
│                             │
│ ⚠️ 预计15:00有小雨          │
│ 建议您提前游览户外景点       │
└─────────────────────────────┘
```

**和风天气API调用示例**：

```java
// WeatherProxyService.java
public WeatherInfo getWeather(Long scenicId) {
    // 1. 查Redis缓存
    String cacheKey = "weather:" + scenicId;
    WeatherInfo cached = redisTemplate.opsForValue().get(cacheKey);
    if (cached != null) return cached;

    // 2. 查询景区坐标
    Scenic scenic = scenicMapper.selectById(scenicId);

    // 3. 调用和风天气API
    WeatherInfo weather = webClient.get()
        .uri("https://devapi.qweather.com/v7/weather/now?location={lng},{lat}&key={key}",
             scenic.getLng(), scenic.getLat(), apiKey)
        .retrieve()
        .bodyToMono(WeatherInfo.class)
        .block();

    // 4. 写入缓存
    redisTemplate.opsForValue().set(cacheKey, weather, 30, TimeUnit.MINUTES);
    return weather;
}
```

### 5.4 地理围栏"移步换景"模块 ★

**替代原iBeacon方案**：

```
原方案 (已删除):
  iBeacon RSSI → 卡尔曼滤波 → WKNN → 坐标(±2m)
  需要: 硬件信标 + 指纹采集 + 定位引擎微服务

新方案 (当前):
  手机GPS → 高德地图SDK → 坐标(±5-10m) → 地理围栏判定
  需要: 零硬件 + 管理端配置围栏坐标
```

**管理端围栏配置**（`GeofenceConfig.vue`）：

```
管理端页面:
  ┌──────────────────────────────────┐
  │  灵山胜境 景点围栏配置             │
  │                                  │
  │  [高德地图画布]                   │
  │   ○ 灵山大佛 (半径50m)            │
  │   ○ 九龙灌浴 (半径30m)            │
  │   ○ 梵宫     (半径80m)            │
  │                                  │
  │  [+ 新增景点围栏]                 │
  │  拖拽中心点 + 拖动半径 → 保存      │
  └──────────────────────────────────┘
```

**围栏判定核心逻辑**：

```java
// GeofenceService.java
public ScenicSpot checkGeofence(double lat, double lng, Long scenicId) {
    // 加载该景区所有围栏（Redis缓存）
    List<ScenicSpot> spots = scenicSpotCache.get(scenicId);

    for (ScenicSpot spot : spots) {
        double distance = haversine(lat, lng, spot.getLat(), spot.getLng());
        if (distance <= spot.getRadius()) {
            return spot; // 进入该景点围栏
        }
    }
    return null; // 未进入任何围栏
}

// Haversine公式计算两点距离(米)
private double haversine(double lat1, double lng1, double lat2, double lng2) {
    final double R = 6371000; // 地球半径(米)
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLng/2) * Math.sin(dLng/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
}
```

**围栏讲解触发逻辑**：

```java
// 在UniApp端 useGPS.ts 中
let currentSpotId = null;

function onPositionUpdate(lat, lng) {
    const spot = await geofenceApi.check(lat, lng);

    if (spot && spot.id !== currentSpotId) {
        // 进入新景点 → 自动触发讲解
        currentSpotId = spot.id;
        chatStore.autoQuery(`请介绍一下${spot.name}`);
    } else if (!spot) {
        // 离开景点 → 进入漫游模式
        currentSpotId = null;
    }
}
```

### 5.5 适老化安全守护

```
加速度计突变 ─→ 高度骤降 ─→ 静止 > 30s
         │             │            │
         └─────────────┴────────────┘
                      │
              判定为疑似跌倒
                      │
          ┌───────────┴───────────┐
          │   App弹出确认框        │
          │   "您是否安全？"       │
          │   + 高分贝蜂鸣         │
          └───────────┬───────────┘
                      │
           ┌────15s内未取消────┐
           │                   │
           ▼                   ▼
      用户取消              触发应急响应
      记录日志              ├─ 当前GPS坐标
                            ├─ 推送管理端大屏
                            ├─ 拨打紧急联系人
                            └─ 短信通知景区安保
```

---

## 六、数字人地区特色定制系统

### 6.1 分层模型架构

```
┌─────────────────────────────────────────┐
│           Live2D 分层模型体系            │
│                                         │
│  Layer 5: 背景/场景 (景区实景合成)       │
│  Layer 4: 配饰层 (头饰/耳环/团扇/佛珠)  │
│  Layer 3: 服饰层 (唐装/汉服/旗袍/僧袍)  │
│  Layer 2: 发型层 (发髻/发冠/束发/僧帽)  │
│  Layer 1: 底座层 (面部+身体基础模型)     │
│                                         │
│  所有层通过 Live2D Cubism 参数绑定       │
│  口型参数由TTS音频实时驱动               │
│  情绪参数由LLM输出标注驱动               │
└─────────────────────────────────────────┘
```

### 6.2 地区特色模板

| 景区类型 | 底座 | 服饰 | 发型 | 配饰 | 音色 | 方言 |
|---------|------|------|------|------|------|------|
| 江南水乡 | 温婉女 | 旗袍/丝绸 | 盘发 | 油纸伞 | 柔和女声 | 吴语 |
| 西北大漠 | 英朗男 | 胡服/皮甲 | 束发 | 弯刀 | 低沉男声 | 西北官话 |
| 唐风遗址 | 丰腴女 | 齐胸襦裙 | 高髻 | 团扇 | 端庄女声 | 关中话 |
| 西南民族 | 清秀女 | 百褶裙/银饰 | 银角冠 | 银项圈 | 清亮女声 | 西南官话 |
| 佛教圣地 | 慈祥长者 | 僧袍/袈裟 | 僧帽 | 佛珠 | 温和男声 | 普通话 |

### 6.3 管理端配置流程

```
管理员 → 登录后台 → 数字人形象配置
    │
    ├── 选择底座模型 (5+预置)
    ├── 选择服饰层 (按地区模板推荐)
    ├── 调整配色
    ├── 选择音色 (10+预置)
    ├── 选择方言模型
    ├── 上传自定义配饰 (可选)
    └── 预览 → 发布
            │
            ▼
    客户端启动 → 检测景区ID → 下载对应Live2D资源包 → 本地渲染
```

---

## 七、知识库多格式文档处理

### 7.1 处理管道

```
原始文档上传 (MinIO暂存)
        │
        ▼
┌──────────────────────────────┐
│ FileTypeDetector             │  扩展名+MIME检测
│ .xlsx / .docx / .pdf / .txt  │
└──────────────┬───────────────┘
               │
    ┌──────────┼──────────┐
    ▼          ▼          ▼
┌──────┐ ┌──────┐ ┌──────────┐
│Xlsx  │ │Docx  │ │Pdf/Txt   │
│Parser│ │Parser│ │Parser    │
│POI   │ │POI   │ │PDFBox/IO │
└──┬───┘ └──┬───┘ └────┬─────┘
   │        │           │
   ▼        ▼           ▼
┌──────────────────────────────┐
│ TypeRouter                   │  内容类型路由
│  ├── 叙事文本 → SemanticChunker
│  ├── 结构化数据 → FieldExtractor
│  └── 表格数据 → TableToMarkdown → TableChunker
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ EmbeddingService             │  阿里云 text-embedding-v3
│ 1024维稠密向量               │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│ Milvus Insert                │
│ Collection: scenic_knowledge │
└──────────────────────────────┘
```

### 7.2 分块策略

| 文档类型 | 分块策略 | 块大小 | 重叠 | 示例 |
|---------|---------|--------|------|------|
| 叙事文本(docx长篇) | 语义段落分界 | 512 tokens | 64 tokens | 灵山胜境历史文化描述 |
| 结构化数据(docx数据集) | 字段级实体分块 | 单条记录 | 0 | 景点名+坐标+时间+标签 |
| 表格数据(xlsx) | 行→Markdown转换 | 单行 | 0（表头拼接）| 客流/行为数据行 |
| PDF | 按页+段落 | 512 tokens | 64 tokens | 景区手册 |
| TXT | 按段落 | 512 tokens | 64 tokens | 纯文本介绍 |

### 7.3 表格→Markdown转换示例

**输入**（xlsx 一行）：

| 景点名称 | 高度 | 开放时间 | 最佳季节 | 主题 | 区域 |
|---------|------|---------|---------|------|------|
| 灵山大佛 | 88m | 08:00-17:30 | 春秋 | 佛教文化 | A区 |

**输出**（嵌入前Markdown）：

```markdown
## 灵山大佛
- 高度: 88米
- 开放时间: 08:00-17:30
- 最佳游览季节: 春秋两季
- 主题分类: 佛教文化
- 所在区域: A区
```

---

## 八、数据库设计

### 8.1 Schema分布

```
PostgreSQL
├── Schema: tourist         # C端只读
│   ├── user                # 游客信息
│   ├── chat_session        # 对话会话
│   ├── chat_message        # 对话消息
│   ├── scenic_spot         # 景点+围栏数据（只读）
│   └── avatar_config       # 数字人配置（只读已发布）
│
├── Schema: admin           # B端读写
│   ├── admin_user          # 管理员+RBAC
│   ├── document            # 上传文档记录
│   ├── knowledge_chunk     # 知识块元数据
│   ├── faq_override        # FAQ热词干预
│   ├── avatar_asset        # Live2D资源文件
│   ├── scenic              # 景区基础配置
│   ├── scenic_spot         # 景点+围栏（读写）
│   ├── analytics_session   # 分析汇总
│   └── emergency_alert     # 应急告警
│
├── Schema: public          # 共享
│   └── region_template     # 地区特色模板

Milvus
├── Collection: scenic_knowledge
│   ├── id (int64)
│   ├── chunk_text (varchar)
│   ├── embedding (float_vector[1024])
│   ├── doc_id (int64)
│   ├── scenic_id (int64)
│   ├── chunk_type (varchar)   # NARRATIVE/STRUCTURED/TABLE
│   └── metadata (json)

Redis
├── session:{token}              # JWT Session (TTL 7d)
├── hot_knowledge:{scenic_id}   # 热点知识缓存 (TTL 1h)
├── weather:{scenic_id}         # 天气缓存 (TTL 30min) ★
├── geofence:{scenic_id}        # 围栏数据缓存 (TTL 1h) ★
├── user_position:{user_id}     # 实时位置 (TTL 30s)
└── rate_limit:{ip}             # 限流

MinIO
├── documents/                   # 上传原始文档
├── live2d/                      # Live2D模型 (.moc3, textures, motions)
├── tts_audio/                   # TTS音频文件
└── reports/                     # PDF分析报告
```

### 8.2 核心表DDL

```sql
-- ==================== C端 Schema ====================
CREATE SCHEMA tourist;

-- 游客用户
CREATE TABLE tourist.user (
    id            BIGSERIAL PRIMARY KEY,
    openid        VARCHAR(100) UNIQUE,
    phone         VARCHAR(20),
    nickname      VARCHAR(50),
    avatar_url    VARCHAR(500),
    profile_tags  JSONB,                     -- {"type":"family","pace":"slow"}
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 对话会话
CREATE TABLE tourist.chat_session (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    scenic_id     BIGINT NOT NULL,
    position_log  JSONB,                     -- [{lat,lng,timestamp}]
    started_at    TIMESTAMP DEFAULT NOW(),
    ended_at      TIMESTAMP
);

-- 对话消息
CREATE TABLE tourist.chat_message (
    id            BIGSERIAL PRIMARY KEY,
    session_id    BIGINT NOT NULL,
    role          VARCHAR(10) NOT NULL,       -- USER / ASSISTANT
    content       TEXT NOT NULL,
    audio_url     VARCHAR(500),
    chunks_used   JSONB,                     -- 使用的知识块ID
    latency_ms    INTEGER,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 景点+围栏 (C端只读视图)
CREATE TABLE tourist.scenic_spot (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    spot_name     VARCHAR(100) NOT NULL,
    latitude      DECIMAL(12,8) NOT NULL,     -- ★GPS替代iBeacon
    longitude     DECIMAL(12,8) NOT NULL,
    radius        INTEGER DEFAULT 50,         -- ★围栏半径(米)
    sort_order    INTEGER DEFAULT 0,
    description   TEXT
);

-- 数字人配置 (C端只读已发布)
CREATE TABLE tourist.avatar_config (
    id              BIGSERIAL PRIMARY KEY,
    scenic_id       BIGINT NOT NULL,
    base_model      VARCHAR(100) NOT NULL,
    costume_layers  JSONB NOT NULL,
    accessory_layers JSONB,
    hair_style      VARCHAR(100),
    color_scheme    VARCHAR(50),
    voice_id        VARCHAR(50) NOT NULL,     -- 讯飞TTS音色ID
    dialect         VARCHAR(20),
    speech_speed    DECIMAL(3,2) DEFAULT 1.0,
    status          SMALLINT DEFAULT 2,       -- 只读已发布
    created_at      TIMESTAMP DEFAULT NOW()
);

-- ==================== B端 Schema ====================
CREATE SCHEMA admin;

-- 管理员
CREATE TABLE admin.admin_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role          VARCHAR(20) NOT NULL,        -- SUPER_ADMIN / SCENIC_ADMIN / EDITOR
    scenic_id     BIGINT,                      -- 关联景区（景区管理员限定）
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 上传文档记录
CREATE TABLE admin.document (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    file_name     VARCHAR(200) NOT NULL,
    file_type     VARCHAR(10) NOT NULL,        -- XLSX/DOCX/PDF/TXT
    file_size     BIGINT,
    minio_path    VARCHAR(500) NOT NULL,
    chunk_count   INTEGER,
    status        VARCHAR(20) DEFAULT 'UPLOADED', -- UPLOADED/CHUNKING/VECTORIZING/DONE/FAILED
    uploaded_by   BIGINT,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 知识块元数据
CREATE TABLE admin.knowledge_chunk (
    id            BIGSERIAL PRIMARY KEY,
    document_id   BIGINT NOT NULL,
    scenic_id     BIGINT NOT NULL,
    chunk_index   INTEGER NOT NULL,
    chunk_text    TEXT NOT NULL,
    chunk_type    VARCHAR(20),                  -- NARRATIVE/STRUCTURED/TABLE
    milvus_id     BIGINT,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- FAQ热词干预
CREATE TABLE admin.faq_override (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    hot_words     VARCHAR(200) NOT NULL,
    forced_answer TEXT NOT NULL,
    priority      INTEGER DEFAULT 0,
    is_active     BOOLEAN DEFAULT TRUE
);

-- Live2D资源文件
CREATE TABLE admin.avatar_asset (
    id              BIGSERIAL PRIMARY KEY,
    asset_type      VARCHAR(30) NOT NULL,       -- BASE_MODEL/COSTUME/ACCESSORY/HAIR
    asset_name      VARCHAR(100) NOT NULL,
    region_tag      VARCHAR(50),
    file_path       VARCHAR(500) NOT NULL,      -- MinIO路径
    thumbnail_path  VARCHAR(500),
    file_size       BIGINT,
    uploaded_by     BIGINT,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- 应急告警
CREATE TABLE admin.emergency_alert (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    scenic_id     BIGINT NOT NULL,
    latitude      DECIMAL(12,8),               -- ★GPS坐标
    longitude     DECIMAL(12,8),
    alert_type    VARCHAR(20),                  -- FALL_DETECTED / MANUAL
    status        VARCHAR(20) DEFAULT 'PENDING', -- PENDING/CONFIRMED/RESOLVED
    handler_id    BIGINT,
    resolved_at   TIMESTAMP,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 景区配置 (★新增围栏字段)
CREATE TABLE admin.scenic (
    id            BIGSERIAL PRIMARY KEY,
    scenic_name   VARCHAR(100) NOT NULL,
    province      VARCHAR(50),
    city          VARCHAR(50),
    latitude      DECIMAL(12,8),                -- 景区中心坐标（天气API用）
    longitude     DECIMAL(12,8),
    address       VARCHAR(300),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 景区+围栏 (B端读写)
CREATE TABLE admin.scenic_spot (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    spot_name     VARCHAR(100) NOT NULL,
    latitude      DECIMAL(12,8) NOT NULL,       -- ★围栏中心GPS
    longitude     DECIMAL(12,8) NOT NULL,
    radius        INTEGER DEFAULT 50,           -- ★围栏半径(米)
    sort_order    INTEGER DEFAULT 0,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- ==================== 公共Schema ====================
-- 地区特色模板
CREATE TABLE public.region_template (
    id              BIGSERIAL PRIMARY KEY,
    template_name   VARCHAR(50) NOT NULL,        -- 江南水乡/西北大漠/唐风遗址...
    base_model      VARCHAR(100) NOT NULL,
    costume_layers  JSONB NOT NULL,
    accessory_layers JSONB,
    hair_style      VARCHAR(100),
    color_scheme    VARCHAR(50),
    voice_id        VARCHAR(50),
    dialect         VARCHAR(20),
    created_at      TIMESTAMP DEFAULT NOW()
);

-- 插入初始模板数据
INSERT INTO public.region_template VALUES
(1, '江南水乡', 'gentle_female', '["cheongsam","silk_scarf"]', '["oil_paper_umbrella"]', 'updo', 'pastel', 'xunfei_female_01', 'wu'),
(2, '唐风遗址', 'plump_female', '["qixiong_dress"]', '["round_fan"]', 'high_bun', 'warm_red', 'xunfei_female_03', 'guanzhong'),
(3, '佛教圣地', 'kind_elder', '["monk_robe"]', '["prayer_beads"]', 'monk_hat', 'saffron', 'xunfei_male_02', 'mandarin');
```

---

## 九、API接口设计

### 9.1 C端API (`/api/v1/tourist/**` Gateway-A :8080)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/wechat-login` | 微信OAuth登录 |
| POST | `/auth/phone-login` | 手机验证码登录 |
| POST | `/user-profile` | 提交/更新用户画像 |
| POST | `/chat/stream` | **SSE流式对话**（核心） |
| GET | `/chat/history/{session_id}` | 对话历史 |
| POST | `/position/report` | 上报GPS→返回所在景点ID ★新 |
| GET | `/scenic/{id}/avatar-config` | 获取数字人配置 |
| GET | `/weather/current` | **实时天气** ★新增 |
| GET | `/weather/forecast` | 7天预报 ★新增 |
| GET | `/itinerary/recommend` | 行程推荐（含天气建议）|
| POST | `/safety/fall-check` | 跌倒确认取消 |
| POST | `/safety/emergency-call` | 一键呼救 |

### 9.2 B端API (`/api/v1/admin/**` Gateway-B :8081)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 管理员登录 |
| POST | `/knowledge/upload` | 上传文档(xlsx/docx/pdf/txt) |
| GET | `/knowledge/list` | 知识条目列表 |
| PUT | `/knowledge/{id}` | 编辑条目 |
| DELETE | `/knowledge/{id}` | 删除条目 |
| POST | `/knowledge/re-vectorize` | 重新向量化 |
| POST | `/faq-override` | FAQ热词干预 |
| POST | `/avatar/config` | 配置数字人 |
| POST | `/avatar/asset/upload` | 上传Live2D资源 |
| POST | `/scenic/spot` | **新增景点围栏** ★新 |
| PUT | `/scenic/spot/{id}` | 编辑围栏 ★新 |
| DELETE | `/scenic/spot/{id}` | 删除围栏 ★新 |
| GET | `/analytics/realtime` | 数据大屏 |
| GET | `/analytics/sentiment-report` | 情感报告 |
| GET | `/emergency/alerts` | 告警列表 |
| PUT | `/emergency/{id}/resolve` | 处理告警 |

### 9.3 C端核心接口详例

**POST `/api/v1/tourist/chat/stream`** (SSE)

请求：
```json
{
  "session_id": 1001,
  "query": "灵山大佛有多高？",
  "lat": 31.4200,
  "lng": 120.1100,
  "voice_id": "xunfei_male_02"
}
```

响应（SSE事件流）：
```
event: token
data: {"text":"灵山","seq":1}

event: token
data: {"text":"大佛","seq":2}

event: weather
data: {"temp":28,"condition":"晴","alert":null}

event: tts_audio
data: {"url":"https://minio.xxx/tts/abc.mp3","duration_ms":1500}

event: emotion
data: {"action":"smile","intensity":0.8}

event: spot_trigger
data: {"spot_id":12,"spot_name":"灵山大佛"}

event: done
data: {"latency_ms":980,"chunks":[12,45,78]}
```

**GET `/api/v1/tourist/weather/current`** ★

请求：`?scenic_id=1`

响应：
```json
{
  "code": 200,
  "data": {
    "obsTime": "2026-06-04T14:30+08:00",
    "temp": "28",
    "feelsLike": "30",
    "condition": "晴",
    "conditionCode": "100",
    "windDir": "东南风",
    "windScale": "3",
    "humidity": "55",
    "alerts": [
      {
        "type": "雷阵雨",
        "level": "黄色",
        "startTime": "2026-06-04T15:00+08:00",
        "advice": "预计15:00有小雨，建议提前游览户外景点"
      }
    ]
  }
}
```

---

## 十、部署方案

### 10.1 Docker Compose 基础设施

```yaml
# docker-compose.yml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    container_name: guide-postgres
    ports:
      - "5432:5432"
    volumes:
      - ./data/postgres:/var/lib/postgresql/data
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init.sql
    environment:
      POSTGRES_DB: ai_guide
      POSTGRES_USER: guide
      POSTGRES_PASSWORD: ${PG_PASSWORD}

  redis:
    image: redis:7-alpine
    container_name: guide-redis
    ports:
      - "6379:6379"
    command: redis-server --requirepass ${REDIS_PASSWORD}

  etcd:
    image: quay.io/coreos/etcd:v3.5.5
    container_name: guide-etcd
    environment:
      - ETCD_AUTO_COMPACTION_MODE=revision
      - ETCD_AUTO_COMPACTION_RETENTION=1000
      - ETCD_QUOTA_BACKEND_BYTES=4294967296

  minio:
    image: minio/minio:latest
    container_name: guide-minio
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - ./data/minio:/data
    environment:
      MINIO_ROOT_USER: ${MINIO_USER}
      MINIO_ROOT_PASSWORD: ${MINIO_PASSWORD}
    command: server /data --console-address ":9001"

  milvus:
    image: milvusdb/milvus:v2.4.0
    container_name: guide-milvus
    ports:
      - "19530:19530"
      - "9091:9091"
    depends_on:
      - etcd
      - minio
    volumes:
      - ./data/milvus:/var/lib/milvus
    environment:
      ETCD_ENDPOINTS: etcd:2379
      MINIO_ADDRESS: minio:9000
      MINIO_ACCESS_KEY_ID: ${MINIO_USER}
      MINIO_SECRET_ACCESS_KEY: ${MINIO_PASSWORD}

  rabbitmq:
    image: rabbitmq:3-management-alpine
    container_name: guide-rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: ${MQ_USER}
      RABBITMQ_DEFAULT_PASS: ${MQ_PASSWORD}
```

### 10.2 Java服务部署

```
┌──────────────────────────────────────────┐
│          云服务器 (4C8G 起步)             │
│                                          │
│  Docker Compose:                         │
│  ├── PostgreSQL + Milvus + etcd           │
│  ├── Redis + MinIO + RabbitMQ            │
│  │                                       │
│  Java微服务 (jar包):                      │
│  ├── gateway-tourist.jar     :8080        │
│  ├── gateway-admin.jar       :8081        │
│  ├── service-auth.jar                    │
│  ├── service-rag.jar                     │
│  ├── service-ai-proxy.jar               │
│  ├── service-geofence.jar               │  ★
│  ├── service-itinerary.jar              │
│  ├── service-safety.jar                 │
│  ├── service-analytics.jar              │
│  └── service-avatar.jar                 │
│                                          │
│  Nginx 反向代理:                         │
│  ├── api.tourist.guide.com  → :8080      │
│  └── api.admin.guide.com    → :8081      │
└──────────────────────────────────────────┘
```

---

## 十一、分阶段实施计划

### Phase 1: 核心MVP（6-8周）

| 步骤 | 任务 | 产出 |
|------|------|------|
| 1.1 | Docker Compose 全部中间件运行 | 基础设施就绪 |
| 1.2 | 鉴权服务（C端微信登录 + B端账号登录）| 双端鉴权可用 |
| 1.3 | 多格式文档解析器（Xlsx/Docx/Pdf Parser）| 文档→纯文本提取 |
| 1.4 | RAG引擎（分块→嵌入→Milvus写入→检索+重排）| 知识库可问答 |
| 1.5 | AI代理服务（ASR/LLM/TTS）| 云API通道打通 |
| 1.6 | UniApp项目初始化（路由+鉴权+基础UI）| App骨架 |
| 1.7 | 对话主链路串联（语音→ASR→RAG→LLM→TTS→播放）| **首条AI对话** |
| 1.8 | 管理后台架子（Vue3初始化+登录+知识库上传）| 知识库可管理 |

**里程碑** 🎯：语音提问"灵山大佛多高"，3秒内听到TTS回答。

### Phase 2: 数字人+定位+天气（4-6周）

| 步骤 | 任务 | 产出 |
|------|------|------|
| 2.1 | Live2D SDK集成（WebGL画布+TTS驱口型）| 数字人可动 |
| 2.2 | 数字人服饰系统（分层加载+管理端配置）| 可换装 |
| 2.3 | 5套地区模板预置 | 形象库就绪 |
| 2.4 | 地理围栏引擎（GPS上报+围栏判定+自动讲解）| **"移步换景"** |
| 2.5 | 管理端围栏配置页（地图拖拽画圈）| 围栏可配置 |
| 2.6 | 天气模块（和风天气API+缓存+极端提醒）| 天气卡片+口播 |

**里程碑** 🎯：走近景点GPS围栏时，数字人自动切换形象+开始讲解，天气异常时主动提醒。

### Phase 3: 运营+安全（3-4周）

| 步骤 | 任务 | 产出 |
|------|------|------|
| 3.1 | 数据大屏（实时指标+热力图+词云）| 管理大屏 |
| 3.2 | 游客分析报告（情感分析+热词+PDF生成）| 分析能力 |
| 3.3 | 适老安全（跌倒检测+一键呼救+告警面板）| 安全闭环 |
| 3.4 | 行程推荐（画像+图论路径+天气感知）| 智能路线 |
| 3.5 | 全链路压测+优化 | 生产就绪 |

**里程碑** 🎯：完整系统在灵山胜境实地试运行。

---

## 十二、成本估算

### 12.1 月度运营成本

| 资源 | 说明 | 月费(估算) |
|------|------|-----------|
| 云服务器 (4C8G) | 阿里云/腾讯云ECS | ¥300-500 |
| 讯飞ASR | 按量（~10万次/月） | ¥200-400 |
| DeepSeek LLM | 按token（~10万次对话） | ¥500-1000 |
| 讯飞TTS | 按字符（~500万字/月） | ¥300-500 |
| 阿里云Embedding | 首次向量化 + 增量 | ¥100-200 |
| 和风天气 | 1000次/天免费 | ¥0 |
| 高德地图 | 5000次/天免费 | ¥0 |
| 域名+SSL | 两个域名 | ¥50-100 |
| **合计月费** | | **¥1450-2700** |
| **单人单次伴游** | | **< ¥0.10** |

### 12.2 一次性投入

| 项目 | 费用 |
|------|------|
| 服务器首年（包年折扣）| ¥2500-4000 |
| Live2D Cubism SDK 授权 | 免费（Indie版） |
| Apple开发者账号 | ¥688/年 |
| Android应用商店 | ¥0（国内主流） |
| **合计首年** | **¥3200-4700** |

> **对比原需求文档方案**（含GPU实例+iBeacon硬件），月费从¥3400+降至¥1450-2700，硬件投入从¥750降至¥0。毛利率空间极为充裕。

---

> **文档结束** | 版本 v2.0 | 2026-06-04
