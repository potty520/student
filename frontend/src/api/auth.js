import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data 登录表单数据
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

/**
 * 修改密码
 * @param {Object} data 修改密码数据
 */
export function changePassword(data) {
  return request({
    url: '/auth/changePassword',
    method: 'post',
    data
  })
}

/**
 * 刷新token
 */
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}