<template>
  <div class="guide-page">
    <div class="weather-bar" v-if="weather.temp">
      <span>{{ weatherIcon }}</span><span>{{ weather.condition }} {{ weather.temp }}°C</span>
      <span class="weather-alert" v-if="weather.alert">⚠️ {{ weather.alert.type }}</span>
      <span class="costume-switch" @click="cycleCostume">🎭 换装</span>
    </div>
    <div class="avatar-area">
      <svg viewBox="0 0 360 440" class="guide-svg" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <linearGradient id="skyG" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" :stop-color="c.bgTop"/><stop offset="40%" stop-color="#E3F2FD"/><stop offset="100%" :stop-color="c.bgBottom"/></linearGradient>
          <radialGradient id="blushL"><stop offset="0%" :stop-color="c.blush" stop-opacity=".5"/><stop offset="100%" :stop-color="c.skin" stop-opacity="0"/></radialGradient>
          <radialGradient id="blushR"><stop offset="0%" :stop-color="c.blush" stop-opacity=".5"/><stop offset="100%" :stop-color="c.skin" stop-opacity="0"/></radialGradient>
          <filter id="sd"><feDropShadow dx="0" dy="2" stdDeviation="4" flood-opacity=".1"/></filter>
        </defs>
        <rect width="360" height="440" fill="url(#skyG)"/>
        <g opacity=".5"><ellipse cx="70" cy="55" rx="35" ry="12" fill="#fff"/><ellipse cx="85" cy="50" rx="25" ry="10" fill="#fff"/><ellipse cx="280" cy="75" rx="30" ry="10" fill="#fff"/><ellipse cx="295" cy="70" rx="22" ry="9" fill="#fff"/></g>
        <path d="M0 380 Q60 300 120 380 Q180 310 240 380 Q300 320 360 380 L360 440 L0 440Z" fill="#A5D6A7" opacity=".45"/>
        <path d="M0 400 Q80 330 160 400 Q240 350 320 400 L360 400 L360 440 L0 440Z" fill="#81C784" opacity=".6"/>
        <ellipse cx="180" cy="430" rx="200" ry="40" fill="#66BB6A" opacity=".55"/>
        <g filter="url(#sd)">
          <animateTransform attributeName="transform" type="translate" values="0,0;0,-3;0,0" dur="4s" repeatCount="indefinite"/>
          <path d="M133 280 Q118 340 108 420 L252 420 Q242 340 227 280Z" :fill="c.robe">
            <animateTransform attributeName="transform" type="scale" values="1,1;1.006,1.008;1,1" dur="3.5s" repeatCount="indefinite" additive="sum"/>
          </path>
          <path d="M163 275 L180 312 L197 275" :fill="c.robeDark" :stroke="c.sash" stroke-width="1.5"/>
          <g><animateTransform attributeName="transform" type="rotate" values="0 120 340;-2 120 340;0 120 340" dur="5s" repeatCount="indefinite"/><path d="M133 280 Q98 290 80 340 Q90 348 115 340 Q125 315 142 302Z" :fill="c.robeDark"/><ellipse cx="94" cy="355" rx="14" ry="12" :fill="c.skin"/></g>
          <g><animateTransform attributeName="transform" type="rotate" :values="gestureVals" :dur="gestureDur" repeatCount="indefinite"/><path d="M227 280 Q262 286 280 325 Q270 335 245 330 Q235 310 218 295Z" :fill="c.robeDark"/><ellipse cx="260" cy="338" rx="13" ry="11" :fill="c.skin"/><text v-if="c.accessory&&c.accessory!=='📿'" :x="c.accessory==='扇'||c.accessory==='🏹'?248:252" y="335" font-size="24" text-anchor="middle">{{ c.accessory }}</text></g>
          <rect x="118" y="335" width="124" height="14" rx="5" :fill="c.sash"/>
        </g>
        <g v-if="c.monkBeads" opacity=".85"><animateTransform attributeName="transform" type="translate" values="0,0;0,-1;0,0" dur="2s" repeatCount="indefinite"/><circle cx="248" cy="320" r="3.5" fill="#6D4C41"/><circle cx="245" cy="328" r="3.5" fill="#795548"/><circle cx="243" cy="337" r="3.5" fill="#6D4C41"/><circle cx="246" cy="345" r="3.5" fill="#795548"/><circle cx="252" cy="350" r="3.5" fill="#6D4C41"/><circle cx="258" cy="347" r="3.5" fill="#795548"/></g>
        <rect x="169" y="266" width="22" height="16" rx="8" :fill="c.skin"/>
        <g><animateTransform attributeName="transform" type="rotate" :values="headNod" :dur="headNodDur" repeatCount="indefinite"/>
          <ellipse cx="180" cy="226" rx="54" ry="60" :fill="c.skin"/><ellipse cx="126" cy="228" rx="10" ry="16" :fill="c.skin"/><ellipse cx="234" cy="228" rx="10" ry="16" :fill="c.skin"/>
          <ellipse cx="180" cy="186" rx="56" ry="35" :fill="c.hair"/><ellipse cx="131" cy="190" rx="13" ry="33" :fill="c.hair"/><ellipse cx="229" cy="190" rx="13" ry="33" :fill="c.hair"/>
          <ellipse v-if="c.hat" cx="180" cy="172" rx="24" ry="11" :fill="c.hair"/>
          <path v-if="c.sideHair" d="M124 210 Q116 240 120 268" :stroke="c.hair" stroke-width="4.5" fill="none" stroke-linecap="round"/><path v-if="c.sideHair" d="M236 210 Q244 240 240 268" :stroke="c.hair" stroke-width="4.5" fill="none" stroke-linecap="round"/>
          <circle cx="146" cy="238" r="18" fill="url(#blushL)"/><circle cx="214" cy="238" r="18" fill="url(#blushR)"/>
          <g><animateTransform attributeName="transform" type="translate" :values="browVals" :dur="browDur" repeatCount="indefinite"/><path d="M144 214 Q156 208 166 214" :stroke="c.hair" stroke-width="2.4" fill="none" stroke-linecap="round"/><path d="M194 214 Q204 208 216 214" :stroke="c.hair" stroke-width="2.4" fill="none" stroke-linecap="round"/></g>
          <g><ellipse cx="155" cy="226" rx="10" ry="11" fill="#fff"/><circle cx="155" cy="227" r="6" fill="#2C1810"/><circle cx="157" cy="225" r="2.2" fill="#fff"/><rect x="144" y="224" width="22" height="6" :fill="c.skin" rx="3" opacity="0"><animate attributeName="height" values="0;0;0;11;0;0;0;0;0;0" dur="4s" repeatCount="indefinite"/><animate attributeName="y" values="224;224;224;220;224;224;224;224;224;224" dur="4s" repeatCount="indefinite"/><animate attributeName="opacity" values="0;0;0;1;0;0;0;0;0;0" dur="4s" repeatCount="indefinite"/></rect></g>
          <g><ellipse cx="205" cy="226" rx="10" ry="11" fill="#fff"/><circle cx="205" cy="227" r="6" fill="#2C1810"/><circle cx="207" cy="225" r="2.2" fill="#fff"/><rect x="194" y="224" width="22" height="6" :fill="c.skin" rx="3" opacity="0"><animate attributeName="height" values="0;0;0;11;0;0;0;0;0;0" dur="4s" repeatCount="indefinite"/><animate attributeName="y" values="224;224;224;220;224;224;224;224;224;224" dur="4s" repeatCount="indefinite"/><animate attributeName="opacity" values="0;0;0;1;0;0;0;0;0;0" dur="4s" repeatCount="indefinite"/></rect></g>
          <path d="M144 217 Q155 213 166 217" :stroke="c.hair" stroke-width="1" fill="none"/><path d="M194 217 Q205 213 216 217" :stroke="c.hair" stroke-width="1" fill="none"/>
          <path d="M180 229 Q182 237 178 242" stroke="#D7A86E" stroke-width="1.8" fill="none" stroke-linecap="round"/>
          <path v-show="!isSpeaking" d="M166 250 Q180 261 194 250" stroke="#D4786E" stroke-width="2.5" :fill="c.lipColor" fill-opacity=".4" stroke-linecap="round"/>
          <g v-show="isSpeaking"><ellipse cx="180" cy="254" rx="10" ry="4" fill="#C97060" opacity=".6"><animate attributeName="ry" values="4;9;5;10;4;8;4" dur="0.8s" repeatCount="indefinite"/><animate attributeName="rx" values="10;9;10;8;10;9;10" dur="0.8s" repeatCount="indefinite"/><animate attributeName="cy" values="254;252;254;251;254;253;254" dur="0.8s" repeatCount="indefinite"/></ellipse><ellipse cx="180" cy="254" rx="7" ry="3" fill="#8B0000" opacity=".3"><animate attributeName="ry" values="3;7;4;8;3;6;3" dur="0.8s" repeatCount="indefinite"/></ellipse></g>
        </g>
        <g v-if="isSpeaking" opacity=".85"><rect v-for="(h,i) in waveHeights" :key="i" :x="278+i*8" :y="212-h" width="4" :height="h" rx="2" fill="#FF7043"><animate attributeName="height" :values="h*.4+';'+h+';'+h*.4" :dur="'0.55s'" :begin="i*0.1+'s'" repeatCount="indefinite"/><animate attributeName="y" :values="(212-h*.4)+';'+(212-h)+';'+(212-h*.4)" :dur="'0.55s'" :begin="i*0.1+'s'" repeatCount="indefinite"/></rect></g>
        <rect x="100" y="398" width="160" height="28" rx="14" fill="rgba(0,0,0,.55)"/><text x="180" y="416" text-anchor="middle" fill="#fff" font-size="11" font-weight="500">{{ costumeLabel }}导游 · 慧心</text><circle cx="242" cy="412" r="4" fill="#69F0AE"/>
      </svg>
    </div>
    <div class="quick-spots"><span class="spot-tag" v-for="s in scenicSpots" :key="s" @click="askSpot(s)">{{ s }}</span></div>
    <div class="chat-area" ref="chatArea"><div class="msg" v-for="(msg,i) in messages" :key="i" :class="msg.role==='USER'?'msg-user':'msg-ai'">{{ msg.content }}</div></div>
    <div class="input-bar"><button class="voice-btn">🎤</button><input v-model="query" placeholder="输入您的问题..." class="text-input" @keyup.enter="sendMsg"/><button class="send-btn" @click="sendMsg">发送</button></div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick, onUnmounted } from 'vue'
