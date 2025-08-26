<template>
  <div class="score-statistics">
    <div class="page-header">
      <h2>成绩统计</h2>
      <p>学生成绩统计分析，支持多维度统计和图表展示</p>
    </div>
    
    <!-- 筛选栏 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="考试">
          <el-select
            v-model="filterForm.examId"
            placeholder="请选择考试"
            style="width: 200px"
            @change="handleExamChange"
          >
            <el-option
              v-for="exam in examList"
              :key="exam.id"
              :label="exam.examName"
              :value="exam.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select
            v-model="filterForm.courseId"
            placeholder="请选择科目"
            style="width: 120px"
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="loadStatistics"
            :disabled="!canLoadStatistics"
            :loading="loading"
          >
            <el-icon><DataAnalysis /></el-icon>
            查看统计
          </el-button>
          <el-button @click="handleExport" :disabled="!hasData">
            <el-icon><Download /></el-icon>
            导出报表
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 统计数据 -->
    <div v-if="hasData" class="statistics-content">
      <!-- 概览数据 -->
      <el-card class="overview-card">
        <template #header>
          <span>概览统计</span>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.totalCount }}</div>
              <div class="stat-label">总人数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.attendCount }}</div>
              <div class="stat-label">实考人数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.absentCount }}</div>
              <div class="stat-label">缺考人数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.avgScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </el-col>
        </el-row>
        
        <el-divider />
        
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.maxScore }}</div>
              <div class="stat-label">最高分</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ statistics.minScore }}</div>
              <div class="stat-label">最低分</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item excellent">
              <div class="stat-value">{{ statistics.excellentRate }}%</div>
              <div class="stat-label">优秀率(≥85分)</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item pass">
              <div class="stat-value">{{ statistics.passRate }}%</div>
              <div class="stat-label">及格率(≥60分)</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 分数段分布 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>分数段分布</span>
            </template>
            <div class="chart-container">
              <div class="score-segments">
                <div 
                  v-for="(count, segment) in statistics.scoreSegments" 
                  :key="segment"
                  class="segment-item"
                >
                  <div class="segment-label">{{ segment }}分</div>
                  <div class="segment-bar">
                    <div 
                      class="segment-fill" 
                      :style="{ width: getSegmentWidth(count) + '%' }"
                    ></div>
                  </div>
                  <div class="segment-count">{{ count }}人</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>三率统计</span>
            </template>
            <div class="chart-container">
              <div class="rate-chart">
                <div class="rate-item excellent">
                  <div class="rate-circle">
                    <div class="rate-value">{{ statistics.excellentRate }}%</div>
                    <div class="rate-label">优秀率</div>
                  </div>
                  <div class="rate-count">{{ statistics.excellentCount }}人</div>
                </div>
                
                <div class="rate-item good">
                  <div class="rate-circle">
                    <div class="rate-value">{{ statistics.goodRate }}%</div>
                    <div class="rate-label">良好率</div>
                  </div>
                  <div class="rate-count">{{ statistics.goodCount }}人</div>
                </div>
                
                <div class="rate-item pass">
                  <div class="rate-circle">
                    <div class="rate-value">{{ statistics.passRate }}%</div>
                    <div class="rate-label">及格率</div>
                  </div>
                  <div class="rate-count">{{ statistics.passCount }}人</div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 成绩分析 -->
      <el-card>
        <template #header>
          <span>成绩分析</span>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="analysis-item">
              <h4>成绩水平</h4>
              <p class="analysis-text">
                平均分{{ statistics.avgScore }}分，
                <span :class="getScoreLevelClass(statistics.avgScore)">
                  {{ getScoreLevelText(statistics.avgScore) }}
                </span>
              </p>
            </div>
          </el-col>
          
          <el-col :span="8">
            <div class="analysis-item">
              <h4>及格情况</h4>
              <p class="analysis-text">
                及格率{{ statistics.passRate }}%，
                <span :class="getPassRateClass(statistics.passRate)">
                  {{ getPassRateText(statistics.passRate) }}
                </span>
              </p>
            </div>
          </el-col>
          
          <el-col :span="8">
            <div class="analysis-item">
              <h4>优秀情况</h4>
              <p class="analysis-text">
                优秀率{{ statistics.excellentRate }}%，
                <span :class="getExcellentRateClass(statistics.excellentRate)">
                  {{ getExcellentRateText(statistics.excellentRate) }}
                </span>
              </p>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
    
    <!-- 空状态 -->
    <el-card v-else class="empty-card">
      <div class="empty-content">
        <el-empty description="请选择考试和科目查看统计数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { generateStatistics } from '@/api/score'

// 响应式数据
const loading = ref(false)

// 筛选表单
const filterForm = reactive({
  examId: null,
  courseId: null
})

// 数据列表
const examList = ref([
  { id: 1, examName: '2024-2025学年第一学期期中考试' },
  { id: 2, examName: '2024-2025学年第一学期期末考试' }
])

const courseList = ref([
  { id: 1, courseName: '语文' },
  { id: 2, courseName: '数学' },
  { id: 3, courseName: '英语' }
])

