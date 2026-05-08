import request from '@/utils/request'

export function getStudentList(params) {
  return request({
    url: '/students/list',
    method: 'get',
    params
  })
}

export function getStudentById(id) {
  return request({
    url: `/students/${id}`,
    method: 'get'
  })
}

export function createStudent(data) {
  return request({
    url: '/students',
    method: 'post',
    data
  })
}

export function updateStudent(id, data) {
  return request({
    url: `/students/${id}`,
    method: 'put',
    data
  })
}

export function deleteStudent(id) {
  return request({
    url: `/students/${id}`,
    method: 'delete'
  })
}

export function batchDeleteStudents(ids) {
  return request({
    url: '/students/batch',
    method: 'delete',
    data: ids
  })
}

export function updateStudentStatus(id, status) {
  return request({
    url: `/students/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function getStudentsByClass(classId) {
  return request({
    url: `/students/class/${classId}`,
    method: 'get'
  })
}
