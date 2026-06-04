<template>
  <el-container class="layout">
    <el-aside width="230px" class="sidebar">
      <div class="logo-area"><span class="logo-icon">🏯</span><span class="logo-text">AI导游管理</span></div>
      <el-menu :default-active="route.path" router background-color="transparent" text-color="rgba(255,255,255,.75)" active-text-color="#fff" class="side-menu">
        <el-menu-item index="/dashboard"><el-icon><DataAnalysis /></el-icon><span>数据大屏</span></el-menu-item>
        <el-sub-menu index="knowledge"><template #title><el-icon><Document /></el-icon><span>知识库管理</span></template>
          <el-menu-item index="/knowledge">知识条目</el-menu-item>
          <el-menu-item index="/knowledge/upload">文档上传</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/avatar"><el-icon><UserFilled /></el-icon><span>形象配置</span></el-menu-item>
        <el-menu-item index="/analytics"><el-icon><TrendCharts /></el-icon><span>数据分析</span></el-menu-item>
        <el-menu-item index="/emergency"><el-icon><Warning /></el-icon><span>应急监控</span></el-menu-item>
        <el-menu-item index="/scenic"><el-icon><MapLocation /></el-icon><span>景区配置</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <span class="page-title">{{ route.meta.title }}</span>
        <div class="topbar-right">
          <el-avatar :size="32" icon="UserFilled" />
          <span class="admin-name">管理员</span>
          <el-button text @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main-content"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { DataAnalysis,Document,UserFilled,TrendCharts,Warning,MapLocation } from '@element-plus/icons-vue'
const router = useRouter(); const route = useRoute()
const logout = ()=>{ localStorage.removeItem('admin_token'); router.push('/login') }
</script>

<style scoped>
.layout{height:100vh}
.sidebar{background:linear-gradient(180deg,#1a1a2e 0%,#16213e 100%);overflow-y:auto;border-right:none}
.logo-area{display:flex;align-items:center;gap:10px;padding:20px 16px;border-bottom:1px solid rgba(255,255,255,.08)}
.logo-icon{font-size:28px}
.logo-text{font-size:16px;font-weight:bold;color:#fff}
.side-menu{border-right:none}
.side-menu .el-menu-item:hover,.side-menu .el-sub-menu__title:hover{background:rgba(255,255,255,.08)!important}
.side-menu .el-menu-item.is-active{background:linear-gradient(90deg,rgba(102,126,234,.4),transparent)!important;border-right:3px solid #667eea}
.topbar{display:flex;justify-content:space-between;align-items:center;background:#fff;border-bottom:1px solid #e8e8e8;padding:0 24px;height:56px}
.page-title{font-size:16px;font-weight:600;color:#333}
.topbar-right{display:flex;align-items:center;gap:10px}
.admin-name{font-size:13px;color:#666}
.main-content{background:#f0f2f5;padding:0;min-height:calc(100vh - 56px)}
</style>
