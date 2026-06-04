import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path:'/login', component:()=>import('@/pages/login/index.vue'), meta:{title:'登录'} },
    { path:'/', component:()=>import('@/pages/index/index.vue'), meta:{title:'首页',requireAuth:true} },
    { path:'/guide', component:()=>import('@/pages/guide/index.vue'), meta:{title:'智能伴游',requireAuth:true} },
    { path:'/map', component:()=>import('@/pages/map/index.vue'), meta:{title:'景区地图'} },
    { path:'/safety', component:()=>import('@/pages/safety/index.vue'), meta:{title:'安全守护'} },
    { path:'/profile', component:()=>import('@/pages/profile/index.vue'), meta:{title:'个人中心'} },
  ]
})

router.beforeEach((to,from,next)=>{
  const userStore=useUserStore()
  if(to.meta.requireAuth&&!userStore.isLoggedIn){next('/login')}
  else if(to.path==='/login'&&userStore.isLoggedIn){next('/')}
  else next()
})

export default router
