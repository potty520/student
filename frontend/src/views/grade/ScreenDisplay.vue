<template>
  <div class="screen-container">
    <!-- 返回按钮 -->
    <div class="back-btn" @click="goBack">
      <el-icon><ArrowLeft /></el-icon>
    </div>

    <!-- 头部 -->
    <div class="screen-header">
      <div class="header-left">
        <div class="header-line"></div>
        <span class="header-title">学生成绩管理系统 - 数据大屏</span>
      </div>
      <div class="header-right">
        <span class="header-clock">{{ currentTime }}</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="selectedExamId" placeholder="选择考试" clearable size="small" @change="loadData">
        <el-option v-for="e in examList" :key="e.id" :label="e.examName" :value="e.id" />
      </el-select>
      <el-select v-model="selectedCourseId" placeholder="选择科目" clearable size="small" @change="loadData">
        <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
    </div>

    <!-- 主体区域 -->
    <div class="screen-body">
      <!-- 左列 -->
      <div class="col-left">
        <div class="panel">
          <div class="panel-title">分数段分布</div>
          <div ref="pieChartRef" class="chart-box"></div>
        </div>
        <div class="panel">
          <div class="panel-title">班级对比分析</div>
          <div ref="barChartRef" class="chart-box"></div>
        </div>
      </div>

      <!-- 中间列 -->
      <div class="col-center">
        <div class="overview-cards">
          <div v-for="card in overviewCards" :key="card.label" class="stat-card">
            <div class="card-icon" :style="{ backgroundColor: card.color }">
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-num">{{ card.value }}</div>
              <div class="card-label">{{ card.label }}</div>
            </div>
          </div>
        </div>
        <div class="panel">
          <div class="panel-title">及格率仪表盘</div>
          <div ref="gaugeChartRef" class="chart-box"></div>
          <div class="gauge-legend">
            <span class="legend-item"><i class="legend-dot" style="background:#F56C6C"></i>不及格(&lt;{{ rateThresholds.passScore }}分)</span>
            <span class="legend-item"><i class="legend-dot" style="background:#E6A23C"></i>及格({{ rateThresholds.passScore }}-{{ rateThresholds.goodScore }}分)</span>
            <span class="legend-item"><i class="legend-dot" style="background:#409EFF"></i>良好({{ rateThresholds.goodScore }}-{{ rateThresholds.excellentScore }}分)</span>
            <span class="legend-item"><i class="legend-dot" style="background:#67C23A"></i>优秀(≥{{ rateThresholds.excellentScore }}分)</span>
          </div>
          <div class="gauge-legend" style="padding-top:0">
            <span class="legend-item" style="font-size:10px;color:#667">满分 {{ rateThresholds.fullScore }} 分，及格线 {{ rateThresholds.passScore }} 分</span>
          </div>
        </div>
      </div>

      <!-- 右列 -->
      <div class="col-right">
        <div class="panel">
          <div class="panel-title">三率分析</div>
          <div ref="ratePieRef" class="chart-box"></div>
        </div>
        <div class="panel">
          <div class="panel-title">成绩排名 TOP10</div>
          <div class="top-table-wrap">
            <table class="top-table">
              <thead>
                <tr><th>排名</th><th>姓名</th><th>班级</th><th>成绩</th></tr>
              </thead>
              <tbody>
                <tr v-for="s in topStudents" :key="s.rank">
                  <td><span class="rank-badge" :class="'rank-' + s.rank">{{ s.rank }}</span></td>
                  <td>{{ s.studentName }}</td>
                  <td>{{ s.className }}</td>
                  <td class="score-val">{{ s.score }}</td>
                </tr>
                <tr v-if="topStudents.length === 0">
                  <td colspan="4" class="empty-row">未选择考试和科目</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="screen-bottom">
      <div class="panel">
        <div class="panel-title">近期考试趋势</div>
        <div ref="lineChartRef" class="chart-box-bottom"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, User, Calendar, School, Reading } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { getScreenData } from '@/api/screen'
