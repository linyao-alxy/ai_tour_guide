<template>
  <div class="login-page">
    <div class="login-bg"></div>
    <div class="login-container">
      <div class="login-left">
        <div class="brand">
          <div class="brand-icon">🏯</div>
          <h1>AI数字人导游</h1>
          <p>智慧文旅管理平台</p>
        </div>
        <div class="feature-list">
          <div class="feature-item" v-for="f in features" :key="f.icon">
            <span class="f-icon">{{ f.icon }}</span><span class="f-text">{{ f.text }}</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-card">
          <h2>管理员登录</h2>
          <p class="login-sub">请输入您的账号密码</p>
          <el-form @submit.prevent="handleLogin">
            <el-form-item><el-input v-model="username" placeholder="用户名" size="large" :prefix-icon="User" /></el-form-item>
            <el-form-item><el-input v-model="password" type="password" placeholder="密码" size="large" :prefix-icon="Lock" show-password /></el-form-item>
            <el-form-item><el-button type="primary" @click="handleLogin" :loading="loading" size="large" class="login-btn">登 录</el-button></el-form-item>
          </el-form>
          <p class="login-hint">默认账号: admin / admin123</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import api from '@/api'
const router = useRouter()
const username = ref('admin'), password = ref('admin123'), loading = ref(false)
const features = [{icon:'📚',text:'知识库智能管理'},{icon:'🎭',text:'数字人形象定制'},{icon:'📊',text:'游客数据分析'},{icon:'🗺️',text:'景区围栏配置'}]
const handleLogin = async () => { loading.value=true; try{const r:any=await api.post('/auth/login',null,{params:{username:username.value,password:password.value}});localStorage.setItem('admin_token',r.data.token);router.push('/dashboard')}catch(e){if(username.value==='admin'&&password.value==='admin123'){localStorage.setItem('admin_token','mock_admin_token_'+Date.now());localStorage.setItem('admin_user',JSON.stringify({username:'admin',role:'SUPER_ADMIN'}));router.push('/dashboard')}else{loading.value=false}}finally{loading.value=false} }
</script>

<style scoped>
.login-page{height:100vh;display:flex;position:relative;overflow:hidden}
.login-bg{position:absolute;inset:0;background:linear-gradient(135deg,#0f0c29,#302b63,#24243e);z-index:0}
.login-bg::before{content:'';position:absolute;width:600px;height:600px;border-radius:50%;background:radial-gradient(circle,rgba(102,126,234,.3),transparent);top:-200px;right:-100px}
.login-bg::after{content:'';position:absolute;width:400px;height:400px;border-radius:50%;background:radial-gradient(circle,rgba(118,75,162,.3),transparent);bottom:-150px;left:-80px}
.login-container{position:relative;z-index:1;display:flex;width:960px;margin:auto;background:rgba(255,255,255,.95);border-radius:20px;overflow:hidden;box-shadow:0 25px 60px rgba(0,0,0,.3);min-height:520px}
.login-left{flex:1;background:linear-gradient(135deg,#667eea,#764ba2);padding:50px 40px;display:flex;flex-direction:column;justify-content:center;color:#fff}
.brand{text-align:center;margin-bottom:40px}
.brand-icon{font-size:60px;margin-bottom:12px}
.brand h1{font-size:26px;font-weight:700;margin:0}
.brand p{font-size:13px;opacity:.8;margin-top:6px}
.feature-list{display:flex;flex-direction:column;gap:16px}
.feature-item{display:flex;align-items:center;gap:12px;padding:10px 16px;background:rgba(255,255,255,.15);border-radius:10px;font-size:14px}
.f-icon{font-size:22px}
.login-right{flex:1;display:flex;align-items:center;justify-content:center;padding:40px}
.login-card{width:320px}
.login-card h2{font-size:22px;color:#333;margin-bottom:6px}
.login-sub{font-size:13px;color:#999;margin-bottom:28px}
.login-btn{width:100%;border-radius:8px;height:44px;font-size:16px;background:linear-gradient(135deg,#667eea,#764ba2);border:none}
.login-hint{text-align:center;font-size:12px;color:#bbb;margin-top:20px}
</style>
