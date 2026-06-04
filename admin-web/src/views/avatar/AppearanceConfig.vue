<template>
  <div class="avatar-page">
    <div class="page-header"><h2>🎭 数字人形象配置</h2></div>
    <el-row :gutter="20">
      <el-col :span="9">
        <div class="config-card">
          <h3>形象设置</h3>
          <el-form label-width="70px" size="default">
            <el-form-item label="景区"><el-select v-model="f.scenicId" style="width:100%"><el-option label="灵山胜境" :value="1" /></el-select></el-form-item>
            <el-form-item label="地区模板">
              <el-select v-model="f.template" style="width:100%" @change="applyTemplate">
                <el-option v-for="t in templates" :key="t.value" :label="t.label" :value="t.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="底座模型"><el-select v-model="f.baseModel" style="width:100%"><el-option label="温婉女性" value="gentle_female" /><el-option label="丰腴女性" value="plump_female" /><el-option label="慈祥长者" value="kind_elder" /><el-option label="英朗男性" value="strong_male" /></el-select></el-form-item>
            <el-form-item label="服饰风格"><el-select v-model="f.costume" style="width:100%"><el-option label="唐装襦裙" value="tang" /><el-option label="旗袍丝绸" value="cheongsam" /><el-option label="僧袍袈裟" value="monk" /><el-option label="民族服饰" value="ethnic" /></el-select></el-form-item>
            <el-form-item label="音色"><el-select v-model="f.voiceId" style="width:100%"><el-option label="柔和女声" value="xf_female_01" /><el-option label="清亮女声" value="xf_female_02" /><el-option label="温和男声" value="xf_male_01" /><el-option label="低沉男声" value="xf_male_02" /></el-select></el-form-item>
            <el-form-item label="方言"><el-select v-model="f.dialect" style="width:100%"><el-option label="普通话" value="mandarin" /><el-option label="吴语" value="wu" /><el-option label="粤语" value="yue" /><el-option label="西南官话" value="xinan" /></el-select></el-form-item>
            <el-form-item label="语速"><el-slider v-model="f.speed" :min="0.5" :max="2" :step="0.1" show-input /></el-form-item>
            <el-form-item><el-button type="primary" @click="save" size="large" style="width:100%">💾 保存并发布</el-button></el-form-item>
          </el-form>
        </div>
      </el-col>
      <el-col :span="15">
        <div class="preview-card">
          <div class="preview-header">
            <h3>形象预览</h3>
            <el-tag :type="tagType" size="small">{{ currentCostume.label }}</el-tag>
          </div>
          <div class="preview-area">
            <svg viewBox="0 0 320 440" class="avatar-svg" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <linearGradient id="bg" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" :stop-color="bgTop"/><stop offset="100%" :stop-color="bgBottom"/>
                </linearGradient>
              </defs>
              <rect width="320" height="440" fill="url(#bg)" rx="12"/>
              <!-- 山 -->
              <ellipse cx="90" cy="380" rx="130" ry="60" fill="#A5D6A7" opacity=".6"/>
              <ellipse cx="230" cy="395" rx="150" ry="55" fill="#81C784" opacity=".8"/>
              <ellipse cx="160" cy="410" rx="190" ry="50" fill="#66BB6A" opacity=".7"/>
              <!-- 身体底 -->
              <ellipse cx="160" cy="345" rx="60" ry="50" :fill="c.robeDark"/>
              <!-- 袍身 -->
              <path :d="robePath" :fill="c.robe"/>
              <!-- 袍领 -->
              <path d="M145 275 L160 310 L175 275" :fill="c.robeDark" :stroke="c.sash" stroke-width="1.5"/>
              <!-- 左袖 -->
              <path d="M120 285 Q85 295 70 340 Q80 348 105 340 Q115 315 130 305Z" :fill="c.robeDark"/>
              <!-- 右袖 -->
              <path d="M200 285 Q235 295 250 330 Q240 338 215 335 Q205 315 190 300Z" :fill="c.robeDark"/>
              <!-- 腰带 -->
              <rect x="108" y="338" width="104" height="14" rx="5" :fill="c.sash"/>
              <!-- 左手 -->
              <ellipse cx="85" cy="358" rx="13" ry="11" :fill="c.skin"/>
              <!-- 右手 -->
              <ellipse cx="230" cy="342" rx="12" ry="10" :fill="c.skin"/>
              <!-- 配饰 -->
              <text x="220" y="335" font-size="22" text-anchor="middle" v-if="c.accessory">{{ c.accessory }}</text>
              <!-- 念珠(僧人专属) -->
              <g v-if="c.monkBeads" opacity=".85">
                <circle cx="218" cy="322" r="3" fill="#6D4C41"/><circle cx="216" cy="330" r="3" fill="#795548"/>
                <circle cx="218" cy="338" r="3" fill="#6D4C41"/><circle cx="223" cy="343" r="3" fill="#795548"/>
              </g>
              <!-- 脖子 -->
              <rect x="150" y="268" width="20" height="14" rx="7" :fill="c.skin"/>
              <!-- 头部 -->
              <ellipse cx="160" cy="228" rx="50" ry="56" :fill="c.skin"/>
              <!-- 耳朵 -->
              <ellipse cx="110" cy="230" rx="9" ry="15" :fill="c.skin"/>
              <ellipse cx="210" cy="230" rx="9" ry="15" :fill="c.skin"/>
              <!-- 头发 -->
              <ellipse cx="160" cy="190" rx="52" ry="33" :fill="c.hair"/>
              <ellipse cx="115" cy="193" rx="11" ry="30" :fill="c.hair"/>
              <ellipse cx="205" cy="193" rx="11" ry="30" :fill="c.hair"/>
              <!-- 发饰/帽子 -->
              <ellipse v-if="c.hat" cx="160" cy="176" rx="20" ry="10" :fill="c.hair"/>
              <!-- 鬓发(女性) -->
              <path v-if="c.sideHair" d="M108 210 Q100 240 105 260" :stroke="c.hair" stroke-width="4" fill="none" stroke-linecap="round"/>
              <path v-if="c.sideHair" d="M212 210 Q220 240 215 260" :stroke="c.hair" stroke-width="4" fill="none" stroke-linecap="round"/>
              <!-- 脸颊 -->
              <circle cx="135" cy="240" r="15" :fill="c.blush" opacity=".45"/>
              <circle cx="185" cy="240" r="15" :fill="c.blush" opacity=".45"/>
              <!-- 眉毛 -->
              <path d="M132 216 Q142 211 150 216" :stroke="c.hair" stroke-width="2" fill="none" stroke-linecap="round"/>
              <path d="M170 216 Q178 211 188 216" :stroke="c.hair" stroke-width="2" fill="none" stroke-linecap="round"/>
              <!-- 眼睛 -->
              <ellipse cx="141" cy="228" rx="8" ry="9" fill="#fff"/>
              <circle cx="141" cy="229" r="5" fill="#2C1810"/>
              <circle cx="143" cy="227" r="1.8" fill="#fff"/>
              <ellipse cx="179" cy="228" rx="8" ry="9" fill="#fff"/>
              <circle cx="179" cy="229" r="5" fill="#2C1810"/>
              <circle cx="181" cy="227" r="1.8" fill="#fff"/>
              <!-- 鼻子 -->
              <path d="M160 231 Q162 239 158 243" stroke="#D7A86E" stroke-width="1.5" fill="none" stroke-linecap="round"/>
              <!-- 嘴 -->
              <path d="M150 252 Q160 261 170 252" stroke="#D4786E" stroke-width="2.2" :fill="c.lipColor" fill-opacity=".4" stroke-linecap="round"/>
              <!-- 标签 -->
              <rect x="90" y="398" width="140" height="28" rx="14" fill="rgba(0,0,0,.55)"/>
              <text x="160" y="416" text-anchor="middle" fill="#fff" font-size="11" font-weight="500">{{ currentCostume.label }}导游</text>
            </svg>
          </div>
          <!-- 模板切换卡片 -->
          <div class="template-cards">
            <div class="t-card" v-for="t in templates" :key="t.value" :class="{active:f.template===t.value}" @click="selectTemplate(t.value)">
              <span class="tc-icon">{{ t.icon }}</span>
              <span class="tc-label">{{ t.label }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

const templates = [
  { value:'佛教圣地', label:'佛教圣地', icon:'📿', voiceId:'xf_male_01', dialect:'mandarin', baseModel:'kind_elder', costume:'monk' },
  { value:'江南水乡', label:'江南水乡', icon:'☂️', voiceId:'xf_female_01', dialect:'wu', baseModel:'gentle_female', costume:'cheongsam' },
  { value:'唐风遗址', label:'唐风遗址', icon:'🏮', voiceId:'xf_female_02', dialect:'guanzhong', baseModel:'plump_female', costume:'tang' },
  { value:'西北大漠', label:'西北大漠', icon:'🏹', voiceId:'xf_male_02', dialect:'xibei', baseModel:'strong_male', costume:'ethnic' },
  { value:'西南民族', label:'西南民族', icon:'💎', voiceId:'xf_female_03', dialect:'xinan', baseModel:'gentle_female', costume:'ethnic' },
]

const f = reactive({ scenicId:1, template:'佛教圣地', baseModel:'kind_elder', costume:'monk', voiceId:'xf_male_01', dialect:'mandarin', speed:1.0 })

// 换装数据 - 根据模板动态计算SVG属性
const costumeData:Record<string,any> = {
  '佛教圣地': { skin:'#FFDAB9', hair:'#3E2723', robe:'#C62828', robeDark:'#8B0000', sash:'#FFD700', accessory:'📿', hat:true, sideHair:false, blush:'#FFAB91', lipColor:'#E57373', monkBeads:true, bgTop:'#90CAF9', bgBottom:'#C8E6C9' },
  '江南水乡': { skin:'#FFF0E0', hair:'#1A1A1A', robe:'#42A5F5', robeDark:'#1E88E5', sash:'#E0E0E0', accessory:'☂️', hat:false, sideHair:true, blush:'#F8BBD0', lipColor:'#F06292', monkBeads:false, bgTop:'#B3E5FC', bgBottom:'#E8F5E9' },
  '唐风遗址': { skin:'#FFF5EC', hair:'#2C1810', robe:'#EC407A', robeDark:'#C2185B', sash:'#FFD700', accessory:'扇', hat:false, sideHair:true, blush:'#F48FB1', lipColor:'#E91E63', monkBeads:false, bgTop:'#FFE0B2', bgBottom:'#FFCCBC' },
  '西北大漠': { skin:'#E8C39E', hair:'#1B0E00', robe:'#8D6E63', robeDark:'#5D4037', sash:'#D4A017', accessory:'🏹', hat:false, sideHair:false, blush:'#D7CCC8', lipColor:'#A1887F', monkBeads:false, bgTop:'#FFECB3', bgBottom:'#D7CCC8' },
  '西南民族': { skin:'#FFE4C4', hair:'#1C1108', robe:'#66BB6A', robeDark:'#388E3C', sash:'#FF9800', accessory:'💎', hat:false, sideHair:true, blush:'#FFAB91', lipColor:'#FF7043', monkBeads:false, bgTop:'#C8E6C9', bgBottom:'#FFF9C4' },
}

const c = computed(() => costumeData[f.template] || costumeData['佛教圣地'])
const currentCostume = computed(() => templates.find(t=>t.value===f.template)||templates[0])
const tagType = computed(() => 'success')
const bgTop = computed(() => c.value.bgTop)
const bgBottom = computed(() => c.value.bgBottom)

const robePath = computed(() => {
  const dark = c.value.robeDark
  return `M118 280 Q108 340 100 430 L220 430 Q212 340 202 280Z`
})

const applyTemplate = (val: string) => {
  const t = templates.find(x=>x.value===val)
  if(t){ f.baseModel=t.baseModel; f.costume=t.costume; f.voiceId=t.voiceId; f.dialect=t.dialect }
  ElMessage.success(`已切换至「${currentCostume.value.label}」风格`)
}
const selectTemplate = (val:string) => { f.template = val; applyTemplate(val) }
const save = () => ElMessage.success('形象配置已保存并发布')
</script>

<style scoped>
.avatar-page{padding:24px}
.page-header{margin-bottom:20px}
.page-header h2{font-size:18px;color:#333;margin:0}
.config-card,.preview-card{background:#fff;border-radius:12px;padding:24px;box-shadow:0 2px 8px rgba(0,0,0,.04);height:fit-content}
.config-card h3{font-size:15px;color:#333;margin:0 0 20px;padding-bottom:12px;border-bottom:1px solid #f0f0f0}
.preview-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px;padding-bottom:12px;border-bottom:1px solid #f0f0f0}
.preview-header h3{font-size:15px;color:#333;margin:0;border:none;padding:0}
.preview-area{display:flex;justify-content:center;margin-bottom:16px}
.avatar-svg{width:100%;max-width:300px;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,.08)}
.template-cards{display:flex;gap:10px;flex-wrap:wrap}
.t-card{flex:1;min-width:70px;display:flex;flex-direction:column;align-items:center;gap:4px;padding:10px 8px;border:2px solid #e8e8e8;border-radius:10px;cursor:pointer;transition:all .2s}
.t-card:hover{border-color:#667eea;background:#f8f9ff}
.t-card.active{border-color:#667eea;background:linear-gradient(135deg,#667eea15,#764ba215)}
.tc-icon{font-size:24px}
.tc-label{font-size:11px;color:#666;white-space:nowrap}
</style>
