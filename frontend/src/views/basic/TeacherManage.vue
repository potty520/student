<template>
  <div class="teacher-manage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>教师管理</h2>
      <p>管理系统中的教师信息，包括基本信息、任教科目等</p>
    </div>
    
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" class="search-form" :inline="true">
        <el-form-item label="教师姓名">
          <el-input
            v-model="searchForm.teacherName"
            placeholder="请输入教师姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="工号">
          <el-input
            v-model="searchForm.teacherCode"
            placeholder="请输入工号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="任教科目">
          <el-select
            v-model="searchForm.subject"
            placeholder="请选择任教科目"
            clearable
            style="width: 150px"
          >
            <el-option label="语文" value="语文" />
            <el-option label="数学" value="数学" />
            <el-option label="英语" value="英语" />
            <el-option label="物理" value="物理" />
            <el-option label="化学" value="化学" />
            <el-option label="生物" value="生物" />
            <el-option label="历史" value="历史" />
            <el-option label="地理" value="地理" />
            <el-option label="政治" value="政治" />
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
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>教师列表</span>
          <div class="header-buttons">
            <el-button 
              type="primary" 
              @click="handleAdd"
              v-if="hasPermission('basic:teacher:add')"
            >
              <el-icon><Plus /></el-icon>
              新增教师
            </el-button>
            <el-button 
              type="danger" 
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
              v-if="hasPermission('basic:teacher:delete')"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="teacherList" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="teacherName" label="教师姓名" width="120" />
        <el-table-column prop="teacherCode" label="工号" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === 1 ? '' : 'danger'">{{ scope.row.gender === 1 ? '男' : '女' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="mobile" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="subject" label="任教科目" width="100">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.subject }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="!hasPermission('basic:teacher:edit')"
            />
          </template>
        </el-table-column>
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
              v-if="hasPermission('basic:teacher:edit')"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)"
              v-if="hasPermission('basic:teacher:delete')"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
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
      width="700px"
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
            <el-form-item label="教师姓名" prop="teacherName">
              <el-input v-model="form.teacherName" placeholder="请输入教师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="teacherCode">
              <el-input v-model="form.teacherCode" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="mobile">
              <el-input v-model="form.mobile" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任教科目" prop="subject">
              <el-select v-model="form.subject" placeholder="请选择任教科目" style="width: 100%">
                <el-option label="语文" value="语文" />
                <el-option label="数学" value="数学" />
                <el-option label="英语" value="英语" />
                <el-option label="物理" value="物理" />
                <el-option label="化学" value="化学" />
                <el-option label="生物" value="生物" />
                <el-option label="历史" value="历史" />
                <el-option label="地理" value="地理" />
                <el-option label="政治" value="政治" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="title">
              <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="助教" value="助教" />
                <el-option label="讲师" value="讲师" />
                <el-option label="副教授" value="副教授" />
                <el-option label="教授" value="教授" />
                <el-option label="特级教师" value="特级教师" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
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
import { getTeacherList, createTeacher, updateTeacher, deleteTeacher, batchDeleteTeachers, updateTeacherStatus } from '@/api/teacher'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const teacherList = ref([])
const selectedIds = ref([])
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  teacherName: '',
  teacherCode: '',
  subject: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  teacherName: '',
  teacherCode: '',
  gender: 1,
  birthDate: '',
  mobile: '',
  email: '',
  subject: '',
  title: '',
  address: '',
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  teacherName: [
    { required: true, message: '请输入教师姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '教师姓名长度在2到20个字符', trigger: 'blur' }
  ],
  teacherCode: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { min: 3, max: 20, message: '工号长度在3到20个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  mobile: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择任教科目', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return form.id ? '编辑教师' : '新增教师'
})

// 权限检查
const hasPermission = (permission) => {
  return userStore.hasPermission(permission)
}

// 方法
const loadTeacherList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      teacherName: searchForm.teacherName || undefined,
      teacherCode: searchForm.teacherCode || undefined
    }
    const res = await getTeacherList(params)
    if (res.code === 200) {
      teacherList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取教师列表失败')
    }
  } catch (error) {
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadTeacherList()
}

// 重置
const handleReset = () => {
  searchForm.teacherName = ''
  searchForm.teacherCode = ''
  searchForm.subject = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    teacherName: row.teacherName,
    teacherCode: row.teacherCode,
    gender: row.gender,
    birthDate: row.birthDate,
    mobile: row.mobile,
    email: row.email,
    subject: row.subject || '',
    title: row.title || '',
    address: row.address || '',
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该教师吗？', '提示', { type: 'warning' })
    const res = await deleteTeacher(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadTeacherList() }
    else ElMessage.error(res.message || '删除失败')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个教师吗？`, '提示', { type: 'warning' })
    const res = await batchDeleteTeachers(selectedIds.value)
    if (res.code === 200) { ElMessage.success('批量删除成功'); selectedIds.value = []; loadTeacherList() }
    else ElMessage.error(res.message || '批量删除失败')
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const res = await updateTeacherStatus(row.id, row.status)
    if (res.code !== 200) { ElMessage.error(res.message || '状态更新失败'); row.status = row.status === 1 ? 0 : 1 }
    else ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 分页大小变更
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadTeacherList()
}

// 分页变更
const handleCurrentChange = (page) => {
  pagination.page = page
  loadTeacherList()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    const res = form.id ? await updateTeacher(form.id, form) : await createTeacher(form)
    if (res.code === 200) { ElMessage.success(form.id ? '更新成功' : '新增成功'); dialogVisible.value = false; loadTeacherList() }
    else ElMessage.error(res.message || '操作失败')
  } catch (error) {
    if (error !== false) ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null, teacherName: '', teacherCode: '', gender: 1, birthDate: '',
    mobile: '', email: '', subject: '', title: '', address: '', status: 1, remark: ''
  })
  if (formRef.value) formRef.value.clearValidate()
}

// 页面加载
onMounted(() => {
  loadTeacherList()
})
</script>

<style scoped>
.teacher-manage {
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
  .teacher-manage {
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