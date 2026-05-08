<template>
  <div class="student-manage">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>学生管理</h2>
      <p>管理系统中的学生信息，包括基本信息、班级归属等</p>
    </div>
    
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" class="search-form" :inline="true">
        <el-form-item label="学生姓名">
          <el-input
            v-model="searchForm.studentName"
            placeholder="请输入学生姓名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="学号">
          <el-input
            v-model="searchForm.studentCode"
            placeholder="请输入学号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="班级">
          <el-select
            v-model="searchForm.className"
            placeholder="请选择班级"
            clearable
            style="width: 150px"
          >
            <el-option v-for="c in classOptions" :key="c.id" :label="c.className" :value="c.className" />
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
          <span>学生列表</span>
          <div class="header-buttons">
            <el-button 
              type="primary" 
              @click="handleAdd"
              v-if="hasPermission('basic:student:add')"
            >
              <el-icon><Plus /></el-icon>
              新增学生
            </el-button>
            <el-button 
              type="danger" 
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
              v-if="hasPermission('basic:student:delete')"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="studentList" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="studentCode" label="学号" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === 1 ? '' : 'danger'">{{ scope.row.gender === 1 ? '男' : '女' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="schoolClass.className" label="班级" width="120">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.schoolClass?.className }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="parentName" label="家长姓名" width="120" />
        <el-table-column prop="parentPhone" label="家长电话" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="!hasPermission('basic:student:edit')"
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
              v-if="hasPermission('basic:student:edit')"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)"
              v-if="hasPermission('basic:student:delete')"
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
            <el-form-item label="学生姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentCode">
              <el-input v-model="form.studentCode" placeholder="请输入学号" />
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
            <el-form-item label="班级" prop="className">
              <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
                <el-option v-for="c in classOptions" :key="c.id" :label="c.className" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="家长姓名" prop="parentName">
              <el-input v-model="form.parentName" placeholder="请输入家长姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家长电话" prop="parentPhone">
              <el-input v-model="form.parentPhone" placeholder="请输入家长电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭地址">
          <el-input v-model="form.address" placeholder="请输入家庭地址" />
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
import { getStudentList, createStudent, updateStudent, deleteStudent, batchDeleteStudents, updateStudentStatus } from '@/api/student'
import { getAllClasses } from '@/api/class'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const studentList = ref([])
const selectedIds = ref([])
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  studentName: '',
  studentCode: '',
  className: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 班级选项
const classOptions = ref([])

// 表单数据
const form = reactive({
  id: null,
  studentName: '',
  studentCode: '',
  gender: 1,
  birthDate: '',
  classId: '',
  phone: '',
  parentName: '',
  parentPhone: '',
  address: '',
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  studentName: [
    { required: true, message: '请输入学生姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '学生姓名长度在2到20个字符', trigger: 'blur' }
  ],
  studentCode: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 3, max: 20, message: '学号长度在3到20个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  parentName: [
    { required: true, message: '请输入家长姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '家长姓名长度在2到20个字符', trigger: 'blur' }
  ],
  parentPhone: [
    { required: true, message: '请输入家长电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return form.id ? '编辑学生' : '新增学生'
})

// 权限检查
const hasPermission = (permission) => {
  return userStore.hasPermission(permission)
}

// 方法
const loadClassOptions = async () => {
  try {
    const res = await getAllClasses()
    if (res.code === 200) classOptions.value = res.data
  } catch (e) { /* ignore */ }
}

const loadStudentList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      studentName: searchForm.studentName || undefined,
      studentCode: searchForm.studentCode || undefined
    }
    const res = await getStudentList(params)
    if (res.code === 200) {
      studentList.value = res.data.records || []
      pagination.total = res.data.total || 0
    } else {
      ElMessage.error(res.message || '获取学生列表失败')
    }
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadStudentList()
}

// 重置
const handleReset = () => {
  searchForm.studentName = ''
  searchForm.studentCode = ''
  searchForm.className = ''
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
    studentName: row.studentName,
    studentCode: row.studentCode,
    gender: row.gender,
    birthDate: row.birthDate,
    classId: row.classId || '',
    phone: row.phone,
    parentName: row.parentName,
    parentPhone: row.parentPhone,
    address: row.address,
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteStudent(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadStudentList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个学生吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await batchDeleteStudents(selectedIds.value)
    if (res.code === 200) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadStudentList()
    } else {
      ElMessage.error(res.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const res = await updateStudentStatus(row.id, row.status)
    if (res.code !== 200) {
      ElMessage.error(res.message || '状态更新失败')
      row.status = row.status === 1 ? 0 : 1
    } else {
      ElMessage.success('状态更新成功')
    }
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
  loadStudentList()
}

// 分页变更
const handleCurrentChange = (page) => {
  pagination.page = page
  loadStudentList()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    const res = form.id ? await updateStudent(form.id, form) : await createStudent(form)
    if (res.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      loadStudentList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== false) ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: null,
    studentName: '',
    studentCode: '',
    gender: 1,
    birthDate: '',
    classId: '',
    phone: '',
    parentName: '',
    parentPhone: '',
    address: '',
    status: 1,
    remark: ''
  })
  if (formRef.value) formRef.value.clearValidate()
}

// 页面加载
onMounted(() => {
  loadClassOptions()
  loadStudentList()
})
</script>

<style scoped>
.student-manage {
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
  .student-manage {
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