import { getExamList, getCourseList } from '@/api/score'

const router = useRouter()

// 时钟
const currentTime = ref(dayjs().format('YYYY-MM-DD HH:mm:ss'))
let clockTimer = null

// 筛选
const selectedExamId = ref(null)
const selectedCourseId = ref(null)
const examList = ref([])
const courseList = ref([])

// 数据
const overviewCards = reactive([
  { label: '学生总数', value: 0, icon: 'User', color: '#409EFF' },
  { label: '考试总数', value: 0, icon: 'Calendar', color: '#67C23A' },
  { label: '班级总数', value: 0, icon: 'School', color: '#E6A23C' },
  { label: '课程总数', value: 0, icon: 'Reading', color: '#F56C6C' }
])
const topStudents = ref([])
const rateThresholds = reactive({
  fullScore: 100,
  passScore: 60,
  goodScore: 80,
  excellentScore: 90
})

// 图表实例
let pieChart = null, barChart = null, gaugeChart = null, ratePieChart = null, lineChart = null
const pieChartRef = ref(null)
const barChartRef = ref(null)
const gaugeChartRef = ref(null)
const ratePieRef = ref(null)
const lineChartRef = ref(null)

let refreshTimer = null

const goBack = () => {
  router.push('/grade/statistics')
}

async function loadData() {
  try {
    const res = await getScreenData({
      examId: selectedExamId.value || undefined,
      courseId: selectedCourseId.value || undefined
    })
    if (!res?.data) return
    const data = res.data

    // Overview
    if (data.overview) {
      overviewCards[0].value = data.overview.totalStudents || 0
      overviewCards[1].value = data.overview.totalExams || 0
      overviewCards[2].value = data.overview.totalClasses || 0
      overviewCards[3].value = data.overview.totalCourses || 0
    }

    const hasDetail = selectedExamId.value && selectedCourseId.value

    // Score distribution pie
    if (data.scoreDistribution && hasDetail) {
      renderPieChart(data.scoreDistribution)
    } else {
      pieChart?.clear()
    }

    // Class comparison bar
    if (data.classComparison && hasDetail) {
      renderBarChart(data.classComparison)
    } else {
      barChart?.clear()
    }

    // Rate analysis
    if (data.rateAnalysis && hasDetail) {
      // Store thresholds for dynamic gauge colors and legend
      const ra = data.rateAnalysis
      rateThresholds.fullScore = parseFloat(ra.fullScore) || 100
      rateThresholds.passScore = parseFloat(ra.passScore) || 60
      rateThresholds.goodScore = parseFloat(ra.goodScore) || 80
      rateThresholds.excellentScore = parseFloat(ra.excellentScore) || 90
      renderRatePieChart(ra)
      renderGaugeChart(ra)
    } else {
      ratePieChart?.clear()
      gaugeChart?.clear()
    }

    // Top students
    topStudents.value = (hasDetail && data.topStudents) ? data.topStudents : []

    // Recent trend
    if (data.recentTrend && selectedCourseId.value) {
      renderLineChart(data.recentTrend)
    } else {
      lineChart?.clear()
    }

    // Resize all charts after rendering
    await nextTick()
    pieChart?.resize()
    barChart?.resize()
    gaugeChart?.resize()
    ratePieChart?.resize()
    lineChart?.resize()
  } catch (e) {
    console.error('大屏数据加载失败:', e)
  }
}

function renderPieChart(distribution) {
  if (!pieChart) return
  const names = distribution.map(d => d.label)
  const values = distribution.map(d => d.count)
  const colors = ['#F56C6C', '#E6A23C', '#F2D46D', '#67C23A', '#409EFF']
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#ccc' } },
    color: colors,
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '48%'],
      itemStyle: { borderRadius: 4, borderColor: 'rgba(0,0,0,0.2)', borderWidth: 2 },
      label: { color: '#ccc' },
      data: names.map((n, i) => ({ name: n, value: values[i] }))
    }]
  })
}

