<template>
  <div class="analytics-page">
    <div class="page-header"><h2>📊 游客分析报告</h2></div>
    <el-row :gutter="20">
      <el-col :span="12"><div class="chart-card"><h3>满意度趋势</h3><div ref="c1" class="chart-box"></div></div></el-col>
      <el-col :span="12"><div class="chart-card"><h3>情感分布</h3><div ref="c2" class="chart-box"></div></div></el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12"><div class="chart-card"><h3>游客画像</h3><div class="profile-grid"><div class="pf" v-for="p in profiles" :key="p.label"><span class="pf-label">{{ p.label }}</span><el-progress :percentage="p.pct" :stroke-width="10" :color="p.color" /></div></div></div></el-col>
      <el-col :span="12"><div class="chart-card"><h3>热门咨询Top5</h3><div class="top-list"><div class="top-item" v-for="(t,i) in tops" :key="t"><span class="top-num">{{ i+1 }}</span><span>{{ t }}</span></div></div></div></el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref,onMounted,nextTick } from 'vue'
import * as echarts from 'echarts'
const c1=ref(),c2=ref()
const profiles=ref([{label:'历史文化爱好者',pct:38,color:'#667eea'},{label:'亲子出游',pct:25,color:'#67c23a'},{label:'银发慢行',pct:18,color:'#e6a23c'},{label:'自然风光',pct:12,color:'#409EFF'},{label:'拍照打卡',pct:7,color:'#f56c6c'}])
const tops=ref(['灵山大佛有多高','九龙灌浴演出时间','门票多少钱','停车场在哪','素食餐厅推荐'])
onMounted(async()=>{await nextTick();if(c1.value){const c=echarts.init(c1.value);c.setOption({tooltip:{},grid:{left:40,right:10,top:10,bottom:20},xAxis:{data:['5/28','5/29','5/30','5/31','6/1','6/2','6/3']},yAxis:{min:85,max:100},series:[{data:[91,93,90,94,95,93,96],type:'line',smooth:true,areaStyle:{color:'rgba(102,126,234,.15)'},itemStyle:{color:'#667eea'}}]})};if(c2.value){const c=echarts.init(c2.value);c.setOption({tooltip:{},series:[{type:'pie',radius:['50%','75%'],center:['50%','55%'],data:[{value:65,name:'满意'},{value:22,name:'一般'},{value:8,name:'不满'},{value:5,name:'未表态'}],emphasis:{scaleSize:8},itemStyle:{borderRadius:4}}]})}})
</script>

<style scoped>
.analytics-page{padding:24px}
.page-header{margin-bottom:20px}
.page-header h2{font-size:18px;color:#333;margin:0}
.chart-card{background:#fff;border-radius:12px;padding:20px;box-shadow:0 2px 8px rgba(0,0,0,.04);margin-bottom:20px}
.chart-card h3{font-size:15px;color:#333;margin:0 0 16px}
.chart-box{height:260px}
.profile-grid{display:flex;flex-direction:column;gap:18px;padding-top:8px}
.pf-label{font-size:13px;color:#666;margin-bottom:4px;display:block}
.top-list{display:flex;flex-direction:column;gap:16px;padding-top:8px}
.top-item{display:flex;align-items:center;gap:12px;font-size:14px;color:#333}
.top-num{width:24px;height:24px;border-radius:6px;background:#667eea;color:#fff;display:flex;align-items:center;justify-content:center;font-size:12px;font-weight:bold}
</style>