const query=ref(''),isSpeaking=ref(false),chatArea=ref<HTMLElement>()
const weather=reactive({temp:28,condition:'晴',alert:null} as any),weatherIcon=ref('☀️')
const scenicSpots=['灵山大照壁','五明桥','佛足坛','五智门','菩提大道','灵山大佛','九龙灌浴','梵宫','五印坛城','曼飞龙塔','拈花广场','梵天花海','香月花街']
const messages=ref<{role:string,content:string}[]>([{role:'ASSISTANT',content:'👋 您好！我是灵山胜境的AI导游慧心，请问有什么可以帮您的？'}])
const costumeIdx=ref(0),costumeNames=['佛教圣地','江南水乡','唐风遗址','西北大漠','西南民族']
const costumes:Record<string,any>={
  '佛教圣地':{skin:'#FFDAB9',hair:'#3E2723',robe:'#C62828',robeDark:'#8B0000',sash:'#FFD700',accessory:'📿',hat:true,sideHair:false,blush:'#FFAB91',lipColor:'#E57373',monkBeads:true,bgTop:'#90CAF9',bgBottom:'#C8E6C9'},
  '江南水乡':{skin:'#FFF0E0',hair:'#1A1A1A',robe:'#42A5F5',robeDark:'#1E88E5',sash:'#E0E0E0',accessory:'☂️',hat:false,sideHair:true,blush:'#F8BBD0',lipColor:'#F06292',monkBeads:false,bgTop:'#B3E5FC',bgBottom:'#E8F5E9'},
  '唐风遗址':{skin:'#FFF5EC',hair:'#2C1810',robe:'#EC407A',robeDark:'#C2185B',sash:'#FFD700',accessory:'扇',hat:false,sideHair:true,blush:'#F48FB1',lipColor:'#E91E63',monkBeads:false,bgTop:'#FFE0B2',bgBottom:'#FFCCBC'},
  '西北大漠':{skin:'#E8C39E',hair:'#1B0E00',robe:'#8D6E63',robeDark:'#5D4037',sash:'#D4A017',accessory:'🏹',hat:false,sideHair:false,blush:'#D7CCC8',lipColor:'#A1887F',monkBeads:false,bgTop:'#FFECB3',bgBottom:'#D7CCC8'},
  '西南民族':{skin:'#FFE4C4',hair:'#1C1108',robe:'#66BB6A',robeDark:'#388E3C',sash:'#FF9800',accessory:'💎',hat:false,sideHair:true,blush:'#FFAB91',lipColor:'#FF7043',monkBeads:false,bgTop:'#C8E6C9',bgBottom:'#FFF9C4'},
}
const costumeLabel=computed(()=>costumeNames[costumeIdx.value])
const c=computed(()=>costumes[costumeLabel.value])
const waveHeights=[10,18,24,18,10]
const headNod=computed(()=>isSpeaking.value?'0 180 230;2 180 230;0 180 230;-1 180 230;0 180 230':'0 180 230')
const headNodDur=computed(()=>isSpeaking.value?'1.5s':'1s')
const gestureVals=computed(()=>isSpeaking.value?'0 240 300;-8 240 300;4 240 300;0 240 300':'0 240 300;-2 240 300;2 240 300;0 240 300')
const gestureDur=computed(()=>isSpeaking.value?'1.2s':'6s')
const browVals=computed(()=>isSpeaking.value?'0,0;0,-1.5;0,0':'0,0')
const browDur=computed(()=>isSpeaking.value?'1.8s':'1s')
const cycleCostume=()=>{costumeIdx.value=(costumeIdx.value+1)%5}
let speakTimer:any=null
const sendMsg=()=>{if(!query.value.trim())return;messages.value.push({role:'USER',content:query.value});const q=query.value;query.value='';isSpeaking.value=true;speakTimer=setTimeout(()=>{messages.value.push({role:'ASSISTANT',content:getAnswer(q)});isSpeaking.value=false;nextTick(()=>{if(chatArea.value)chatArea.value.scrollTop=chatArea.value.scrollHeight})},800+Math.random()*1200)}
onUnmounted(()=>{if(speakTimer)clearTimeout(speakTimer)})
const askSpot=(n:string)=>{query.value='请介绍一下'+n;sendMsg()}
const kb:Record<string,string>={'灵山大佛':'灵山大佛高88米，基座总高101.5米，是世界上最高的青铜释迦牟尼佛像。','九龙灌浴':'九龙灌浴是大型动态音乐群雕，再现佛祖诞生场景。演出：10:00、11:30、14:00、16:00。','梵宫':'灵山梵宫集世界佛教艺术之大成，穹顶有3D立体画、琉璃壁画。','五印坛城':'藏传佛教风格建筑，外观仿布达拉宫。','曼飞龙塔':'南传佛教白塔群，仿西双版纳曼飞龙塔。','灵山大照壁':'长39.8米高7米，青石雕刻"华夏第一壁"。','五明桥':'5座汉白玉拱桥，代表五明智慧。','佛足坛':'巨型青铜佛足印，刻32种吉祥图案。','五智门':'五门六柱汉白玉牌坊，象征五方五佛。','菩提大道':'长约250米，两侧印度菩提树。','拈花广场':'占地8000㎡，12米拈花微笑雕塑。','梵天花海':'占地30000㎡，格桑花波斯菊等。','香月花街':'全长800米，青石板禅意商街。'}
function getAnswer(q:string):string{for(const[k,v]of Object.entries(kb)){if(q.includes(k))return v}if(q.includes('高'))return'灵山大佛高88米，加基座总高101.5米。';if(q.includes('时间'))return'08:00-17:30，九龙灌浴演出10:00、11:30、14:00、16:00。';if(q.includes('门票'))return'成人票210元，学生/老人半价105元。';return'灵山胜境是5A级佛教文化景区，欢迎继续探索！'}
</script>

