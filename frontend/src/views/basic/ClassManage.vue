<template>
  <div class="class-manage">
    <div class="page-header">
      <h2>班级管理</h2>
      <p>管理学校班级信息</p>
    </div>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增班级
          </el-button>
        </div>
      </template>
      
      <el-table :data="classList" v-loading="loading">
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="gradeName" label="年级" />
        <el-table-column prop="studentCount" label="学生数量" />
        <el-table-column prop="teacherName" label="班主任" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const classList = ref([
  { id: 1, className: '一年级1班', gradeName: '一年级', studentCount: 30, teacherName: '张老师' },
  { id: 2, className: '一年级2班', gradeName: '一年级', studentCount: 29, teacherName: '李老师' },
  { id: 3, className: '二年级1班', gradeName: '二年级', studentCount: 31, teacherName: '王老师' }
])

const handleAdd = () => {
  console.log('添加班级')
}

const handleEdit = (row) => {
  console.log('编辑班级', row)
}

const handleDelete = (row) => {
  console.log('删除班级', row)
}

onMounted(() => {
  // 加载数据
})
</script>

<style scoped>
.class-manage {
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
}
</style>