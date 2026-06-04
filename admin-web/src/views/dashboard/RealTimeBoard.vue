<template>
  <div class="dashboard">
    <div class="page-header"><h2><el-icon><DataAnalysis /></el-icon> 实时运营大屏</h2><div class="header-right"><el-tag type="success" size="large">🟢 运行中</el-tag><span class="update-time">{{ time }}</span></div></div>
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="c in stats" :key="c.label"><div class="stat-card" :style="{borderTopColor:c.color}"><div class="stat-icon">{{ c.icon }}</div><div class="stat-info"><p class="stat-value">{{ c.value }}</p><p class="stat-label">{{ c.label }}</p></div></div></el-col>
    </el-row>
    <el-row :gutter="20"><el-col :span="14"><div class="chart-card"><h3>📈 服务趋势</h3><div ref="lc" class="chart-box"></div></div></el-col><el-col :span="10"><div class="chart-card"><h3>🔥 热词云</h3><div class="word-cloud"><span v-for="w in words" :key="w.text" :style="{fontSize:w.size+'px',color:w.color}">{{ w.text }}</span></div></div></el-col></el-row>
    <el-row :gutter="20">
      <el-col :span="12"><div class="chart-card"><h3>📍 景点热度</h3><div class="rank-list"><div class="rank-item" v-for="(s,i) in spots" :key="s.name"><span class="rk" :class="'t'+(i+1)">{{ i+1 }}</span><span class="rn">{{ s.name }}</span><el-progress :percentage="s.pct" :stroke-width="8" :color="s.color" style="flex:1" /></div></div></div></el-col>
      <el-col :span="12"><div class="chart-card"><h3>💬 最新咨询</h3><div class="chat-log"><div class="log-item" v-for="l in logs" :key="l.time"><span class="lr">{{ l.role==='游客'?'🧑':'🤖' }}</span><span class="lt">{{ l.text }}</span><span class="ltm">{{ l.time }}</span></div></div></div></el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { DataAnalysis } from '@element-plus/icons-vue'
const time = ref(''), lc = ref()
const stats = ref([{icon:'👥',label:'当前在线',value:128,color:'#667eea'},{icon:'💬',label:'今日会话',value:1456,color:'#67c23a'},{icon:'⚡',label:'平均延迟',value:'0.98s',color:'#e6a23c'},{icon:'😊',label:'满意度',value:'94.2%',color:'#f56c6c'}])
const words = ref([{text:'灵山大佛',size:28,color:'#667eea'},{text:'九龙灌浴',size:22,color:'#764ba2'},{text:'门票',size:20,color:'#e6a23c'},{text:'开放时间',size:18,color:'#67c23a'},{text:'梵宫',size:26,color:'#409EFF'},{text:'交通',size:16,color:'#f56c6c'},{text:'洗手间',size:17,color:'#909399'},{text:'素食',size:19,color:'#ff9800'}])
const spots = ref([{name:'灵山大佛',pct:92,color:'#667eea'},{name:'九龙灌浴',pct:85,color:'#764ba2'},{name:'梵宫',pct:78,color:'#409EFF'},{name:'五印坛城',pct:65,color:'#67c23a'},{name:'菩提大道',pct:58,color:'#e6a23c'}])
const logs = ref([{role:'游客',text:'灵山大佛有多高？',time:'14:28'},{role:'AI',text:'灵山大佛高88米，世界上最高的青铜释迦牟尼佛像...',time:'14:28'},{role:'游客',text:'九龙灌浴几点演出？',time:'14:25'},{role:'AI',text:'10:00、11:30、14:00、16:00',time:'14:25'}])
onMounted(async()=>{await nextTick();if(lc.value){const c=echarts.init(lc.value);c.setOption({tooltip:{trigger:'axis'},legend:{data:['咨询量','满意度']},grid:{left:40,right:20,top:30,bottom:20},xAxis:{data:['周一','周二','周三','周四','周五','周六','周日']},yAxis:[{type:'value',name:'咨询量'},{type:'value',name:'满意度%',min:80,max:100}],series:[{name:'咨询量',type:'bar',data:[1200,1350,1100,1480,1560,2100,1880],itemStyle:{color:'#667eea'},barWidth:24},{name:'满意度',type:'line',yAxisIndex:1,data:[92,93,91,94,95,96,94],smooth:true,itemStyle:{color:'#67c23a'}}]});window.addEventListener('resize',()=>c.resize())};time.value=new Date().toLocaleTimeString('zh-CN');setInterval(()=>{time.value=new Date().toLocaleTimeString('zh-CN')},30000)})
</script>

<style scoped>
.dashboard{padding:20px;background:#f0f2f5;min-height:100%}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
.page-header h2{font-size:20px;color:#333;display:flex;align-items:center;gap:8px;margin:0}
.header-right{display:flex;align-items:center;gap:12px}
.update-time{font-size:12px;color:#999}
.stat-row{margin-bottom:20px}
.stat-card{background:#fff;border-radius:12px;padding:20px;border-top:3px solid;display:flex;align-items:center;gap:16px;box-shadow:0 2px 8px rgba(0,0,0,.04);transition:transform .2s}
.stat-card:hover{transform:translateY(-2px);box-shadow:0 4px 16px rgba(0,0,0,.08)}
.stat-icon{font-size:36px}
.stat-value{font-size:28px;font-weight:700;color:#333;margin:0}
.stat-label{font-size:13px;color:#999;margin:4px 0 0}
.chart-card{background:#fff;border-radius:12px;padding:20px;box-shadow:0 2px 8px rgba(0,0,0,.04);margin-bottom:20px}
.chart-card h3{font-size:15px;color:#333;margin:0 0 16px}
.chart-box{height:280px}
.word-cloud{display:flex;flex-wrap:wrap;gap:10px;align-items:center;justify-content:center;padding:20px;min-height:260px}
.rank-list{display:flex;flex-direction:column;gap:14px}
.rank-item{display:flex;align-items:center;gap:10px}
.rk{width:24px;height:24px;border-radius:6px;display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:bold;color:#fff;background:#909399;flex-shrink:0}
.rk.t1{background:#f56c6c}.rk.t2{background:#e6a23c}.rk.t3{background:#409EFF}
.rn{width:80px;font-size:13px;color:#666;flex-shrink:0}
.chat-log{display:flex;flex-direction:column;gap:10px;max-height:180px;overflow-y:auto}
.log-item{display:flex;align-items:flex-start;gap:8px;font-size:12px}
.lr{flex-shrink:0;width:20px}
.lt{flex:1;color:#666;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.ltm{color:#bbb;flex-shrink:0}
</style>
