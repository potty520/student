import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCurrentUser } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const permissions = ref([])
  const roles = ref([])

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const hasRole = computed(() => (role) => roles.value.includes(role))
  const hasPermission = computed(() => (permission) => permissions.value.includes(permission))

  // 登录
  const userLogin = async (loginForm) => {
    try {
      const response = await login(loginForm)
      if (response.code === 200) {
        token.value = response.data.token
        userInfo.value = response.data.userInfo
        permissions.value = response.data.permissions || []
        roles.value = response.data.roles || []
        
        // 保存到本地存储
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        
        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(response.message || '登录失败')
        return false
      }
    } catch (error) {
      console.error('登录错误:', error)
      ElMessage.error('网络错误，请稍后重试')
      return false
    }
  }

  // 退出登录
  const userLogout = async () => {
    try {
      await logout()
    } catch (error) {
      console.error('退出登录错误:', error)
    } finally {
      // 清除状态
      token.value = ''
      userInfo.value = null
      permissions.value = []
      roles.value = []
      
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      ElMessage.success('已退出登录')
    }
  }

  // 获取当前用户信息
  const getCurrentUserInfo = async () => {
    try {
      if (!token.value) return false
      
      const response = await getCurrentUser()
      if (response.code === 200) {
        userInfo.value = response.data.userInfo
        permissions.value = response.data.permissions || []
        roles.value = response.data.roles || []
        return true
      } else {
        // token可能已过期
        await userLogout()
        return false
      }
    } catch (error) {
      console.error('获取用户信息错误:', error)
      await userLogout()
      return false
    }
  }

  // 检查登录状态
  const checkLoginStatus = async () => {
    if (token.value) {
      const stored = localStorage.getItem('userInfo')
      if (stored) {
        userInfo.value = JSON.parse(stored)
      }
      // 验证token是否有效
      await getCurrentUserInfo()
    }
  }

  // 更新用户信息
  const updateUserInfo = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    // 状态
    token,
    userInfo,
    permissions,
    roles,
    
    // 计算属性
    isLoggedIn,
    hasRole,
    hasPermission,
    
    // 方法
    userLogin,
    userLogout,
    getCurrentUserInfo,
    checkLoginStatus,
    updateUserInfo
  }
})