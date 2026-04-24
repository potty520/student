<template>
  <div class="course-manage">
    <div class="page-header">
      <h2>课程管理</h2>
      <p>管理学校课程信息</p>
    </div>
    
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" class="search-form" :inline="true">
        <el-form-item label="课程名称">
          <el-input
            v-model="searchForm.courseName"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程编码">
          <el-input
            v-model="searchForm.courseCode"
            placeholder="请输入课程编码"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="学段">
          <el-select v-model="searchForm.stage" placeholder="请选择学段" clearable style="width: 140px">
            <el-option label="小学" :value="1" />
            <el-option label="初中" :value="2" />
            <el-option label="高中" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="searchForm.courseType" placeholder="请选择类型" clearable style="width: 140px">
            <el-option label="必修" :value="1" />
            <el-option label="选修" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <span>课程列表</span>
          <div class="header-buttons">
            <el-button
              type="primary"
              @click="handleAdd"
              v-if="hasPermission('basic:course:add')"
            >
              <el-icon><Plus /></el-icon>
              新增课程
            </el-button>
            <el-button
              type="danger"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
              v-if="hasPermission('basic:course:delete')"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="courseList"
        v-loading="loading"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="courseCode" label="课程编码" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="stage" label="学段" width="90">
          <template #default="{ row }">
            <el-tag>{{ stageText(row.stage) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.courseType === 1 ? 'primary' : 'success'">
              {{ row.courseType === 1 ? '必修' : '选修' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="fullScore" label="满分" width="90" />
        <el-table-column prop="scoreType" label="计分方式" width="100">
          <template #default="{ row }">
            {{ scoreTypeText(row.scoreType) }}
          </template>
        </el-table-column>
        <el-table-column prop="passScore" label="及格" width="80" />
        <el-table-column prop="goodScore" label="良好" width="80" />
        <el-table-column prop="excellentScore" label="优秀" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="80">
          <template #default="{ row }">
            <el-input-number
              v-model="row.sortOrder"
              :min="0"
              :max="9999"
              size="small"
              controls-position="right"
              @change="(val) => handleSortChange(row, val)"
              :disabled="!hasPermission('basic:course:edit')"
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="() => handleStatusChange(row)"
              :disabled="!hasPermission('basic:course:edit')"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              @click="handleEdit(row)"
              v-if="hasPermission('basic:course:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row)"
              v-if="hasPermission('basic:course:delete')"
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
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="760px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程编码" prop="courseCode">
              <el-input v-model="form.courseCode" placeholder="如：MATH001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
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
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="form.courseType" placeholder="请选择类型" style="width: 100%">
                <el-option label="必修" :value="1" />
                <el-option label="选修" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学分" prop="credit">
              <el-input-number v-model="form.credit" :min="0" :max="99" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="满分分值" prop="fullScore">
              <el-input-number v-model="form.fullScore" :min="1" :max="1000" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计分方式" prop="scoreType">
              <el-select v-model="form.scoreType" placeholder="请选择计分方式" style="width: 100%">
                <el-option label="百分制" :value="1" />
                <el-option label="等级制" :value="2" />
                <el-option label="五分制" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="及格线" prop="passScore">
              <el-input-number v-model="form.passScore" :min="0" :max="9999" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="良好线" prop="goodScore">
              <el-input-number v-model="form.goodScore" :min="0" :max="9999" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优秀线" prop="excellentScore">
              <el-input-number v-model="form.excellentScore" :min="0" :max="9999" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述（可选）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { Delete, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  batchDeleteCourses,
  createCourse,
  deleteCourse,
  getCourseList,
  updateCourse,
  updateCourseSortOrder,
  updateCourseStatus
} from '@/api/course'

const userStore = useUserStore()
const hasPermission = (permission) => userStore.hasPermission(permission)

const loading = ref(false)
const submitLoading = ref(false)
const courseList = ref([])
const selectedIds = ref([])

const searchForm = reactive({
  courseName: '',
  courseCode: '',
  stage: undefined,
  courseType: undefined,
  status: undefined
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  id: undefined,
  courseCode: '',
  courseName: '',
  stage: undefined,
  courseType: undefined,
  credit: null,
  fullScore: null,
  scoreType: undefined,
  passScore: null,
  goodScore: null,
  excellentScore: null,
  description: '',
  status: 1,
  sortOrder: 0
})

const rules = {
  courseCode: [
    { required: true, message: '请输入课程编码', trigger: 'blur' },
    { max: 20, message: '课程编码长度不能超过20个字符', trigger: 'blur' }
  ],
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { max: 50, message: '课程名称长度不能超过50个字符', trigger: 'blur' }
  ],
  stage: [{ required: true, message: '请选择学段', trigger: 'change' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  fullScore: [{ required: true, message: '请输入满分分值', trigger: 'change' }],
  scoreType: [{ required: true, message: '请选择计分方式', trigger: 'change' }]
}

const dialogTitle = computed(() => (form.id ? '编辑课程' : '新增课程'))

const stageText = (stage) => {
  if (stage === 1) return '小学'
  if (stage === 2) return '初中'
  if (stage === 3) return '高中'
  return '-'
}

const scoreTypeText = (type) => {
  if (type === 1) return '百分制'
  if (type === 2) return '等级制'
  if (type === 3) return '五分制'
  return '-'
}

const formatDate = (val) => (val ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : '-')

const buildQueryParams = () => ({
  page: pagination.page,
  size: pagination.size,
  courseName: searchForm.courseName || undefined,
  courseCode: searchForm.courseCode || undefined,
  stage: searchForm.stage ?? undefined,
  courseType: searchForm.courseType ?? undefined,
  status: searchForm.status ?? undefined
})

const loadCourseList = async () => {
  loading.value = true
  try {
    const res = await getCourseList(buildQueryParams())
    const pageData = res?.data
    courseList.value = pageData?.records || pageData?.list || []
    pagination.total = pageData?.total || 0
  } catch (e) {
    // request.js 已统一提示，这里只兜底
    if (!e?.message) ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCourseList()
}

const handleReset = () => {
  searchForm.courseName = ''
  searchForm.courseCode = ''
  searchForm.stage = undefined
  searchForm.courseType = undefined
  searchForm.status = undefined
  pagination.page = 1
  loadCourseList()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadCourseList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadCourseList()
}

const handleSelectionChange = (rows) => {
  selectedIds.value = (rows || []).map(r => r.id)
}

const resetForm = () => {
  form.id = undefined
  form.courseCode = ''
  form.courseName = ''
  form.stage = undefined
  form.courseType = undefined
  form.credit = null
  form.fullScore = null
  form.scoreType = undefined
  form.passScore = null
  form.goodScore = null
  form.excellentScore = null
  form.description = ''
  form.status = 1
  form.sortOrder = 0
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, {
    id: row.id,
    courseCode: row.courseCode,
    courseName: row.courseName,
    stage: row.stage,
    courseType: row.courseType,
    credit: row.credit ?? null,
    fullScore: row.fullScore ?? null,
    scoreType: row.scoreType,
    passScore: row.passScore ?? null,
    goodScore: row.goodScore ?? null,
    excellentScore: row.excellentScore ?? null,
    description: row.description || '',
    status: row.status ?? 1,
    sortOrder: row.sortOrder ?? 0
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除课程【${row.courseName}】吗？`, '提示', { type: 'warning' })
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    loadCourseList()
  } catch (_) {}
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确认批量删除选中的 ${selectedIds.value.length} 门课程吗？`, '提示', { type: 'warning' })
    await batchDeleteCourses(selectedIds.value)
    ElMessage.success('删除成功')
    selectedIds.value = []
    loadCourseList()
  } catch (_) {}
}

const handleStatusChange = async (row) => {
  try {
    await updateCourseStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (_) {
    // 失败回滚
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleSortChange = async (row, val) => {
  try {
    await updateCourseSortOrder(row.id, val)
    ElMessage.success('排序更新成功')
  } catch (_) {
    loadCourseList()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    submitLoading.value = true
    await formRef.value.validate()

    const payload = {
      courseCode: form.courseCode,
      courseName: form.courseName,
      stage: form.stage,
      courseType: form.courseType,
      credit: form.credit,
      fullScore: form.fullScore,
      scoreType: form.scoreType,
      passScore: form.passScore,
      goodScore: form.goodScore,
      excellentScore: form.excellentScore,
      description: form.description,
      status: form.status,
      sortOrder: form.sortOrder
    }

    if (form.id) {
      await updateCourse(form.id, { id: form.id, ...payload })
      ElMessage.success('更新成功')
    } else {
      await createCourse(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadCourseList()
  } catch (_) {
    // request.js 已统一提示
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.clearValidate?.()
}

onMounted(() => {
  loadCourseList()
})
</script>

<style scoped>
.course-manage {
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
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>