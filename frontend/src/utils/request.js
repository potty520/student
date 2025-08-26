import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    // 打印请求信息（开发环境）
    if (import.meta.env.DEV) {
      console.log('发送请求:', config.method?.toUpperCase(), config.url, config.data || config.params)
    }
    
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 打印响应信息（开发环境）
    if (import.meta.env.DEV) {
      console.log('收到响应:', response.config.url, res)
    }
    
    // 响应成功
    if (res.code === 200) {
      return res
    }
    
    // 处理业务错误
    if (res.code === 401) {
      // 未授权，跳转登录页
      ElMessage.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.userLogout()
      router.push('/login')
      return Promise.reject(new Error(res.message || '未授权'))
    }
    
    if (res.code === 403) {
      ElMessage.error('没有权限执行此操作')
      return Promise.reject(new Error(res.message || '权限不足'))
    }
    
    // 其他业务错误
    ElMessage.error(res.message || '操作失败')
    return Promise.reject(new Error(res.message || '操作失败'))
  },
  error => {
    console.error('响应拦截器错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          ElMessage.error(data?.message || '请求参数错误')
          break
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.userLogout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.message === 'Network Error') {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

// 文件上传请求
export const uploadRequest = axios.create({
  baseURL: '/api',
  timeout: 300000, // 5分钟
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})

// 为上传请求添加token
uploadRequest.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

export default request