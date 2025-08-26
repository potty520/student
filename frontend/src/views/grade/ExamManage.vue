<template>
  <div class="exam-manage">
    <div class="page-header">
      <h2>考试管理</h2>
      <p>管理学校考试安排</p>
    </div>
    
    <el-card>
      <div class="card-header">
        <!-- 搜索表单 -->
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="考试名称">
            <el-input
              v-model="searchForm.examName"
              placeholder="请输入考试名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="考试类型">
            <el-select
              v-model="searchForm.examType"
              placeholder="请选择考试类型"
              clearable
              style="width: 150px"
            >
              <el-option label="月考" :value="1" />
              <el-option label="期中考试" :value="2" />
              <el-option label="期末考试" :value="3" />
              <el-option label="模拟考试" :value="4" />
              <el-option label="其他" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item label="学年">
            <el-input
              v-model="searchForm.schoolYear"
              placeholder="如：2024-2025"
              clearable
              style="width: 150px"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="未开始" :value="0" />
              <el-option label="进行中" :value="1" />
              <el-option label="已结束" :value="2" />
              <el-option label="已发布" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">
              搜索
            </el-button>
            <el-button @click="handleReset" :icon="Refresh">
              重置
            </el-button>
          </el-form-item>
        </el-form>
        
        <!-- 操作按钮 -->
        <div class="header-buttons">
          <el-button
            type="primary"
            @click="handleAdd"
            :icon="Plus"
            v-if="hasPermission('grade:exam:add')"
          >
            新增考试
          </el-button>
          <el-button
            type="danger"
            @click="handleBatchDelete"
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            v-if="hasPermission('grade:exam:delete')"
          >
            批量删除
          </el-button>
        </div>
      </div>
      
      <!-- 考试列表 -->
      <el-table
        v-loading="loading"
        :data="examList"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="examCode" label="考试编码" width="120" />
        <el-table-column prop="examName" label="考试名称" min-width="150" />
        <el-table-column prop="examType" label="考试类型" width="100">
          <template #default="scope">
            <el-tag :type="getExamTypeTagType(scope.row.examType)">
              {{ getExamTypeName(scope.row.examType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="schoolYear" label="学年" width="120" />
        <el-table-column prop="semester" label="学期" width="80">
          <template #default="scope">
            {{ scope.row.semester === 1 ? '第一学期' : '第二学期' }}
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="handleEdit(scope.row)"
              v-if="hasPermission('grade:exam:edit')"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="success"
              @click="handleStart(scope.row)"
              v-if="scope.row.status === 0 && hasPermission('grade:exam:start')"
            >
              开始
            </el-button>
            <el-button 
              size="small" 
              type="warning"
              @click="handleFinish(scope.row)"
              v-if="scope.row.status === 1 && hasPermission('grade:exam:finish')"
            >
              结束
            </el-button>
            <el-button 
              size="small" 
              type="info"
              @click="handlePublish(scope.row)"
              v-if="scope.row.status === 2 && hasPermission('grade:exam:publish')"
            >
              发布
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)"
              v-if="hasPermission('grade:exam:delete')"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-size-options="[10, 20, 50, 100]"
          :total-text="'共 {total} 条'"
          :page-size-text="'条/页'"
          :goto-text="'跳至'"
          :page-text="'页'"
          :prev-text="'上一页'"
          :next-text="'下一页'"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试编码" prop="examCode">
              <el-input v-model="form.examCode" placeholder="请输入考试编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试名称" prop="examName">
              <el-input v-model="form.examName" placeholder="请输入考试名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试类型" prop="examType">
              <el-select v-model="form.examType" placeholder="请选择考试类型" style="width: 100%">
                <el-option label="月考" :value="1" />
                <el-option label="期中考试" :value="2" />
                <el-option label="期末考试" :value="3" />
                <el-option label="模拟考试" :value="4" />
                <el-option label="其他" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学年" prop="schoolYear">
              <el-input v-model="form.schoolYear" placeholder="如：2024-2025" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="form.semester" placeholder="请选择学期" style="width: 100%">
                <el-option label="第一学期" :value="1" />
                <el-option label="第二学期" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="未开始" :value="0" />
                <el-option label="进行中" :value="1" />
                <el-option label="已结束" :value="2" />
                <el-option label="已发布" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="请选择开始日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                placeholder="请选择结束日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="考试描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入考试描述"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/date'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const examList = ref([])
const selectedIds = ref([])
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  examName: '',
  examType: null,
  schoolYear: '',
  status: null
})

// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  examCode: '',
  examName: '',
  examType: null,
  schoolYear: '',
  semester: null,
  startDate: '',
  endDate: '',
  status: 0,
  description: '',
  remark: ''
})

