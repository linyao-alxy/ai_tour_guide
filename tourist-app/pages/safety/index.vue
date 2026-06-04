<script setup lang="ts">
import { ref } from 'vue'

const isMonitoring = ref(false)

const toggleMonitor = () => {
  isMonitoring.value = !isMonitoring.value
  if (isMonitoring.value) {
    uni.startAccelerometer({ interval: 'ui' })
    uni.onAccelerometerChange((res) => {
      // TODO: 加速度计跌倒检测算法
    })
  } else {
    uni.stopAccelerometer()
  }
}

const emergencyCall = () => {
  uni.request({
    url: '/api/v1/tourist/safety/emergency-call',
    method: 'POST',
    data: { userId: 1, lat: 0, lng: 0 }
  })
  uni.showToast({ title: '已发送求助信号', icon: 'none' })
}
</script>

<template>
  <view class="safety-page">
    <view class="status-card">
      <text class="status-title">{{ isMonitoring ? '🟢 安全守护中' : '⚪ 未开启' }}</text>
      <text class="status-desc">开启后将实时监测跌倒风险</text>
    </view>

    <button :class="['monitor-btn', isMonitoring ? 'active' : '']" @click="toggleMonitor">
      {{ isMonitoring ? '停止守护' : '开启安全守护' }}
    </button>

    <button class="emergency-btn" @click="emergencyCall">🆘 一键呼救</button>
  </view>
</template>

<style scoped>
.safety-page { padding:40rpx; }
.status-card { background:#fff; border-radius:16rpx; padding:40rpx; text-align:center; margin-bottom:40rpx; }
.status-title { font-size:36rpx; font-weight:bold; }
.status-desc { font-size:26rpx; color:#999; margin-top:12rpx; }
.monitor-btn { height:96rpx; line-height:96rpx; border-radius:48rpx; font-size:32rpx; margin-bottom:24rpx; }
.monitor-btn.active { background:#67c23a; color:#fff; }
.emergency-btn { height:96rpx; line-height:96rpx; border-radius:48rpx; background:#f56c6c; color:#fff; font-size:32rpx; }
</style>
