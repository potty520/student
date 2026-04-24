<template>
  <div class="role-manage">
    <div class="page-header">
      <h2>角色管理</h2>
      <p>管理系统角色权限</p>
    </div>
    
    <el-card>
      <!-- 搜索和操作栏 -->
      <div class="card-header">
        <div class="search-form">
          <el-input
            v-model="searchForm.roleName"
            placeholder="请输入角色名称"
            style="width: 200px; margin-right: 10px;"
            clearable
          />
          <el-select
            v-model="searchForm.status"
            placeholder="角色状态"
            style="width: 120px; margin-right: 10px;"
            clearable
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
        <div class="header-buttons">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增角色
          </el-button>
        </div>
      </div>

      <!-- 角色表格 -->
      <el-table
        v-loading="loading"
        :data="roleList"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="角色描述" min-width="200" />
        <el-table-column label="权限" min-width="300">
          <template #default="{ row }">
            <el-tag 
              v-for="permission in row.permissions" 
              :key="permission.id" 
              size="small" 
              style="margin-right: 5px; margin-bottom: 5px;"
            >
              {{ permission.permissionName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色编码" prop="roleCode">
              <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="角色描述" prop="description">
              <el-input 
                v-model="roleForm.description" 
                type="textarea" 
                :rows="3" 
                placeholder="请输入角色描述"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch
                v-model="roleForm.status"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="权限配置" prop="permissionIds">
              <el-tree
                ref="permissionTreeRef"
                :data="permissionTree"
                :props="treeProps"
                show-checkbox
                node-key="id"
                :default-checked-keys="roleForm.permissionIds"
                :check-strictly="false"
                style="border: 1px solid #dcdfe6; border-radius: 4px; padding: 10px; max-height: 300px; overflow-y: auto;"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  updateRoleStatus,
  assignRolePermissions
} from '@/api/role'
import { getPermissionTree } from '@/api/permission'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const roleFormRef = ref()
const permissionTreeRef = ref()

// 搜索表单
const searchForm = reactive({
  roleName: '',
  status: ''
})

// 角色列表
const roleList = ref([])

// 权限树形数据
const permissionTree = ref([])

// 树形控件属性
const treeProps = {
  children: 'children',
  label: 'label'
}

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 角色表单
const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  permissionIds: [],
  status: 1
})

// 表单验证规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入角色描述', trigger: 'blur' }
  ],
  permissionIds: [
    { required: true, message: '请选择权限', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const normalizePermissionNodes = (nodes = []) => {
  return nodes.map(node => ({
    ...node,
    label: node.permissionName,
    children: normalizePermissionNodes(node.children || [])
  }))
}

// 方法
const handleSearch = () => {
  pagination.page = 1
  loadRoleList()
}

const handleReset = () => {
  searchForm.roleName = ''
  searchForm.status = ''
  pagination.page = 1
  loadRoleList()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(roleForm, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description,
    status: row.status,
    permissionIds: row.permissions.map(p => p.id)
  })
  dialogVisible.value = true
  
  // 设置权限树选中状态
  setTimeout(() => {
    if (permissionTreeRef.value) {
      permissionTreeRef.value.setCheckedKeys(roleForm.permissionIds)
    }
  }, 100)
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '禁用' : '启用'}角色"${row.roleName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const nextStatus = row.status === 1 ? 0 : 1
    await updateRoleStatus(row.id, nextStatus)
    ElMessage.success(`${nextStatus === 1 ? '启用' : '禁用'}成功`)
    loadRoleList()
  } catch {
    // 用户取消
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色"${row.roleName}"吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch {
    // 用户取消
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadRoleList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadRoleList()
}

const handleSubmit = async () => {
  if (!roleFormRef.value) return
  
  // 获取选中的权限ID
  const checkedKeys = permissionTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
  roleForm.permissionIds = [...checkedKeys, ...halfCheckedKeys]
  
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      if (roleForm.permissionIds.length === 0) {
        ElMessage.warning('请至少选择一个权限')
        return
      }
      
      submitLoading.value = true
      try {
        const payload = {
          roleName: roleForm.roleName,
          roleCode: roleForm.roleCode,
          description: roleForm.description,
          status: roleForm.status
        }

        let roleId = roleForm.id
        if (isEdit.value) {
          await updateRole(roleForm.id, payload)
          ElMessage.success('编辑成功')
        } else {
          const created = await createRole(payload)
          roleId = created.data?.id
          ElMessage.success('新增成功')
        }

        if (roleId) {
          await assignRolePermissions(roleId, roleForm.permissionIds)
        }

        dialogVisible.value = false
        loadRoleList()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(roleForm, {
    id: null,
    roleName: '',
    roleCode: '',
    description: '',
    permissionIds: [],
    status: 1
  })
  roleFormRef.value?.clearValidate()
  if (permissionTreeRef.value) {
    permissionTreeRef.value.setCheckedKeys([])
  }
}

const loadRoleList = () => {
  loading.value = true
  getRoleList({
    page: pagination.page,
    size: pagination.size,
    roleName: searchForm.roleName || undefined,
    status: searchForm.status === '' ? undefined : searchForm.status
  }).then((res) => {
    const pageData = res.data || {}
    roleList.value = (pageData.records || []).map((item) => ({
      ...item,
      permissions: item.permissions || []
    }))
    pagination.total = pageData.total || 0
  }).finally(() => {
    loading.value = false
  })
}

// 生命周期
onMounted(() => {
  getPermissionTree().then((res) => {
    permissionTree.value = normalizePermissionNodes(res.data || [])
  })
  loadRoleList()
})
</script>

<style scoped>
.role-manage {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
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
  .role-manage {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-form {
    justify-content: center;
  }
  
  .header-buttons {
    justify-content: center;
  }
  
  .pagination-container {
    text-align: center;
  }
}
</style>