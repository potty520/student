import request from '@/utils/request'

export function getTeacherList(params) {
  return request({
    url: '/teachers/list',
    method: 'get',
    params
  })
}

export function getTeacherById(id) {
  return request({
    url: `/teachers/${id}`,
    method: 'get'
  })
}

export function createTeacher(data) {
  return request({
    url: '/teachers',
    method: 'post',
    data
  })
}

export function updateTeacher(id, data) {
  return request({
    url: `/teachers/${id}`,
    method: 'put',
    data
  })
}

export function deleteTeacher(id) {
  return request({
    url: `/teachers/${id}`,
    method: 'delete'
  })
}

export function batchDeleteTeachers(ids) {
  return request({
    url: '/teachers/batch',
    method: 'delete',
    data: ids
  })
}

export function updateTeacherStatus(id, status) {
  return request({
    url: `/teachers/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function getActiveTeachers() {
  return request({
    url: '/teachers/active',
    method: 'get'
  })
}
