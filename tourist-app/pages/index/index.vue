<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleStartGuide = () => { router.push('/guide') }

onMounted(() => { console.log('App 已启动') })
</script>

<template>
  <div class="home">
    <div class="hero">
      <img class="logo" src="/vite.svg" alt="logo" />
      <p class="title">AI数字人导游</p>
      <p class="subtitle">您的专属智慧伴游助手</p>
    </div>

    <div class="actions">
      <button class="btn-primary" @click="handleStartGuide">🎙️ 开始伴游</button>
      <button class="btn-secondary" @click="router.push('/map')">🗺️ 查看地图</button>
      <button class="btn-secondary" @click="router.push('/safety')">🛡️ 安全守护</button>
    </view>

    <!-- 用户画像快速选择 -->
    <div class="profile-tags">
      <p class="tag-label">选择您的偏好（可选）:</p>
      <div class="tag-group">
        <span class="tag" v-for="tag in ['历史文化','自然风光','亲子出游','银发慢行','拍照打卡']" :key="tag">{{ tag }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home { padding: 40rpx; }
.hero { display:flex; flex-direction:column; align-items:center; margin:60rpx 0; }
.logo { width:80px; height:80px; border-radius:50%; background:#667eea; }
.title { font-size:48rpx; font-weight:bold; color:#333; margin-top:20rpx; }
.subtitle { font-size:28rpx; color:#999; margin-top:12rpx; }
.actions { display:flex; flex-direction:column; gap:20rpx; margin:40rpx 0; }
.btn-primary { background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; border-radius:48rpx; height:96rpx; line-height:96rpx; font-size:32rpx; }
.btn-secondary { background:#f5f5f5; color:#666; border-radius:48rpx; height:88rpx; line-height:88rpx; font-size:28rpx; }
.profile-tags { margin-top:40rpx; }
.tag-label { font-size:26rpx; color:#999; }
.tag-group { display:flex; flex-wrap:wrap; gap:16rpx; margin-top:16rpx; }
.tag { padding:12rpx 28rpx; background:#e8f4fd; color:#409EFF; border-radius:32rpx; font-size:26rpx; }
</style>