// 表单验证规则
const rules = {
  examCode: [
    { required: true, message: '请输入考试编码', trigger: 'blur' },
    { max: 50, message: '考试编码长度不能超过50个字符', trigger: 'blur' }
  ],
  examName: [
    { required: true, message: '请输入考试名称', trigger: 'blur' },
    { max: 100, message: '考试名称长度不能超过100个字符', trigger: 'blur' }
  ],
  examType: [
    { required: true, message: '请选择考试类型', trigger: 'change' }
  ],
  schoolYear: [
    { required: true, message: '请输入学年', trigger: 'blur' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => form.id ? '编辑考试' : '新增考试')

// 权限检查函数
const hasPermission = (permission) => {
  return true // 临时返回true，实际应该调用 userStore.hasPermission(permission)
}

// 获取考试类型名称
const getExamTypeName = (type) => {
  const typeMap = {
    1: '月考',
    2: '期中考试',
    3: '期末考试',
    4: '模拟考试',
    5: '其他'
  }
  return typeMap[type] || '未知'
}

// 获取考试类型标签类型
const getExamTypeTagType = (type) => {
  const typeMap = {
    1: '',
    2: 'warning',
    3: 'danger',
    4: 'info',
    5: 'success'
  }
  return typeMap[type] || ''
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    0: '未开始',
    1: '进行中',
    2: '已结束',
    3: '已发布'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return statusMap[status] || ''
}

// 方法
const loadExamList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.pageNum,
      size: pagination.pageSize,
      ...searchForm
    }
    
    const response = await axios.get('/api/exams/list', { params })
    if (response.data.success) {
      examList.value = response.data.data.records
      pagination.total = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取考试列表失败')
    }
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadExamList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    examName: '',
    examType: null,
    schoolYear: '',
    status: null
  })
  pagination.pageNum = 1
  loadExamList()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    examCode: row.examCode,
    examName: row.examName,
    examType: row.examType,
    schoolYear: row.schoolYear,
    semester: row.semester,
    startDate: row.startDate,
    endDate: row.endDate,
    status: row.status,
    description: row.description || '',
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除考试"${row.examName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.delete(`/api/exams/${row.id}`)
    if (response.data.success) {
      ElMessage.success('删除成功')
      loadExamList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除考试失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的考试')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个考试吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.delete('/api/exams/batch', {
      data: selectedIds.value
    })
    if (response.data.success) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadExamList()
    } else {
      ElMessage.error(response.data.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除考试失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要开始考试"${row.examName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.put(`/api/exams/${row.id}/start`)
    if (response.data.success) {
      ElMessage.success('考试已开始')
      loadExamList()
    } else {
      ElMessage.error(response.data.message || '开始考试失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开始考试失败:', error)
      ElMessage.error('开始考试失败')
    }
  }
}

const handleFinish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要结束考试"${row.examName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.put(`/api/exams/${row.id}/finish`)
    if (response.data.success) {
      ElMessage.success('考试已结束')
      loadExamList()
    } else {
      ElMessage.error(response.data.message || '结束考试失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束考试失败:', error)
      ElMessage.error('结束考试失败')
    }
  }
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布考试"${row.examName}"的成绩吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await axios.put(`/api/exams/${row.id}/publish`)
    if (response.data.success) {
      ElMessage.success('成绩已发布')
      loadExamList()
    } else {
      ElMessage.error(response.data.message || '发布成绩失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布成绩失败:', error)
      ElMessage.error('发布成绩失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const url = form.id ? `/api/exams/${form.id}` : '/api/exams'
        const method = form.id ? 'put' : 'post'
        
        const response = await axios[method](url, form)
        if (response.data.success) {
          ElMessage.success(form.id ? '更新成功' : '添加成功')
          dialogVisible.value = false
          loadExamList()
        } else {
          ElMessage.error(response.data.message || '操作失败')
        }
      } catch (error) {
        console.error('提交考试信息失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadExamList()
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadExamList()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    examCode: '',
    examName: '',
    examType: null,
    schoolYear: '',
    semester: null,
    startDate: '',
    endDate: '',
    status: 0,
    description: '',
    remark: ''
  })
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 生命周期
onMounted(() => {
  loadExamList()
})
</script>

<style scoped>
.exam-manage {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.card-header {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 16px;
}

.header-buttons {
  display: flex;
  gap: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-manage {
    padding: 10px;
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-form .el-form-item {
    margin-bottom: 12px;
  }
  
  .header-buttons {
    flex-direction: column;
  }
  
  .header-buttons .el-button {
    width: 100%;
  }
}
</style>