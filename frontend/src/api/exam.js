import request from '@/utils/request'

export function getExamList(params) {
  return request({
    url: '/exams/list',
    method: 'get',
    params
  })
}

export function createExam(data) {
  return request({
    url: '/exams',
    method: 'post',
    data
  })
}

export function updateExam(id, data) {
  return request({
    url: `/exams/${id}`,
    method: 'put',
    data
  })
}

export function deleteExam(id) {
  return request({
    url: `/exams/${id}`,
    method: 'delete'
  })
}

export function batchDeleteExams(ids) {
  return request({
    url: '/exams/batch',
    method: 'delete',
    data: ids
  })
}

export function startExam(id) {
  return request({
    url: `/exams/${id}/start`,
    method: 'put'
  })
}

export function finishExam(id) {
  return request({
    url: `/exams/${id}/finish`,
    method: 'put'
  })
}

export function publishExam(id) {
  return request({
    url: `/exams/${id}/publish`,
    method: 'put'
  })
}