function renderBarChart(comparison) {
  if (!barChart) return
  const names = comparison.map(d => d.className).reverse()
  const avgs = comparison.map(d => parseFloat(d.avgScore) || 0).reverse()
  barChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', top: '3%', containLabel: true },
    xAxis: { type: 'value', axisLabel: { color: '#ccc' }, splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } } },
    yAxis: { type: 'category', data: names, axisLabel: { color: '#ccc', fontSize: 10 }, axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } } },
    series: [{
      type: 'bar',
      data: avgs.map(v => ({ value: v, itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#667eea' }, { offset: 1, color: '#764ba2' }]) } })),
      barWidth: 16
    }]
  })
}

function renderGaugeChart(rates) {
  if (!gaugeChart) return
  const passRate = parseFloat(rates.passRate) || 0
  // 根据实际分数线动态计算颜色分界点（相对于满分100%）
  const fs = rateThresholds.fullScore
  const passPct = Math.round((rateThresholds.passScore / fs) * 100)
  const goodPct = Math.round((rateThresholds.goodScore / fs) * 100)
  const excPct = Math.round((rateThresholds.excellentScore / fs) * 100)
  gaugeChart.setOption({
    series: [{
      type: 'gauge',
      startAngle: 210,
      endAngle: -30,
      center: ['50%', '55%'],
      radius: '85%',
      min: 0,
      max: 100,
      axisLine: {
        show: true,
        lineStyle: {
          width: 20,
          color: [
            [passPct / 100, '#F56C6C'],
            [goodPct / 100, '#E6A23C'],
            [excPct / 100, '#409EFF'],
            [1, '#67C23A']
          ]
        }
      },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      detail: {
        formatter: '{value}%',
        fontSize: 28,
        color: '#fff',
        offsetCenter: [0, '65%']
      },
      title: { show: false },
      data: [{ value: passRate, name: '及格率' }],
      pointer: { length: '65%', width: 6 }
    }]
  })
}

function renderRatePieChart(rates) {
  if (!ratePieChart) return
  ratePieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 0, textStyle: { color: '#ccc' } },
    color: ['#67C23A', '#409EFF', '#E6A23C'],
    series: [{
      type: 'pie',
      radius: ['40%', '68%'],
      center: ['50%', '48%'],
      itemStyle: { borderRadius: 4, borderColor: 'rgba(0,0,0,0.2)', borderWidth: 2 },
      label: { color: '#ccc', formatter: '{b}\n{d}%' },
      data: [
        { name: '优秀', value: rates.excellentCount || 0 },
        { name: '良好', value: (rates.goodCount || 0) - (rates.excellentCount || 0) },
        { name: '及格', value: (rates.passCount || 0) - (rates.goodCount || 0) },
        { name: '不及格', value: (rates.totalCount || 0) - (rates.passCount || 0) }
      ].filter(d => d.value > 0)
    }]
  })
}

function renderLineChart(trend) {
  if (!lineChart) return
  const names = trend.map(d => d.examName)
  const values = trend.map(d => parseFloat(d.avgScore) || 0)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { color: '#ccc', rotate: names.length > 6 ? 30 : 0 },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.3)' } }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#ccc' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#409EFF', width: 2 },
      itemStyle: { color: '#409EFF' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(64,158,255,0.4)' }, { offset: 1, color: 'rgba(64,158,255,0.02)' }]) }
    }]
  })
}

function initCharts() {
  nextTick(() => {
    if (pieChartRef.value) pieChart = echarts.init(pieChartRef.value, 'dark')
    if (barChartRef.value) barChart = echarts.init(barChartRef.value, 'dark')
    if (gaugeChartRef.value) gaugeChart = echarts.init(gaugeChartRef.value, 'dark')
    if (ratePieRef.value) ratePieChart = echarts.init(ratePieRef.value, 'dark')
    if (lineChartRef.value) lineChart = echarts.init(lineChartRef.value, 'dark')
    loadData()
  })
}