<style scoped>
.guide-page{display:flex;flex-direction:column;height:100vh;background:#f0f2f5;max-width:480px;margin:0 auto;box-shadow:0 0 30px rgba(0,0,0,.1)}
.weather-bar{padding:6px 14px;background:#fff;font-size:13px;color:#666;display:flex;align-items:center;gap:8px;border-bottom:1px solid #eee;z-index:2}
.weather-alert{color:#e6a23c;font-weight:bold}
.costume-switch{margin-left:auto;font-size:12px;color:#667eea;cursor:pointer;padding:2px 8px;border:1px solid #667eea;border-radius:10px}
.avatar-area{height:400px;overflow:hidden;position:relative}
.guide-svg{width:100%;height:100%;display:block}
.quick-spots{padding:6px 12px;background:#fff;display:flex;gap:8px;overflow-x:auto;white-space:nowrap;border-bottom:1px solid #eee}
.spot-tag{padding:5px 14px;background:#E8F5E9;color:#2E7D32;border-radius:14px;font-size:12px;cursor:pointer;flex-shrink:0;transition:all .2s}
.spot-tag:active{background:#C8E6C9;transform:scale(.95)}
.chat-area{flex:1;padding:12px;overflow-y:auto;display:flex;flex-direction:column}
.msg{max-width:82%;padding:10px 14px;border-radius:14px;margin-bottom:10px;font-size:14px;line-height:1.6;word-break:break-all;animation:fadeIn .3s ease}
.msg-ai{background:#fff;color:#333;align-self:flex-start;border-bottom-left-radius:4px}
.msg-user{background:#409EFF;color:#fff;align-self:flex-end;border-bottom-right-radius:4px}
@keyframes fadeIn{from{opacity:0;transform:translateY(8px)}to{opacity:1;transform:translateY(0)}}
.input-bar{display:flex;align-items:center;padding:10px 12px;background:#fff;border-top:1px solid #e4e7ed;gap:8px}
.voice-btn{width:40px;height:40px;border-radius:50%;background:#f0f0f0;font-size:20px;border:none;cursor:pointer;flex-shrink:0}
.text-input{flex:1;height:40px;background:#f5f5f5;border-radius:20px;padding:0 16px;font-size:14px;border:none;outline:none}
.send-btn{background:#409EFF;color:#fff;border-radius:20px;height:40px;padding:0 18px;font-size:14px;border:none;cursor:pointer;flex-shrink:0}
</style>
