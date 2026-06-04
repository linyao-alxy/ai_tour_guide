-- ============================================
-- AI数字人导游系统 - 数据库初始化脚本
-- ============================================

-- 创建Schema
CREATE SCHEMA IF NOT EXISTS tourist;
CREATE SCHEMA IF NOT EXISTS admin;
CREATE SCHEMA IF NOT EXISTS public;

-- ==================== C端 Schema ====================

-- 游客用户
CREATE TABLE tourist.user (
    id            BIGSERIAL PRIMARY KEY,
    openid        VARCHAR(100) UNIQUE,
    phone         VARCHAR(20),
    nickname      VARCHAR(50),
    avatar_url    VARCHAR(500),
    profile_tags  JSONB,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 对话会话
CREATE TABLE tourist.chat_session (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    scenic_id     BIGINT NOT NULL,
    position_log  JSONB,
    started_at    TIMESTAMP DEFAULT NOW(),
    ended_at      TIMESTAMP
);

-- 对话消息
CREATE TABLE tourist.chat_message (
    id            BIGSERIAL PRIMARY KEY,
    session_id    BIGINT NOT NULL,
    role          VARCHAR(10) NOT NULL,
    content       TEXT NOT NULL,
    audio_url     VARCHAR(500),
    chunks_used   JSONB,
    latency_ms    INTEGER,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 景点+围栏 (C端只读)
CREATE TABLE tourist.scenic_spot (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    spot_name     VARCHAR(100) NOT NULL,
    latitude      DECIMAL(12,8) NOT NULL,
    longitude     DECIMAL(12,8) NOT NULL,
    radius        INTEGER DEFAULT 50,
    sort_order    INTEGER DEFAULT 0,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 数字人配置 (C端只读)
CREATE TABLE tourist.avatar_config (
    id              BIGSERIAL PRIMARY KEY,
    scenic_id       BIGINT NOT NULL,
    base_model      VARCHAR(100) NOT NULL,
    costume_layers  JSONB NOT NULL,
    accessory_layers JSONB,
    hair_style      VARCHAR(100),
    color_scheme    VARCHAR(50),
    voice_id        VARCHAR(50) NOT NULL,
    dialect         VARCHAR(20),
    speech_speed    DECIMAL(3,2) DEFAULT 1.0,
    status          SMALLINT DEFAULT 2,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- ==================== B端 Schema ====================

-- 管理员
CREATE TABLE admin.admin_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    scenic_id     BIGINT,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 文档记录
CREATE TABLE admin.document (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    file_name     VARCHAR(200) NOT NULL,
    file_type     VARCHAR(10) NOT NULL,
    file_size     BIGINT,
    minio_path    VARCHAR(500) NOT NULL,
    chunk_count   INTEGER,
    status        VARCHAR(20) DEFAULT 'UPLOADED',
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
    chunk_type    VARCHAR(20),
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

-- Live2D资源
CREATE TABLE admin.avatar_asset (
    id              BIGSERIAL PRIMARY KEY,
    asset_type      VARCHAR(30) NOT NULL,
    asset_name      VARCHAR(100) NOT NULL,
    region_tag      VARCHAR(50),
    file_path       VARCHAR(500) NOT NULL,
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
    latitude      DECIMAL(12,8),
    longitude     DECIMAL(12,8),
    alert_type    VARCHAR(20),
    status        VARCHAR(20) DEFAULT 'PENDING',
    handler_id    BIGINT,
    resolved_at   TIMESTAMP,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 景区配置
CREATE TABLE admin.scenic (
    id            BIGSERIAL PRIMARY KEY,
    scenic_name   VARCHAR(100) NOT NULL,
    province      VARCHAR(50),
    city          VARCHAR(50),
    latitude      DECIMAL(12,8),
    longitude     DECIMAL(12,8),
    address       VARCHAR(300),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- 景点围栏 (B端读写)
CREATE TABLE admin.scenic_spot (
    id            BIGSERIAL PRIMARY KEY,
    scenic_id     BIGINT NOT NULL,
    spot_name     VARCHAR(100) NOT NULL,
    latitude      DECIMAL(12,8) NOT NULL,
    longitude     DECIMAL(12,8) NOT NULL,
    radius        INTEGER DEFAULT 50,
    sort_order    INTEGER DEFAULT 0,
    description   TEXT,
    created_at    TIMESTAMP DEFAULT NOW()
);

-- ==================== 公共Schema ====================

-- 地区特色模板
CREATE TABLE public.region_template (
    id              BIGSERIAL PRIMARY KEY,
    template_name   VARCHAR(50) NOT NULL,
    base_model      VARCHAR(100) NOT NULL,
    costume_layers  JSONB NOT NULL,
    accessory_layers JSONB,
    hair_style      VARCHAR(100),
    color_scheme    VARCHAR(50),
    voice_id        VARCHAR(50),
    dialect         VARCHAR(20),
    created_at      TIMESTAMP DEFAULT NOW()
);

-- ==================== 初始数据 ====================

-- 默认管理员 (密码: admin123)
INSERT INTO admin.admin_user (username, password_hash, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'SUPER_ADMIN');

-- 灵山胜境景区
INSERT INTO admin.scenic (scenic_name, province, city, latitude, longitude, address) VALUES
('灵山胜境', '江苏省', '无锡市', 31.4200, 120.1100, '江苏省无锡市滨湖区马山镇灵山路');

-- 灵山胜境景点围栏
INSERT INTO admin.scenic_spot (scenic_id, spot_name, latitude, longitude, radius, sort_order, description) VALUES
(1, '灵山大佛', 31.4220, 120.1080, 80, 1, '88米高的青铜释迦牟尼佛像，灵山胜境标志性景点'),
(1, '九龙灌浴', 31.4190, 120.1110, 60, 2, '再现佛祖诞生场景的大型动态音乐群雕'),
(1, '梵宫', 31.4170, 120.1130, 100, 3, '集艺术殿堂与佛教文化于一体的宏伟建筑');

-- 地区特色模板
INSERT INTO public.region_template (template_name, base_model, costume_layers, accessory_layers, hair_style, color_scheme, voice_id, dialect) VALUES
('江南水乡', 'gentle_female', '["cheongsam","silk_scarf"]', '["oil_paper_umbrella"]', 'updo', 'pastel', 'xunfei_female_01', 'wu'),
('唐风遗址', 'plump_female', '["qixiong_dress"]', '["round_fan"]', 'high_bun', 'warm_red', 'xunfei_female_03', 'guanzhong'),
('佛教圣地', 'kind_elder', '["monk_robe"]', '["prayer_beads"]', 'monk_hat', 'saffron', 'xunfei_male_02', 'mandarin');
