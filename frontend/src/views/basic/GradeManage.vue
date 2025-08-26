<template>
  <div class="grade-manage">
    <div class="page-header">
      <h2>年级管理</h2>
      <p>管理学校年级信息，支持新增、编辑、删除年级</p>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="年级名称">
          <el-input
            v-model="searchForm.gradeName"
            placeholder="请输入年级名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="学年">
          <el-select
            v-model="searchForm.schoolYear"
            placeholder="请选择学年"
            clearable
            style="width: 150px"
          >
            <el-option label="2024-2025" value="2024-2025" />
            <el-option label="2023-2024" value="2023-2024" />
            <el-option label="2022-2023" value="2022-2023" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 数据表格 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span>年级列表</span>
          <div class="header-buttons">
            <el-button 
              type="primary" 
              @click="handleAdd"
            >
              <el-icon><Plus /></el-icon>
              新增年级
            </el-button>
            <el-button 
              type="danger" 
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="gradeList" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="gradeName" label="年级名称" width="120" />
        <el-table-column prop="gradeCode" label="年级编码" width="120" />
        <el-table-column prop="gradeLevel" label="年级序号" width="100" />
        <el-table-column prop="schoolYear" label="学年" width="120" />
        <el-table-column prop="stage" label="学段" width="80">
          <template #default="scope">
            <el-tag :type="getStageType(scope.row.stage)">{{ getStageText(scope.row.stage) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)"
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
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
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
            <el-form-item label="年级名称" prop="gradeName">
              <el-input v-model="form.gradeName" placeholder="请输入年级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级编码" prop="gradeCode">
              <el-input v-model="form.gradeCode" placeholder="请输入年级编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学段" prop="stage">
              <el-select v-model="form.stage" placeholder="请选择学段" style="width: 100%">
                <el-option label="小学" :value="1" />
                <el-option label="初中" :value="2" />
                <el-option label="高中" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级序号" prop="gradeLevel">
              <el-input-number v-model="form.gradeLevel" :min="1" :max="12" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学年" prop="schoolYear">
              <el-select v-model="form.schoolYear" placeholder="请选择学年" style="width: 100%">
                <el-option label="2024-2025" value="2024-2025" />
                <el-option label="2023-2024" value="2023-2024" />
                <el-option label="2022-2023" value="2022-2023" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/date'
import {
  getGradeList,
  createGrade,
  updateGrade,
  deleteGrade,
  batchDeleteGrades,
  updateGradeStatus
} from '@/api/grade'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const gradeList = ref([])
const selectedIds = ref([])
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  gradeName: '',
  schoolYear: ''
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
  gradeName: '',
  gradeCode: '',
  stage: 1,
  gradeLevel: 1,
  schoolYear: '2024-2025',
  sortOrder: 0,
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  gradeName: [
    { required: true, message: '请输入年级名称', trigger: 'blur' },
    { min: 2, max: 50, message: '年级名称长度在2到50个字符', trigger: 'blur' }
  ],
  gradeCode: [
    { required: true, message: '请输入年级编码', trigger: 'blur' },
    { min: 2, max: 20, message: '年级编码长度在2到20个字符', trigger: 'blur' }
  ],
  stage: [
    { required: true, message: '请选择学段', trigger: 'change' }
  ],
  gradeLevel: [
    { required: true, message: '请输入年级序号', trigger: 'blur' }
  ],
  schoolYear: [
    { required: true, message: '请选择学年', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return form.id ? '编辑年级' : '新增年级'
})

// 权限检查
const hasPermission = (permission) => {
  return userStore.hasPermission(permission)
}

// 获取学段文本
const getStageText = (stage) => {
  const stageMap = { 1: '小学', 2: '初中', 3: '高中' }
  return stageMap[stage] || '未知'
}

// 获取学段标签类型
const getStageType = (stage) => {
  const typeMap = { 1: 'primary', 2: 'success', 3: 'warning' }
  return typeMap[stage] || 'info'
}

// 方法
const loadGradeList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    
    const response = await getGradeList(params)
    if (response.code === 200) {
      gradeList.value = response.data.content
      pagination.total = response.data.totalElements
    } else {
      ElMessage.error(response.message || '获取年级列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadGradeList()
}

// 重置
const handleReset = () => {
  searchForm.gradeName = ''
  searchForm.schoolYear = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该年级吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteGrade(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadGradeList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个年级吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await batchDeleteGrades(selectedIds.value)
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadGradeList()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const response = await updateGradeStatus(row.id, row.status)
    if (response.code === 200) {
      ElMessage.success('状态更新成功')
    } else {
      ElMessage.error(response.message || '状态更新失败')
      // 恢复原状态
      row.status = row.status === 1 ? 0 : 1
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页大小变更
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadGradeList()
}

// 分页变更
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadGradeList()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    submitLoading.value = true
    const response = form.id 
      ? await updateGrade(form.id, form)
      : await createGrade(form)
    
    if (response.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      loadGradeList()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    gradeName: '',
    gradeCode: '',
    stage: 1,
    gradeLevel: 1,
    schoolYear: '2024-2025',
    sortOrder: 0,
    status: 1,
    remark: ''
  })
  
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 页面加载
onMounted(() => {
  loadGradeList()
})
</script>

<style scoped>
.grade-manage {
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

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .grade-manage {
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
  
  .pagination-container {
    text-align: center;
  }
}
</style>