<template>
  <div class="class-manage">
    <div class="page-header">
      <h2>班级管理</h2>
      <p>管理学校班级信息，支持新增、编辑、删除班级</p>
    </div>

    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="班级名称">
          <el-input v-model="searchForm.className" placeholder="请输入班级名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="searchForm.gradeId" placeholder="请选择年级" clearable style="width: 150px">
            <el-option v-for="g in gradeOptions" :key="g.id" :label="g.gradeName" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增班级</el-button>
            <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete"><el-icon><Delete /></el-icon>批量删除</el-button>
          </div>
        </div>
      </template>

      <el-table :data="classList" v-loading="loading" @selection-change="handleSelectionChange" row-key="id">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="className" label="班级名称" width="150" />
        <el-table-column prop="classCode" label="班级编码" width="120" />
        <el-table-column prop="grade.gradeName" label="所属年级" width="120" />
        <el-table-column prop="studentCount" label="学生数量" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级名称" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级编码" prop="classCode">
              <el-input v-model="form.classCode" placeholder="请输入班级编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属年级" prop="gradeId">
              <el-select v-model="form.gradeId" placeholder="请选择年级" style="width: 100%">
                <el-option v-for="g in gradeOptions" :key="g.id" :label="g.gradeName" :value="g.id" />
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
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import { getClassList, createClass, updateClass, deleteClass, batchDeleteClasses, updateClassStatus } from '@/api/class'
import { getAllActiveGrades } from '@/api/grade'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const classList = ref([])
const selectedIds = ref([])
const formRef = ref()
const gradeOptions = ref([])

const searchForm = reactive({ className: '', gradeId: '' })

const pagination = reactive({ page: 1, size: 10, total: 0 })

const form = reactive({
  id: null, className: '', classCode: '', gradeId: null, sortOrder: 0, status: 1, remark: ''
})

const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  classCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  gradeId: [{ required: true, message: '请选择年级', trigger: 'change' }]
}

const dialogTitle = computed(() => form.id ? '编辑班级' : '新增班级')

const loadGradeOptions = async () => {
  try {
    const res = await getAllActiveGrades()
    if (res.code === 200) gradeOptions.value = res.data
  } catch (e) { /* ignore */ }
}

const loadClassList = async () => {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.size, ...searchForm }
    if (!params.gradeId) delete params.gradeId
    const res = await getClassList(params)
    if (res.code === 200) {
      classList.value = res.data.content
      pagination.total = res.data.totalElements
    }
  } catch (e) {
    ElMessage.error('加载班级列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { pagination.page = 1; loadClassList() }
const handleReset = () => { searchForm.className = ''; searchForm.gradeId = ''; handleSearch() }

const handleAdd = () => {
  Object.assign(form, { id: null, className: '', classCode: '', gradeId: null, sortOrder: 0, status: 1, remark: '' })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, { ...row })
  formRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该班级吗？', '提示', { type: 'warning' })
    const res = await deleteClass(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadClassList() }
    else ElMessage.error(res.message || '删除失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}个班级吗？`, '提示', { type: 'warning' })
    const res = await batchDeleteClasses(selectedIds.value)
    if (res.code === 200) { ElMessage.success('批量删除成功'); selectedIds.value = []; loadClassList() }
    else ElMessage.error(res.message || '批量删除失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('批量删除失败') }
}

const handleStatusChange = async (row) => {
  try {
    const res = await updateClassStatus(row.id, row.status)
    if (res.code !== 200) { ElMessage.error(res.message || '状态更新失败'); row.status = row.status === 1 ? 0 : 1 }
  } catch (e) { ElMessage.error('状态更新失败'); row.status = row.status === 1 ? 0 : 1 }
}

const handleSelectionChange = (selection) => { selectedIds.value = selection.map(item => item.id) }
const handleSizeChange = () => { pagination.page = 1; loadClassList() }
const handleCurrentChange = () => { loadClassList() }

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    const res = form.id ? await updateClass(form.id, form) : await createClass(form)
    if (res.code === 200) { ElMessage.success(form.id ? '更新成功' : '新增成功'); dialogVisible.value = false; loadClassList() }
    else ElMessage.error(res.message || '操作失败')
  } catch (e) { if (e !== false) ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

onMounted(() => { loadGradeOptions(); loadClassList() })
</script>

<style scoped>
.class-manage { padding: 20px; }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0 0 8px 0; color: #303133; }
.page-header p { margin: 0; color: #606266; font-size: 14px; }
.search-card { margin-bottom: 20px; }
.search-form { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-buttons { display: flex; gap: 10px; }
.pagination-container { margin-top: 20px; text-align: right; }
</style>
