<template>
  <div class="knowledge-page">
    <div class="page-header"><h2>📚 知识条目列表</h2><el-button type="primary" @click="router.push('/knowledge/upload')">+ 上传文档</el-button></div>
    <div class="filter-bar">
      <el-input v-model="search" placeholder="搜索知识点..." prefix-icon="Search" clearable style="width:280px" />
      <el-select v-model="typeFilter" placeholder="类型筛选" clearable style="width:140px">
        <el-option label="叙事文本" value="NARRATIVE" /><el-option label="结构化" value="STRUCTURED" /><el-option label="表格" value="TABLE" />
      </el-select>
    </div>
    <el-table :data="filteredList" stripe class="data-table" empty-text="暂无知识条目，请先上传文档">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="chunkText" label="内容" show-overflow-tooltip min-width="400" />
      <el-table-column prop="chunkType" label="类型" width="90">
        <template #default="{row}"><el-tag :type="row.chunkType==='NARRATIVE'?'':'warning'" size="small">{{ row.chunkType==='NARRATIVE'?'叙事':'表格' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right"><template #default><el-button type="danger" size="small" link>删除</el-button></template></el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
const router = useRouter()
const search = ref(''), typeFilter = ref('')
const list = ref([
  {id:1,chunkText:'灵山大佛高88米，是世界上最高的青铜释迦牟尼佛像。大佛右手施"无畏印"，左手施"与愿印"。',chunkType:'NARRATIVE'},
  {id:2,chunkText:'九龙灌浴演出时间：10:00、11:30、14:00、16:00，喷泉最高30米。',chunkType:'NARRATIVE'},
  {id:3,chunkText:'灵山大照壁长39.8米高7米，赵朴初题字"灵山胜境"。',chunkType:'STRUCTURED'},
  {id:4,chunkText:'五明桥由5座汉白玉石拱桥组成，代表佛教五明智慧。',chunkType:'STRUCTURED'},
  {id:5,chunkText:'佛足坛：青铜足印长1.2m宽0.6m，刻32种吉祥图案。',chunkType:'TABLE'},
])
const filteredList = computed(()=>list.value.filter(i=>(!search.value||i.chunkText.includes(search.value))&&(!typeFilter.value||i.chunkType===typeFilter.value)))
</script>

<style scoped>
.knowledge-page{padding:24px}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
.page-header h2{font-size:18px;color:#333;margin:0}
.filter-bar{display:flex;gap:12px;margin-bottom:16px;background:#fff;padding:12px 16px;border-radius:8px}
.data-table{background:#fff;border-radius:8px}
</style>
