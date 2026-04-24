import request from '@/utils/request'

/**
 * 课程管理 API
 */

export function getCourseList(params) {
  return request({
    url: '/courses/list',
    method: 'get',
    params
  })
}

export function getCourseById(id) {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  })
}

export function createCourse(data) {
  return request({
    url: '/courses',
    method: 'post',
    data
  })
}

export function updateCourse(id, data) {
  return request({
    url: `/courses/${id}`,
    method: 'put',
    data
  })
}

export function deleteCourse(id) {
  return request({
    url: `/courses/${id}`,
    method: 'delete'
  })
}

export function batchDeleteCourses(ids) {
  return request({
    url: '/courses/batch',
    method: 'delete',
    data: ids
  })
}

export function updateCourseStatus(id, status) {
  return request({
    url: `/courses/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function updateCourseSortOrder(id, sortOrder) {
  return request({
    url: `/courses/${id}/sort`,
    method: 'put',
    params: { sortOrder }
  })
}

export function getActiveCourses() {
  return request({
    url: '/courses/active',
    method: 'get'
  })
}

export function getCoursesByStage(stage) {
  return request({
    url: `/courses/stage/${stage}`,
    method: 'get'
  })
}

export function getCoursesByType(courseType) {
  return request({
    url: `/courses/type/${courseType}`,
    method: 'get'
  })
}

