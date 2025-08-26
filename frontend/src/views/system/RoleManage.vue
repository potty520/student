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
const roleList = ref([
  {
    id: 1,
    roleName: '超级管理员',
    roleCode: 'SUPER_ADMIN',
    description: '系统超级管理员，拥有所有权限',
    status: 1,
    createTime: '2024-01-01 10:00:00',
    permissions: [
      { id: 1, permissionName: '用户管理' },
      { id: 2, permissionName: '角色管理' },
      { id: 3, permissionName: '成绩管理' },
      { id: 4, permissionName: '基础数据' }
    ]
  },
  {
    id: 2,
    roleName: '教师',
    roleCode: 'TEACHER',
    description: '教师角色，可以管理成绩和班级信息',
    status: 1,
    createTime: '2024-01-02 10:00:00',
    permissions: [
      { id: 3, permissionName: '成绩管理' },
      { id: 4, permissionName: '基础数据' }
    ]
  },
  {
    id: 3,
    roleName: '学生',
    roleCode: 'STUDENT',
    description: '学生角色，只能查看自己的成绩',
    status: 1,
    createTime: '2024-01-03 10:00:00',
    permissions: [
      { id: 5, permissionName: '成绩查看' }
    ]
  }
])

// 权限树形数据
const permissionTree = ref([
  {
    id: 1,
    label: '系统管理',
    children: [
      { id: 1, label: '用户管理' },
      { id: 2, label: '角色管理' }
    ]
  },
  {
    id: 2,
    label: '成绩管理',
    children: [
      { id: 3, label: '成绩录入' },
      { id: 4, label: '成绩统计' },
      { id: 5, label: '成绩查看' }
    ]
  },
  {
    id: 3,
    label: '基础数据',
    children: [
      { id: 6, label: '年级管理' },
      { id: 7, label: '班级管理' },
      { id: 8, label: '学科管理' }
    ]
  }
])

// 树形控件属性
const treeProps = {
  children: 'children',
  label: 'label'
}

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 3
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
    
    // 这里应该调用API
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success(`${row.status === 1 ? '启用' : '禁用'}成功`)
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
    
    // 这里应该调用API
    const index = roleList.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      roleList.value.splice(index, 1)
      pagination.total--
    }
    ElMessage.success('删除成功')
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
        // 这里应该调用API
        await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
        
        if (isEdit.value) {
          // 编辑
          const index = roleList.value.findIndex(item => item.id === roleForm.id)
          if (index > -1) {
            Object.assign(roleList.value[index], {
              roleName: roleForm.roleName,
              roleCode: roleForm.roleCode,
              description: roleForm.description,
              status: roleForm.status,
              permissions: getPermissionsByIds(roleForm.permissionIds)
            })
          }
          ElMessage.success('编辑成功')
        } else {
          // 新增
          const newRole = {
            id: Date.now(),
            roleName: roleForm.roleName,
            roleCode: roleForm.roleCode,
            description: roleForm.description,
            status: roleForm.status,
            createTime: new Date().toLocaleString(),
            permissions: getPermissionsByIds(roleForm.permissionIds)
          }
          roleList.value.unshift(newRole)
          pagination.total++
          ElMessage.success('新增成功')
        }
        
        dialogVisible.value = false
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
  // 这里应该调用API加载角色列表
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 根据权限ID获取权限信息
const getPermissionsByIds = (ids) => {
  const permissions = []
  const permissionMap = {
    1: { id: 1, permissionName: '用户管理' },
    2: { id: 2, permissionName: '角色管理' },
    3: { id: 3, permissionName: '成绩录入' },
    4: { id: 4, permissionName: '成绩统计' },
    5: { id: 5, permissionName: '成绩查看' },
    6: { id: 6, permissionName: '年级管理' },
    7: { id: 7, permissionName: '班级管理' },
    8: { id: 8, permissionName: '学科管理' }
  }
  
  ids.forEach(id => {
    if (permissionMap[id]) {
      permissions.push(permissionMap[id])
    }
  })
  
  return permissions
}

// 生命周期
onMounted(() => {
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