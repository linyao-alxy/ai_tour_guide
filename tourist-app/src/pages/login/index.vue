<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-container">
      <div class="login-card">
        <div class="brand">
          <div class="brand-icon">🏯</div>
          <h1>AI数字人导游</h1>
          <p>智慧文旅伴游助手</p>
        </div>

        <!-- 登录方式切换 -->
        <div class="login-tabs">
          <span :class="{active:mode==='phone'}" @click="mode='phone'">手机登录</span>
          <span :class="{active:mode==='wechat'}" @click="mode='wechat'">微信登录</span>
        </div>

        <!-- 手机登录 -->
        <div v-if="mode==='phone'" class="form-area">
          <div class="input-group">
            <span class="prefix">+86</span>
            <input v-model="phone" placeholder="请输入手机号" maxlength="11" class="phone-input" />
          </div>
          <div class="input-group">
            <input v-model="code" placeholder="验证码" maxlength="6" class="code-input" />
            <button class="code-btn" @click="sendCode" :disabled="countdown>0">
              {{ countdown>0 ? countdown+'s' : '获取验证码' }}
            </button>
          </div>
          <button class="login-btn" @click="handlePhoneLogin" :disabled="!phone||code.length<4">
            登录 / 注册
          </button>
        </div>

        <!-- 微信登录 -->
        <div v-if="mode==='wechat'" class="form-area">
          <div class="wechat-area">
            <div class="wechat-icon">💬</div>
            <p>点击下方按钮使用微信一键登录</p>
            <button class="wechat-btn" @click="handleWechatLogin">微信授权登录</button>
          </div>
        </div>

        <p class="agreement">登录即同意《用户协议》和《隐私政策》</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const mode = ref('phone')
const phone = ref('')
const code = ref('')
const countdown = ref(0)

const sendCode = () => {
  if (phone.value.length !== 11) return
  countdown.value = 60
  const t = setInterval(() => { countdown.value--; if(countdown.value<=0) clearInterval(t) }, 1000)
  // TODO: 调用短信API
  console.log('验证码已发送(模拟): 000000')
}

const handlePhoneLogin = () => {
  userStore.login({
    id: Date.now(),
    phone: phone.value,
    nickname: '游客' + phone.value.slice(-4),
    token: 'mock_token_' + Date.now()
  })
  router.push('/')
}

const handleWechatLogin = () => {
  userStore.login({
    id: Date.now(),
    openid: 'wx_mock_' + Date.now(),
    nickname: '微信用户',
    token: 'mock_token_wx_' + Date.now()
  })
  router.push('/')
}
</script>

<style scoped>
.login-page { height:100vh; display:flex; position:relative; overflow:hidden; }
.login-bg { position:absolute; inset:0; background:linear-gradient(160deg,#667eea 0%,#764ba2 50%,#f093fb 100%); z-index:0; }
.login-bg::before { content:''; position:absolute; width:300px; height:300px; border-radius:50%; background:rgba(255,255,255,.1); top:-80px; right:-60px; }
.login-bg::after { content:''; position:absolute; width:200px; height:200px; border-radius:50%; background:rgba(255,255,255,.08); bottom:-40px; left:-40px; }
.login-container { position:relative; z-index:1; width:100%; max-width:400px; margin:auto; padding:20px; }
.login-card { background:#fff; border-radius:20px; padding:36px 28px; box-shadow:0 20px 50px rgba(0,0,0,.2); }
.brand { text-align:center; margin-bottom:28px; }
.brand-icon { font-size:52px; margin-bottom:8px; }
.brand h1 { font-size:22px; color:#333; margin:0; }
.brand p { font-size:13px; color:#999; margin:4px 0 0; }
.login-tabs { display:flex; gap:0; margin-bottom:24px; border-radius:10px; overflow:hidden; border:1px solid #e8e8e8; }
.login-tabs span { flex:1; text-align:center; padding:10px 0; font-size:14px; cursor:pointer; background:#f8f8f8; color:#666; transition:all .2s; }
.login-tabs span.active { background:#667eea; color:#fff; font-weight:600; }
.form-area { display:flex; flex-direction:column; gap:14px; }
.input-group { display:flex; align-items:center; background:#f5f5f5; border-radius:10px; overflow:hidden; }
.prefix { padding:0 12px; font-size:14px; color:#666; font-weight:600; border-right:1px solid #e0e0e0; }
.phone-input,.code-input { flex:1; height:48px; border:none; background:transparent; padding:0 14px; font-size:15px; outline:none; }
.code-btn { padding:0 14px; height:48px; background:#667eea; color:#fff; border:none; font-size:13px; cursor:pointer; white-space:nowrap; }
.code-btn:disabled { background:#ccc; }
.login-btn { width:100%; height:48px; border:none; border-radius:10px; background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; font-size:16px; font-weight:600; cursor:pointer; }
.login-btn:disabled { opacity:.5; }
.wechat-area { text-align:center; padding:10px 0; }
.wechat-icon { font-size:48px; margin-bottom:12px; }
.wechat-area p { font-size:13px; color:#999; margin:0 0 20px; }
.wechat-btn { width:100%; height:48px; border:none; border-radius:10px; background:#07C160; color:#fff; font-size:16px; font-weight:600; cursor:pointer; }
.agreement { text-align:center; font-size:11px; color:#bbb; margin:20px 0 0; }
</style>