// 统计数据
const statistics = ref({
  totalCount: 0,
  attendCount: 0,
  absentCount: 0,
  maxScore: 0,
  minScore: 0,
  avgScore: 0,
  excellentCount: 0,
  excellentRate: 0,
  goodCount: 0,
  goodRate: 0,
  passCount: 0,
  passRate: 0,
  scoreSegments: {}
})

// 计算属性
const canLoadStatistics = computed(() => {
  return filterForm.examId && filterForm.courseId
})

const hasData = computed(() => {
  return statistics.value.totalCount > 0
})

// 方法
const handleExamChange = () => {
  resetStatistics()
}

const handleCourseChange = () => {
  resetStatistics()
}

// 加载统计数据
const loadStatistics = async () => {
  if (!canLoadStatistics.value) {
    ElMessage.warning('请先选择考试和科目')
    return
  }
  
  loading.value = true
  try {
    const response = await generateStatistics({
      examId: filterForm.examId,
      courseId: filterForm.courseId
    })
    
    if (response.code === 200) {
      statistics.value = response.data
      ElMessage.success('统计数据加载成功')
    } else {
      ElMessage.error(response.message || '加载统计数据失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 导出报表
const handleExport = () => {
  ElMessage.info('导出功能待实现')
}

// 重置统计数据
const resetStatistics = () => {
  statistics.value = {
    totalCount: 0,
    attendCount: 0,
    absentCount: 0,
    maxScore: 0,
    minScore: 0,
    avgScore: 0,
    excellentCount: 0,
    excellentRate: 0,
    goodCount: 0,
    goodRate: 0,
    passCount: 0,
    passRate: 0,
    scoreSegments: {}
  }
}

// 获取分数段宽度
const getSegmentWidth = (count) => {
  const maxCount = Math.max(...Object.values(statistics.value.scoreSegments))
  return maxCount > 0 ? (count / maxCount) * 100 : 0
}

// 获取成绩水平文本
const getScoreLevelText = (score) => {
  if (score >= 85) return '优秀'
  if (score >= 75) return '良好'
  if (score >= 60) return '及格'
  return '待提高'
}

const getScoreLevelClass = (score) => {
  if (score >= 85) return 'excellent'
  if (score >= 75) return 'good'
  if (score >= 60) return 'pass'
  return 'fail'
}

// 获取及格率文本
const getPassRateText = (rate) => {
  if (rate >= 95) return '非常理想'
  if (rate >= 85) return '较好'
  if (rate >= 70) return '一般'
  return '需改进'
}

const getPassRateClass = (rate) => {
  if (rate >= 85) return 'excellent'
  if (rate >= 70) return 'good'
  return 'fail'
}

// 获取优秀率文本
const getExcellentRateText = (rate) => {
  if (rate >= 30) return '非常优秀'
  if (rate >= 20) return '较好'
  if (rate >= 10) return '一般'
  return '需努力'
}

const getExcellentRateClass = (rate) => {
  if (rate >= 20) return 'excellent'
  if (rate >= 10) return 'good'
  return 'fail'
}

// 页面加载
onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.score-statistics {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 0;
}

.statistics-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overview-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-item.excellent .stat-value {
  color: #67c23a;
}

.stat-item.pass .stat-value {
  color: #409eff;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.score-segments {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.segment-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.segment-label {
  width: 60px;
  font-size: 14px;
  color: #606266;
}

.segment-bar {
  flex: 1;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.segment-fill {
  height: 100%;
  background: linear-gradient(to right, #409eff, #67c23a);
  transition: width 0.3s;
}

.segment-count {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #303133;
  font-weight: bold;
}

.rate-chart {
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 100%;
}

.rate-item {
  text-align: center;
}

.rate-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
  border: 3px solid #e4e7ed;
}

.rate-item.excellent .rate-circle {
  border-color: #67c23a;
  background-color: rgba(103, 194, 58, 0.1);
}

.rate-item.good .rate-circle {
  border-color: #e6a23c;
  background-color: rgba(230, 162, 60, 0.1);
}

.rate-item.pass .rate-circle {
  border-color: #409eff;
  background-color: rgba(64, 158, 255, 0.1);
}

.rate-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.rate-label {
  font-size: 12px;
  color: #909399;
}

.rate-count {
  font-size: 14px;
  color: #606266;
}

.analysis-item {
  text-align: center;
  padding: 15px;
}

.analysis-item h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.analysis-text {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.analysis-text .excellent {
  color: #67c23a;
  font-weight: bold;
}

.analysis-text .good {
  color: #e6a23c;
  font-weight: bold;
}

.analysis-text .pass {
  color: #409eff;
  font-weight: bold;
}

.analysis-text .fail {
  color: #f56c6c;
  font-weight: bold;
}

.empty-card {
  height: 400px;
}

.empty-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .score-statistics {
    padding: 10px;
  }
  
  .filter-form {
    flex-direction: column;
  }
  
  .filter-form .el-form-item {
    margin-bottom: 10px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .rate-chart {
    flex-direction: column;
    gap: 20px;
  }
  
  .chart-container {
    height: auto;
    padding: 20px;
  }
}
</style>