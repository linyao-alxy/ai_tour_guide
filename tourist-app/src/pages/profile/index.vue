<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-circle">{{ (userStore.nickname||'游').charAt(0) }}</div>
      <h2>{{ userStore.nickname || '游客' }}</h2>
      <p>{{ userStore.phone || '未绑定手机' }}</p>
    </div>
    <div class="section"><h3>🎯 旅行偏好</h3><div class="tag-group"><span class="tag" v-for="t in interests" :key="t.value" :class="{active:prefs.interests.includes(t.value)}" @click="toggle(t.value)">{{ t.icon }} {{ t.label }}</span></div></div>
    <div class="section"><h3>🚶 游览节奏</h3><div class="tag-group"><span class="tag" v-for="p in paces" :key="p.value" :class="{active:prefs.pace===p.value}" @click="prefs.pace=p.value">{{ p.icon }} {{ p.label }}</span></div></div>
    <div class="section"><h3>♿ 特殊需求</h3><div class="switch-row"><span>无障碍通道优先</span><span class="toggle" :class="{on:prefs.accessibility}" @click="prefs.accessibility=!prefs.accessibility"></span></div></div>
    <div class="section"><h3>🆘 紧急联系人</h3><input class="text-input" v-model="prefs.emergencyContact" placeholder="请输入紧急联系人电话" /></div>
    <div class="actions"><button class="save-btn" @click="save">💾 保存设置</button><button class="logout-btn" @click="logout">退出登录</button></div>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
const router=useRouter(); const userStore=useUserStore()
const interests=[{value:'history',label:'历史文化',icon:'🏛️'},{value:'nature',label:'自然风光',icon:'🏔️'},{value:'family',label:'亲子出游',icon:'👨‍👩‍👧'},{value:'elder',label:'银发慢行',icon:'🧓'},{value:'photo',label:'拍照打卡',icon:'📸'},{value:'food',label:'美食探索',icon:'🍜'}]
const paces=[{value:'slow',label:'慢行细品',icon:'🐢'},{value:'normal',label:'适中节奏',icon:'🚶'},{value:'fast',label:'快速游览',icon:'🏃'}]
const prefs=reactive({interests:[] as string[],pace:'normal',accessibility:false,emergencyContact:''})
onMounted(()=>{if(userStore.profile)Object.assign(prefs,userStore.profile)})
const toggle=(v:string)=>{const i=prefs.interests.indexOf(v);i>=0?prefs.interests.splice(i,1):prefs.interests.push(v)}
const save=()=>{userStore.updateProfile({...prefs})}
const logout=()=>{userStore.logout();router.push('/login')}
</script>

<style scoped>
.profile-page{max-width:480px;margin:0 auto;padding:20px;min-height:100vh;background:#f5f5f5}
.profile-header{text-align:center;padding:30px 0 20px;background:#fff;border-radius:16px;margin-bottom:16px}
.avatar-circle{width:64px;height:64px;border-radius:50%;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;font-size:28px;display:flex;align-items:center;justify-content:center;margin:0 auto 12px;font-weight:bold}
.profile-header h2{font-size:20px;color:#333;margin:0}
.profile-header p{font-size:13px;color:#999;margin:6px 0 0}
.section{background:#fff;border-radius:14px;padding:18px;margin-bottom:14px}
.section h3{font-size:15px;color:#333;margin:0 0 14px}
.tag-group{display:flex;flex-wrap:wrap;gap:10px}
.tag{padding:8px 16px;background:#f0f0f0;border-radius:20px;font-size:13px;color:#666;cursor:pointer;transition:all .2s;border:2px solid transparent}
.tag.active{background:#e8ecff;color:#667eea;border-color:#667eea;font-weight:600}
.switch-row{display:flex;justify-content:space-between;align-items:center;font-size:14px;color:#666}
.toggle{width:48px;height:26px;border-radius:13px;background:#d0d0d0;position:relative;cursor:pointer;transition:all .3s}
.toggle::after{content:'';position:absolute;top:2px;left:2px;width:22px;height:22px;border-radius:50%;background:#fff;transition:all .3s}
.toggle.on{background:#67c23a}.toggle.on::after{left:24px}
.text-input{width:100%;height:44px;border:1px solid #e0e0e0;border-radius:10px;padding:0 14px;font-size:14px;outline:none;box-sizing:border-box}
.text-input:focus{border-color:#667eea}
.actions{display:flex;flex-direction:column;gap:12px;margin-top:20px}
.save-btn{width:100%;height:48px;border:none;border-radius:12px;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;font-size:16px;font-weight:600;cursor:pointer}
.logout-btn{width:100%;height:44px;border:1px solid #e0e0e0;border-radius:12px;background:#fff;color:#999;font-size:14px;cursor:pointer}
</style>
