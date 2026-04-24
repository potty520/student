<template>
  <div class="message-manage">
    <div class="page-header">
      <h2>系统通知</h2>
      <p>发布和管理系统通知消息</p>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="通知标题">
          <el-input v-model="searchForm.title" clearable placeholder="请输入通知标题" style="width: 240px" />
        </el-form-item>
        <el-form-item label="发送状态">
          <el-select v-model="searchForm.status" clearable placeholder="请选择状态" style="width: 160px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发送" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>通知列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新建通知
          </el-button>
        </div>
      </template>

      <el-table :data="messageList" v-loading="loading" row-key="id">
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="senderName" label="发送人" width="120" />
        <el-table-column label="接收对象" width="120">
          <template #default="{ row }">{{ targetTypeText(row.targetType) }}</template>
        </el-table-column>
        <el-table-column prop="totalCount" label="接收人数" width="100" />
        <el-table-column prop="readCount" label="已读人数" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发送' : '草稿' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="180">
          <template #default="{ row }">{{ formatDate(row.sendTime) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 0" size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="handleSend(row)">发送</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="接收对象" prop="targetType">
          <el-radio-group v-model="form.targetType">
            <el-radio :label="1">全部用户</el-radio>
            <el-radio :label="2">按角色</el-radio>
            <el-radio :label="3">按班级</el-radio>
            <el-radio :label="4">指定个人</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.targetType !== 1" label="对象ID" prop="targetIds">
          <el-input v-model="form.targetIds" placeholder="多个ID用英文逗号分隔，如：1,2,3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewVisible" title="通知详情" width="700px">
      <div class="detail-line"><strong>标题：</strong>{{ currentMessage?.title || '-' }}</div>
      <div class="detail-line"><strong>发送人：</strong>{{ currentMessage?.senderName || '-' }}</div>
      <div class="detail-line"><strong>接收对象：</strong>{{ targetTypeText(currentMessage?.targetType) }}</div>
      <div class="detail-line"><strong>发送时间：</strong>{{ formatDate(currentMessage?.sendTime) }}</div>
      <div class="detail-content">
        <strong>内容：</strong>
        <p>{{ currentMessage?.content || '-' }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { createMessage, deleteMessage, getMessageList, sendMessage, updateMessage } from '@/api/message'

const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const messageList = ref([])
const currentMessage = ref(null)

const searchForm = reactive({
  title: '',
  status: undefined
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const viewVisible = ref(false)
const formRef = ref()
const form = reactive({
  id: undefined,
  title: '',
  content: '',
  targetType: 1,
  targetIds: ''
})

const rules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
  targetType: [{ required: true, message: '请选择接收对象', trigger: 'change' }],
  targetIds: [{ required: true, message: '请输入对象ID', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.id ? '编辑通知草稿' : '新建系统通知'))

const targetTypeText = (targetType) => {
  if (targetType === 1) return '全部用户'
  if (targetType === 2) return '按角色'
  if (targetType === 3) return '按班级'
  if (targetType === 4) return '指定个人'
  return '-'
}

const formatDate = (v) => (v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-')

const loadMessages = async () => {
  loading.value = true
  try {
    const res = await getMessageList({
      page: pagination.page,
      size: pagination.size,
      title: searchForm.title || undefined,
      messageType: 1,
      status: searchForm.status
    })
    const pageData = res?.data
    messageList.value = pageData?.records || pageData?.list || []
    pagination.total = pageData?.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadMessages()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = undefined
  pagination.page = 1
  loadMessages()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadMessages()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadMessages()
}

const resetForm = () => {
  form.id = undefined
  form.title = ''
  form.content = ''
  form.targetType = 1
  form.targetIds = ''
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  form.id = row.id
  form.title = row.title
  form.content = row.content
  form.targetType = row.targetType || 1
  form.targetIds = row.targetIds || ''
  dialogVisible.value = true
}

const handleView = (row) => {
  currentMessage.value = row
  viewVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const senderId = userStore.userInfo?.id
    const senderName = userStore.userInfo?.realName || userStore.userInfo?.username || '系统管理员'
    const payload = {
      id: form.id,
      title: form.title,
      content: form.content,
      messageType: 1,
      targetType: form.targetType,
      targetIds: form.targetType === 1 ? null : form.targetIds,
      sendMethod: 1,
      senderId,
      senderName
    }
    if (form.id) {
      await updateMessage(form.id, payload)
      ElMessage.success('草稿更新成功')
    } else {
      await createMessage(payload)
      ElMessage.success('草稿创建成功')
    }
    dialogVisible.value = false
    loadMessages()
  } finally {
    submitLoading.value = false
  }
}

const handleSend = async (row) => {
  try {
    await ElMessageBox.confirm(`确认发送通知【${row.title}】吗？`, '提示', { type: 'warning' })
    await sendMessage(row.id)
    ElMessage.success('通知发送成功')
    loadMessages()
  } catch (_) {}
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除通知【${row.title}】吗？`, '提示', { type: 'warning' })
    await deleteMessage(row.id)
    ElMessage.success('删除成功')
    loadMessages()
  } catch (_) {}
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.message-manage {
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
.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.detail-line {
  margin-bottom: 12px;
}
.detail-content p {
  margin: 8px 0 0;
  line-height: 1.7;
  white-space: pre-wrap;
}
</style>

