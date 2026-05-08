import request from '@/utils/request'

export function getClassList(params) {
  return request({
    url: '/basic/class/list',
    method: 'get',
    params
  })
}

export function getAllClasses() {
  return request({
    url: '/basic/class/all',
    method: 'get'
  })
}

export function getClassesByGrade(gradeId) {
  return request({
    url: `/basic/class/grade/${gradeId}`,
    method: 'get'
  })
}

export function getClassById(id) {
  return request({
    url: `/basic/class/${id}`,
    method: 'get'
  })
}

export function createClass(data) {
  return request({
    url: '/basic/class',
    method: 'post',
    data
  })
}

export function updateClass(id, data) {
  return request({
    url: `/basic/class/${id}`,
    method: 'put',
    data
  })
}

export function deleteClass(id) {
  return request({
    url: `/basic/class/${id}`,
    method: 'delete'
  })
}

export function batchDeleteClasses(ids) {
  return request({
    url: '/basic/class/batch',
    method: 'delete',
    data: ids
  })
}

export function updateClassStatus(id, status) {
  return request({
    url: `/basic/class/${id}/status`,
    method: 'put',
    params: { status }
  })
}
