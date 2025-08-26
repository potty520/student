<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>欢迎使用学生成绩管理系统</h1>
      <p>{{ currentTime }}</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-container">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
          <div class="stat-card">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="action in quickActions" :key="action.title">
          <el-card class="action-card" @click="handleQuickAction(action)">
            <div class="action-content">
              <el-icon size="32" :color="action.color">
                <component :is="action.icon" />
              </el-icon>
              <h3>{{ action.title }}</h3>
              <p>{{ action.description }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最新考试和通知 -->
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :md="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>最近考试</span>
              <el-button type="text" @click="$router.push('/grade/exam')">更多</el-button>
            </div>
          </template>
          <div class="exam-list">
            <div v-if="recentExams.length === 0" class="empty-state">
              <el-empty description="暂无考试安排" />
            </div>
            <div v-else v-for="exam in recentExams" :key="exam.id" class="exam-item">
              <div class="exam-info">
                <h4>{{ exam.name }}</h4>
                <p>{{ exam.date }} | {{ exam.status }}</p>
              </div>
              <el-tag :type="getExamStatusType(exam.status)">
                {{ exam.status }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>系统通知</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <div class="notice-list">
            <div v-if="notices.length === 0" class="empty-state">
              <el-empty description="暂无通知" />
            </div>
            <div v-else v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-content">
                <h4>{{ notice.title }}</h4>
                <p>{{ notice.content }}</p>
                <span class="notice-time">{{ notice.time }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/date'

const router = useRouter()
const userStore = useUserStore()

// 当前时间
const currentTime = ref('')

// 统计数据
const stats = ref([
  {
    title: '学生总数',
    value: '1,234',
    icon: 'UserFilled',
    color: '#409EFF'
  },
  {
    title: '教师总数',
    value: '89',
    icon: 'Avatar',
    color: '#67C23A'
  },
  {
    title: '班级总数',
    value: '36',
    icon: 'House',
    color: '#E6A23C'
  },
  {
    title: '本月考试',
    value: '12',
    icon: 'Document',
    color: '#F56C6C'
  }
])

// 快捷操作
const quickActions = ref([
  {
    title: '录入成绩',
    description: '快速录入学生考试成绩',
    icon: 'Edit',
    color: '#409EFF',
    route: '/grade/score'
  },
  {
    title: '学生管理',
    description: '管理学生基本信息',
    icon: 'UserFilled',
    color: '#67C23A',
    route: '/basic/student'
  },
  {
    title: '考试安排',
    description: '创建和管理考试',
    icon: 'Calendar',
    color: '#E6A23C',
    route: '/grade/exam'
  },
  {
    title: '成绩统计',
    description: '查看成绩分析报表',
    icon: 'DataAnalysis',
    color: '#F56C6C',
    route: '/grade/statistics'
  },
  {
    title: '班级管理',
    description: '管理年级班级信息',
    icon: 'House',
    color: '#909399',
    route: '/basic/class'
  },
  {
    title: '系统设置',
    description: '用户和权限管理',
    icon: 'Setting',
    color: '#606266',
    route: '/system/user'
  }
])

// 最近考试
const recentExams = ref([
  {
    id: 1,
    name: '期中考试',
    date: '2024-11-15',
    status: '进行中'
  },
  {
    id: 2,
    name: '月考三',
    date: '2024-11-20',
    status: '未开始'
  },
  {
    id: 3,
    name: '期末考试',
    date: '2024-12-25',
    status: '未开始'
  }
])

// 系统通知
const notices = ref([
  {
    id: 1,
    title: '系统维护通知',
    content: '系统将于本周末进行维护升级，请提前保存数据。',
    time: '2024-08-25 10:00'
  },
  {
    id: 2,
    title: '新功能上线',
    content: '成绩分析功能已上线，支持多维度统计分析。',
    time: '2024-08-24 15:30'
  }
])

// 定时器
let timer = null

// 更新当前时间
const updateTime = () => {
  const now = new Date()
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const year = now.getFullYear()
  const month = (now.getMonth() + 1).toString().padStart(2, '0')
  const date = now.getDate().toString().padStart(2, '0')
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  const seconds = now.getSeconds().toString().padStart(2, '0')
  const weekday = weekdays[now.getDay()]
  
  currentTime.value = `${year}-${month}-${date} ${hours}:${minutes}:${seconds} ${weekday}`
}

// 处理快捷操作点击
const handleQuickAction = (action) => {
  if (action.route) {
    router.push(action.route)
  }
}

// 获取考试状态类型
const getExamStatusType = (status) => {
  switch (status) {
    case '进行中':
      return 'success'
    case '未开始':
      return 'info'
    case '已结束':
      return 'warning'
    default:
      return 'info'
  }
}

// 组件挂载
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  
  // 加载统计数据
  loadStats()
})

// 组件卸载
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

// 加载统计数据
const loadStats = async () => {
  try {
    // 这里可以调用API获取真实的统计数据
    // const response = await getSystemStats()
    // stats.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 30px;
}

.dashboard-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.dashboard-header p {
  font-size: 16px;
  color: #606266;
}

.stats-container {
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.quick-actions {
  margin-bottom: 30px;
}

.quick-actions h2 {
  margin-bottom: 20px;
  color: #303133;
}

.action-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.action-content {
  text-align: center;
  padding: 20px;
}

.action-content h3 {
  margin: 15px 0 10px;
  color: #303133;
  font-size: 16px;
}

.action-content p {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.content-row {
  margin-bottom: 20px;
}

.content-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-list,
.notice-list {
  height: 320px;
  overflow-y: auto;
}

.exam-item,
.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.exam-item:last-child,
.notice-item:last-child {
  border-bottom: none;
}

.exam-info h4,
.notice-content h4 {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 14px;
}

.exam-info p {
  margin: 0;
  color: #909399;
  font-size: 12px;
}

.notice-content {
  flex: 1;
}

.notice-content p {
  margin: 5px 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .action-content {
    padding: 15px;
  }
}
</style>