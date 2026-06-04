<template>
  <div class="upload-page">
    <div class="page-header"><h2>📄 文档上传</h2></div>
    <div class="upload-card">
      <el-upload drag action="/api/v1/admin/knowledge/upload" :headers="authHeader" :data="{scenicId:1,uploadedBy:1}" :on-success="onSuccess" class="upload-area">
        <div class="upload-content">
          <div class="upload-icon">📁</div>
          <p class="upload-title">拖拽文件到此处或点击上传</p>
          <p class="upload-hint">支持 .xlsx / .docx / .pdf / .txt 格式，单个文件不超过50MB</p>
        </div>
      </el-upload>
      <div class="upload-tips">
        <h4>💡 上传提示</h4>
        <ul>
          <li><b>XLSX</b> 表格数据会被自动转换为Markdown格式进行向量化</li>
          <li><b>DOCX</b> 叙事文档按段落语义分块，结构化数据按字段提取</li>
          <li><b>PDF</b> 按页解析，保留原文结构</li>
          <li>上传后系统将自动进行文档解析→智能分块→向量嵌入→写入知识库</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
const authHeader = computed(()=>({Authorization:`Bearer ${localStorage.getItem('admin_token')}`}))
const onSuccess = ()=>ElMessage.success('上传成功，文档正在处理中...')
</script>

<style scoped>
.upload-page{padding:24px}
.page-header{margin-bottom:20px}
.page-header h2{font-size:18px;color:#333;margin:0}
.upload-card{background:#fff;border-radius:12px;padding:30px;box-shadow:0 2px 8px rgba(0,0,0,.04)}
.upload-area{width:100%}
.upload-content{text-align:center;padding:40px}
.upload-icon{font-size:48px;margin-bottom:12px}
.upload-title{font-size:16px;color:#333;margin:0 0 8px}
.upload-hint{font-size:13px;color:#999;margin:0}
.upload-tips{margin-top:24px;padding:20px;background:#f8f9ff;border-radius:8px;border:1px solid #e8ecff}
.upload-tips h4{margin:0 0 12px;font-size:14px;color:#667eea}
.upload-tips ul{margin:0;padding-left:20px}
.upload-tips li{font-size:13px;color:#666;line-height:2}
</style>
