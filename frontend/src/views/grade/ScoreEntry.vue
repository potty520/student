<template>
  <div class="score-entry">
    <div class="page-header">
      <h2>成绩录入</h2>
      <p>录入学生考试成绩，支持批量导入和在线录入</p>
    </div>
    
    <!-- 选择栏 -->
    <el-card class="selection-card">
      <el-form :model="selectionForm" :inline="true" class="selection-form">
        <el-form-item label="考试">
          <el-select
            v-model="selectionForm.examId"
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
        <el-form-item label="班级">
          <el-select
            v-model="selectionForm.classId"
            placeholder="请选择班级"
            style="width: 150px"
            @change="handleClassChange"
          >
            <el-option
              v-for="classItem in classList"
              :key="classItem.id"
              :label="classItem.className"
              :value="classItem.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select
            v-model="selectionForm.courseId"
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
            @click="loadScoreTemplate"
            :disabled="!canLoadTemplate"
          >
            <el-icon><Search /></el-icon>
            加载成绩表
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 成绩录入表格 -->
    <el-card v-if="studentList.length > 0">
      <template #header>
        <div class="card-header">
          <div class="header-info">
            <span>成绩录入 - {{ examInfo?.examName }} - {{ classInfo?.className }} - {{ courseInfo?.courseName }}</span>
            <el-tag type="info" style="margin-left: 10px">共 {{ studentList.length }} 名学生</el-tag>
          </div>
          <div class="header-buttons">
            <el-button @click="handleBatchImport">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button 
              type="primary" 
              @click="handleBatchSave"
              :loading="saveLoading"
              :disabled="!hasChanges"
            >
              <el-icon><Check /></el-icon>
              保存成绩
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="studentList" 
        v-loading="loading"
        border
        size="small"
        class="score-table"
      >
        <el-table-column prop="studentCode" label="学号" width="100" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column label="成绩" width="120">
          <template #default="scope">
            <el-input-number
              v-model="scope.row.score"
              :min="0"
              :max="100"
              :precision="1"
              size="small"
              placeholder="请输入成绩"
              @change="handleScoreChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="缺考" width="80">
          <template #default="scope">
            <el-checkbox
              v-model="scope.row.absent"
              @change="handleAbsentChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="150">
          <template #default="scope">
            <el-input
              v-model="scope.row.remark"
              placeholder="备注信息"
              size="small"
              @input="handleRemarkChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.hasChanges" type="warning" size="small">已修改</el-tag>
            <el-tag v-else-if="scope.row.score !== null || scope.row.absent" type="success" size="small">已录入</el-tag>
            <el-tag v-else type="info" size="small">未录入</el-tag>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 统计信息 -->
      <div class="statistics-info">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-statistic title="实考人数" :value="attendCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="缺考人数" :value="absentCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="平均分" :value="averageScore" :precision="1" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="及格率" :value="passRate" suffix="%" :precision="1" />
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入成绩"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="import-area">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="false"
          accept=".xlsx,.xls"
          @change="handleFileChange"
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>
            选择Excel文件
          </el-button>
        </el-upload>
        
        <div class="upload-tips">
          <p>请上传Excel文件，格式要求：</p>
          <ul>
            <li>1. 必须包含学号、姓名、成绩列</li>
            <li>2. 成绩取值范围：0-100</li>
            <li>3. 缺考学生成绩列留空或填写"缺考"</li>
          </ul>
          <el-button link type="primary" @click="downloadTemplate">
            下载模板
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleImportConfirm"
            :loading="importLoading"
            :disabled="!selectedFile"
          >
            确定导入
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  getScoreEntryTemplate,
  batchCreateScores
} from '@/api/score'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const importLoading = ref(false)
const importDialogVisible = ref(false)
const selectedFile = ref(null)
const uploadRef = ref()

// 选择表单
const selectionForm = reactive({
  examId: null,
  classId: null,
  courseId: null
})

// 数据列表
const examList = ref([
  { id: 1, examName: '2024-2025学年第一学期期中考试' },
  { id: 2, examName: '2024-2025学年第一学期期末考试' }
])

const classList = ref([
  { id: 1, className: '一年级1班' },
  { id: 2, className: '一年级2班' },
  { id: 3, className: '二年级1班' }
])

const courseList = ref([
  { id: 1, courseName: '语文' },
  { id: 2, courseName: '数学' },
  { id: 3, courseName: '英语' }
])

const studentList = ref([])
const examInfo = ref(null)
const classInfo = ref(null)
const courseInfo = ref(null)

// 计算属性
const canLoadTemplate = computed(() => {
  return selectionForm.examId && selectionForm.classId && selectionForm.courseId
})

const hasChanges = computed(() => {
  return studentList.value.some(student => student.hasChanges)
})

