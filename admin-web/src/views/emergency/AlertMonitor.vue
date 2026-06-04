<template>
  <div class="emergency-page">
    <div class="page-header"><h2>🚨 应急告警监控</h2><el-tag type="success">当前无活跃告警</el-tag></div>
    <div class="alert-card"><el-empty description="暂无告警记录" :image-size="120" /></div>
  </div>
</template>

<style scoped>
.emergency-page{padding:24px}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
.page-header h2{font-size:18px;color:#333;margin:0}
.alert-card{background:#fff;border-radius:12px;padding:40px;box-shadow:0 2px 8px rgba(0,0,0,.04)}
</style>
