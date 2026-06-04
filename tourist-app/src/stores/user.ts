import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserProfile {
  interests: string[]; pace: string; accessibility: boolean; emergencyContact: string
}

export const useUserStore = defineStore('user', () => {
  const id = ref(0); const nickname = ref(''); const phone = ref(''); const token = ref('')
  const profile = ref<UserProfile|null>(null)
  const isLoggedIn = computed(()=>!!token.value)

  function init(){const s=localStorage.getItem('user_store');if(s){try{const d=JSON.parse(s);id.value=d.id||0;nickname.value=d.nickname||'';phone.value=d.phone||'';token.value=d.token||'';profile.value=d.profile||null}catch(e){}}}
  function persist(){localStorage.setItem('user_store',JSON.stringify({id:id.value,nickname:nickname.value,phone:phone.value,token:token.value,profile:profile.value}))}
  function login(u:{id:number;nickname:string;phone?:string;token:string}){id.value=u.id;nickname.value=u.nickname;phone.value=u.phone||'';token.value=u.token;persist()}
  function updateProfile(p:UserProfile){profile.value=p;persist()}
  function logout(){id.value=0;nickname.value='';phone.value='';token.value='';profile.value=null;localStorage.removeItem('user_store')}
  init()
  return {id,nickname,phone,token,profile,isLoggedIn,login,updateProfile,logout}
})