const attendCount = computed(() => {
  return studentList.value.filter(student => !student.absent && student.score !== null).length
})

const absentCount = computed(() => {
  return studentList.value.filter(student => student.absent).length
})

const averageScore = computed(() => {
  const validScores = studentList.value
    .filter(student => !student.absent && student.score !== null)
    .map(student => student.score)
  
  if (validScores.length === 0) return 0
  
  const sum = validScores.reduce((acc, score) => acc + score, 0)
  return sum / validScores.length
})

const passRate = computed(() => {
  const validScores = studentList.value
    .filter(student => !student.absent && student.score !== null)
  
  if (validScores.length === 0) return 0
  
  const passCount = validScores.filter(student => student.score >= 60).length
  return (passCount / validScores.length) * 100
})

// 方法
const handleExamChange = () => {
  // 清空学生列表
  studentList.value = []
}

const handleClassChange = () => {
  // 清空学生列表
  studentList.value = []
}

const handleCourseChange = () => {
  // 清空学生列表
  studentList.value = []
}

// 加载成绩模板
const loadScoreTemplate = async () => {
  if (!canLoadTemplate.value) {
    ElMessage.warning('请先选择考试、班级和科目')
    return
  }
  
  loading.value = true
  try {
    const response = await getScoreEntryTemplate({
      examId: selectionForm.examId,
      classId: selectionForm.classId,
      courseId: selectionForm.courseId
    })
    
    if (response.code === 200) {
      const data = response.data
      examInfo.value = data.examInfo
      classInfo.value = data.classInfo
      courseInfo.value = data.courseInfo
      
      // 初始化学生成绩数据
      studentList.value = data.students.map(student => ({
        ...student,
        hasChanges: false
      }))
      
      ElMessage.success('成绩表加载成功')
    } else {
      ElMessage.error(response.message || '加载成绩表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 成绩变更
const handleScoreChange = (student) => {
  student.hasChanges = true
  // 如果输入了成绩，取消缺考状态
  if (student.score !== null && student.score !== undefined) {
    student.absent = false
  }
}

// 缺考状态变更
const handleAbsentChange = (student) => {
  student.hasChanges = true
  // 如果标记为缺考，清空成绩
  if (student.absent) {
    student.score = null
  }
}

// 备注变更
const handleRemarkChange = (student) => {
  student.hasChanges = true
}

// 批量保存
const handleBatchSave = async () => {
  if (!hasChanges.value) {
    ElMessage.warning('没有数据需要保存')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要保存所有成绩吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    saveLoading.value = true
    
    // 构建成绩数据
    const scores = studentList.value
      .filter(student => student.hasChanges)
      .map(student => ({
        examId: selectionForm.examId,
        studentId: student.studentId,
        courseId: selectionForm.courseId,
        score: student.absent ? null : student.score,
        absent: student.absent || false,
        remark: student.remark || ''
      }))
    
    const response = await batchCreateScores(scores)
    if (response.code === 200) {
      ElMessage.success('成绩保存成功')
      // 重置修改状态
      studentList.value.forEach(student => {
        student.hasChanges = false
      })
    } else {
      ElMessage.error(response.message || '成绩保存失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('成绩保存失败')
    }
  } finally {
    saveLoading.value = false
  }
}

// 批量导入
const handleBatchImport = () => {
  importDialogVisible.value = true
}

// 文件选择
const handleFileChange = (file) => {
  selectedFile.value = file
}

// 下载模板
const downloadTemplate = () => {
  // 这里实现下载模板的逻辑
  ElMessage.info('模板下载功能待实现')
}

// 确认导入
const handleImportConfirm = () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  
  importLoading.value = true
  
  // 这里实现文件解析和导入逻辑
  setTimeout(() => {
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    importLoading.value = false
    selectedFile.value = null
    
    // 重新加载数据
    loadScoreTemplate()
  }, 2000)
}

// 页面加载
onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.score-entry {
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

.selection-card {
  margin-bottom: 20px;
}

.selection-form {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.score-table {
  margin-bottom: 20px;
}

.statistics-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
}

.import-area {
  text-align: center;
  padding: 20px;
}

.upload-tips {
  margin-top: 20px;
  text-align: left;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
}

.upload-tips p {
  margin: 0 0 10px 0;
  font-weight: bold;
}

.upload-tips ul {
  margin: 10px 0;
  padding-left: 20px;
}

.upload-tips li {
  margin: 5px 0;
  color: #666;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .score-entry {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
  
  .header-buttons {
    justify-content: center;
  }
  
  .selection-form {
    flex-direction: column;
  }
  
  .selection-form .el-form-item {
    margin-bottom: 10px;
  }
}
</style>