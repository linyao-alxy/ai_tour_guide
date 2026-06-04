<template>
  <div class="home">
    <!-- 用户问候 -->
    <div class="user-bar">
      <div class="user-avatar" @click="$router.push('/profile')">{{ (userStore.nickname||'游').charAt(0) }}</div>
      <div class="user-info">
        <p class="greeting">{{ greeting }}</p>
        <p class="user-name">{{ userStore.nickname || '游客' }}</p>
      </div>
      <div class="header-actions">
        <span class="header-btn" @click="$router.push('/profile')">⚙️</span>
      </div>
    </div>

    <!-- 偏好标签(已设置) -->
    <div class="pref-bar" v-if="userStore.profile?.interests?.length">
      <span class="pref-tag-sm" v-for="i in userStore.profile.interests" :key="i">{{ getInterestLabel(i) }}</span>
      <span class="edit-pref" @click="$router.push('/profile')">编辑</span>
    </div>

    <div class="hero">
      <div class="logo">🏯</div>
      <p class="title">AI数字人导游</p>
      <p class="subtitle">您的专属智慧伴游助手</p>
    </div>

    <div class="actions">
      <button class="btn-primary" @click="$router.push('/guide')">🎙️ 开始伴游</button>
      <button class="btn-secondary" @click="$router.push('/map')">🗺️ 查看地图</button>
      <button class="btn-secondary" @click="$router.push('/safety')">🛡️ 安全守护</button>
    </div>

    <!-- 快捷偏好设置提示 -->
    <div class="quick-prefs" v-if="!userStore.profile?.interests?.length">
      <p class="tag-label">选择您的偏好，获取个性化推荐：</p>
      <div class="tag-group">
        <span class="tag" v-for="t in quickTags" :key="t.value" @click="setQuickPref(t.value)">{{ t.icon }} {{ t.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
const router = useRouter()
const userStore = useUserStore()

const hour = new Date().getHours()
const greeting = computed(() => hour<12?'早上好':hour<18?'下午好':'晚上好')

const quickTags = [
  { value:'history', label:'历史文化', icon:'🏛️' },
  { value:'nature', label:'自然风光', icon:'🏔️' },
  { value:'family', label:'亲子出游', icon:'👨‍👩‍👧' },
  { value:'elder', label:'银发慢行', icon:'🧓' },
  { value:'photo', label:'拍照打卡', icon:'📸' },
]

const labelMap:Record<string,string> = { history:'🏛️历史文化', nature:'🏔️自然风光', family:'👨‍👩‍👧亲子', elder:'🧓银发慢行', photo:'📸拍照', food:'🍜美食' }
const getInterestLabel = (v:string) => labelMap[v] || v
const setQuickPref = (v:string) => { userStore.updateProfile({interests:[v],pace:'normal',accessibility:false,emergencyContact:''}) }
</script>

<style scoped>
.home{padding:20px;max-width:480px;margin:0 auto;min-height:100vh;background:#f5f5f5}
.user-bar{display:flex;align-items:center;gap:12px;background:#fff;padding:14px 16px;border-radius:14px;margin-bottom:12px}
.user-avatar{width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;display:flex;align-items:center;justify-content:center;font-size:18px;font-weight:bold;cursor:pointer}
.user-info{flex:1}
.greeting{font-size:12px;color:#999;margin:0}
.user-name{font-size:16px;color:#333;font-weight:600;margin:2px 0 0}
.header-actions{display:flex;gap:8px}
.header-btn{font-size:18px;cursor:pointer;padding:4px}
.pref-bar{display:flex;align-items:center;gap:6px;margin-bottom:12px;flex-wrap:wrap}
.pref-tag-sm{padding:4px 10px;background:#e8ecff;color:#667eea;border-radius:10px;font-size:11px}
.edit-pref{font-size:11px;color:#999;cursor:pointer;margin-left:auto}
.hero{display:flex;flex-direction:column;align-items:center;margin:30px 0}
.logo{width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,#667eea,#764ba2);display:flex;align-items:center;justify-content:center;font-size:40px;color:#fff}
.title{font-size:26px;font-weight:bold;color:#333;margin:16px 0 4px}
.subtitle{font-size:14px;color:#999;margin:0}
.actions{display:flex;flex-direction:column;gap:12px;margin:24px 0}
.btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;border-radius:24px;height:52px;font-size:17px;border:none;cursor:pointer}
.btn-secondary{background:#fff;color:#555;border-radius:24px;height:48px;font-size:15px;border:1px solid #e0e0e0;cursor:pointer}
.quick-prefs{margin-top:20px}
.tag-label{font-size:13px;color:#999;margin-bottom:10px}
.tag-group{display:flex;flex-wrap:wrap;gap:10px}
.tag{padding:8px 16px;background:#f0f0f0;border-radius:20px;font-size:13px;color:#666;cursor:pointer;border:2px solid transparent;transition:all .2s}
.tag:active{background:#e8ecff;color:#667eea;border-color:#667eea}
</style>