function disposeCharts() {
  pieChart?.dispose()
  barChart?.dispose()
  gaugeChart?.dispose()
  ratePieChart?.dispose()
  lineChart?.dispose()
}

onMounted(async () => {
  // 加载考试和课程列表，并自动选中第一个
  try {
    const examRes = await getExamList({ page: 1, size: 100 })
    examList.value = examRes?.data?.records || examRes?.data || []
    const courseRes = await getCourseList()
    courseList.value = courseRes?.data || []

    if (examList.value.length > 0 && !selectedExamId.value) {
      selectedExamId.value = examList.value[0].id
    }
    if (courseList.value.length > 0 && !selectedCourseId.value) {
      selectedCourseId.value = courseList.value[0].id
    }
  } catch (e) { /* ignore */ }

  initCharts()

  // 实时时钟
  clockTimer = setInterval(() => {
    currentTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  }, 1000)

  // 自动刷新
  refreshTimer = setInterval(() => {
    loadData()
  }, 30000)
})

onUnmounted(() => {
  clearInterval(clockTimer)
  clearInterval(refreshTimer)
  disposeCharts()
})
</script>

<style scoped>
.screen-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 2000;
  background: linear-gradient(135deg, #0a1628 0%, #0d1f3c 50%, #0a1628 100%);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.back-btn {
  position: absolute;
  top: 16px;
  left: 20px;
  z-index: 10;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255,255,255,0.08);
  transition: background 0.3s;
}
.back-btn:hover { background: rgba(255,255,255,0.2); }

.screen-header {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
  flex-shrink: 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  position: relative;
  padding: 0 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-line {
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #409EFF, #67C23A);
  border-radius: 2px;
}

.header-title {
  font-size: 22px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right { position: absolute; right: 30px; }

.header-clock {
  font-size: 18px;
  color: #409EFF;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}

.filter-bar {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 10px 0;
  flex-shrink: 0;
}

.filter-bar :deep(.el-select) { width: 180px; }
.filter-bar :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  box-shadow: none;
}
.filter-bar :deep(.el-input__inner) { color: #eee; }

.screen-body {
  flex: 1;
  display: flex;
  gap: 14px;
  padding: 8px 14px 0;
  min-height: 0;
}

.col-left, .col-right { width: 30%; display: flex; flex-direction: column; gap: 10px; }
.col-center { flex: 1; display: flex; flex-direction: column; gap: 10px; }

.panel {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 6px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-title {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #a8d8ff;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  flex-shrink: 0;
}

.chart-box { flex: 1; min-height: 200px; width: 100%; }

.overview-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  flex-shrink: 0;
}

.stat-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 6px;
  padding: 16px 14px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  flex-shrink: 0;
}

.card-num {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

.card-label { font-size: 12px; color: #8899aa; margin-top: 4px; }

.top-table-wrap { flex: 1; overflow-y: auto; padding: 0 8px; }

.top-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.top-table th {
  color: #8899aa;
  padding: 8px 4px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  font-weight: 500;
}

.top-table td {
  color: #ccc;
  padding: 6px 4px;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.04);
}

.rank-badge {
  display: inline-block;
  width: 20px;
  height: 20px;
  line-height: 20px;
  border-radius: 50%;
  font-size: 11px;
  font-weight: 600;
  background: rgba(255,255,255,0.1);
}

.rank-1 { background: #FFD700; color: #333; }
.rank-2 { background: #C0C0C0; color: #333; }
.rank-3 { background: #CD7F32; color: #fff; }

.score-val { color: #67C23A; font-weight: 600; }
.empty-row { color: #666; padding: 20px; }

.screen-bottom {
  height: 25%;
  flex-shrink: 0;
  padding: 8px 14px;
}

.screen-bottom .panel { height: 100%; }

.gauge-legend {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px 12px;
  padding: 4px 10px 10px;
  flex-shrink: 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #8899aa;
}

.legend-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  flex-shrink: 0;
}

.chart-box-bottom {
  flex: 1;
  min-height: 120px;
  width: 100%;
}
</style>
