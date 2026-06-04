<template>
  <div class="safety-page">
    <div class="status-card"><p class="status-title">🟢 安全守护中</p><p class="status-desc">开启后将实时监测跌倒风险</p></div>
    <button class="monitor-btn active">停止守护</button>
    <button class="emergency-btn">🆘 一键呼救</button>
  </div>
</template>
<style scoped>
.safety-page { padding:32px; max-width:480px; margin:0 auto; }
.status-card { background:#fff; border-radius:12px; padding:32px; text-align:center; margin-bottom:32px; }
.status-title { font-size:22px; font-weight:bold; }
.status-desc { font-size:14px; color:#999; margin-top:8px; }
.monitor-btn { width:100%; height:52px; border-radius:26px; font-size:18px; border:none; margin-bottom:16px; }
.monitor-btn.active { background:#67c23a; color:#fff; }
.emergency-btn { width:100%; height:52px; border-radius:26px; background:#f56c6c; color:#fff; font-size:18px; border:none; }
</style>
