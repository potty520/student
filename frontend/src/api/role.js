import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/roles/list',
    method: 'get',
    params
  })
}

export function getActiveRoles() {
  return request({
    url: '/roles/active',
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

export function updateRoleStatus(id, status) {
  return request({
    url: `/roles/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function assignRolePermissions(id, permissionIds) {
  return request({
    url: `/roles/${id}/permissions`,
    method: 'post',
    data: permissionIds
  })
}
