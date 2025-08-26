<template>
  <div class="course-manage">
    <div class="page-header">
      <h2>课程管理</h2>
      <p>管理学校课程信息</p>
    </div>
    
    <el-card>
      <div class="content-placeholder">
        <el-empty description="课程管理功能开发中..." />
      </div>
    </el-card>
  </div>
</template>

<script setup>
// 课程管理功能待完善
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

.content-placeholder {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>