<template>
  <div class="scenic-page">
    <div class="page-header"><h2>🗺️ 景区围栏配置 - 灵山胜境</h2><el-button type="primary">+ 新增景点围栏</el-button></div>
    <el-table :data="spots" stripe class="data-table">
      <el-table-column prop="spotName" label="景点名称" />
      <el-table-column prop="latitude" label="纬度" width="120" />
      <el-table-column prop="longitude" label="经度" width="120" />
      <el-table-column label="围栏半径" width="120"><template #default="{row}">{{ row.radius }}m</template></el-table-column>
      <el-table-column label="状态" width="100"><template #default><el-tag type="success" size="small">启用</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default><el-button size="small" type="primary" link>编辑</el-button><el-button size="small" type="danger" link>删除</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
const spots = ref([
  {spotName:'灵山大佛',latitude:'31.4220',longitude:'120.1080',radius:80},
  {spotName:'九龙灌浴',latitude:'31.4190',longitude:'120.1110',radius:60},
  {spotName:'梵宫',latitude:'31.4170',longitude:'120.1130',radius:100},
  {spotName:'五印坛城',latitude:'31.4180',longitude:'120.1150',radius:60},
  {spotName:'曼飞龙塔',latitude:'31.4160',longitude:'120.1100',radius:50},
])
</script>

<style scoped>
.scenic-page{padding:24px}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
.page-header h2{font-size:18px;color:#333;margin:0}
.data-table{background:#fff;border-radius:8px}
</style>
