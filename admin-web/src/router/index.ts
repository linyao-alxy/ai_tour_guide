import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/LoginView.vue')
    },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/dashboard/RealTimeBoard.vue'), meta: { title: '数据大屏' } },
        { path: 'knowledge', component: () => import('@/views/knowledge/KnowledgeList.vue'), meta: { title: '知识库管理' } },
        { path: 'knowledge/upload', component: () => import('@/views/knowledge/DocumentUpload.vue'), meta: { title: '文档上传' } },
        { path: 'avatar', component: () => import('@/views/avatar/AppearanceConfig.vue'), meta: { title: '形象配置' } },
        { path: 'analytics', component: () => import('@/views/analytics/SentimentReport.vue'), meta: { title: '数据分析' } },
        { path: 'emergency', component: () => import('@/views/emergency/AlertMonitor.vue'), meta: { title: '应急监控' } },
        { path: 'scenic', component: () => import('@/views/scenic/GeofenceConfig.vue'), meta: { title: '景区配置' } }
      ]
    }
  ]
})

export default router
