<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useChatStore } from '@/stores/chat'
import { useWeatherStore } from '@/stores/weather'
import { useGPS } from '@/composables/useGPS'

const chatStore = useChatStore()
const weatherStore = useWeatherStore()
const { startTracking, stopTracking } = useGPS()

const messages = ref<any[]>([])
const inputText = ref('')
const isRecording = ref(false)
const weather = ref<any>({})

onMounted(async () => {
  startTracking()
  weather.value = await weatherStore.fetchCurrent(1)
})

onUnmounted(() => { stopTracking() })

const sendText = async () => {
  if (!inputText.value.trim()) return
  messages.value.push({ role: 'USER', content: inputText.value })
  const query = inputText.value
  inputText.value = ''

  // TODO: SSE流式接收
  messages.value.push({ role: 'ASSISTANT', content: '正在思考...' })
}

const toggleRecord = () => { isRecording.value = !isRecording.value }
</script>

<template>
  <view class="guide-page">
    <!-- 天气卡片 -->
    <view class="weather-bar" v-if="weather.temp">
      <text>{{ weather.temp }}°C {{ weather.condition }}</text>
      <text v-if="weather.alert" class="alert">⚠️ {{ weather.alert.type }}</text>
    </view>

    <!-- Live2D画布区域 -->
    <view class="avatar-area">
      <canvas type="webgl" id="live2d-canvas" class="live2d-canvas"></canvas>
    </view>

    <!-- 对话区域 -->
    <scroll-view class="chat-area" scroll-y>
      <view v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role === 'USER' ? 'msg-user' : 'msg-ai']">
        <text>{{ msg.content }}</text>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-bar">
      <button class="voice-btn" @touchstart="toggleRecord" @touchend="toggleRecord">🎤</button>
      <input v-model="inputText" placeholder="输入您的问题..." class="text-input" confirm-type="send" @confirm="sendText" />
      <button class="send-btn" @click="sendText">发送</button>
    </view>
  </view>
</template>

<style scoped>
.guide-page { display:flex; flex-direction:column; height:100vh; background:#f5f5f5; }
.weather-bar { padding:12rpx 24rpx; background:#fff; font-size:24rpx; color:#666; display:flex; gap:16rpx; }
.weather-bar .alert { color:#e6a23c; }
.avatar-area { height:400rpx; background:#000; display:flex; justify-content:center; align-items:center; }
.live2d-canvas { width:100%; height:100%; }
.chat-area { flex:1; padding:16rpx; }
.msg { max-width:80%; padding:16rpx 24rpx; border-radius:16rpx; margin-bottom:16rpx; font-size:28rpx; }
.msg-user { align-self:flex-end; background:#409EFF; color:#fff; }
.msg-ai { align-self:flex-start; background:#fff; color:#333; }
.input-bar { display:flex; align-items:center; padding:16rpx; background:#fff; border-top:1px solid #e4e7ed; }
.voice-btn { width:72rpx; height:72rpx; border-radius:50%; background:#f0f0f0; font-size:36rpx; line-height:72rpx; text-align:center; }
.text-input { flex:1; margin:0 16rpx; height:72rpx; background:#f5f5f5; border-radius:36rpx; padding:0 24rpx; font-size:28rpx; }
.send-btn { background:#409EFF; color:#fff; border-radius:36rpx; height:72rpx; line-height:72rpx; padding:0 32rpx; font-size:28rpx; }
</style>
