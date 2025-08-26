import request from '@/utils/request'

/**
 * 年级管理API接口
 * 
 * @author Qoder
 * @since 2025-08-25
 */

/**
 * 分页查询年级列表
 * @param {Object} params 查询参数
 */
export function getGradeList(params) {
  return request({
    url: '/basic/grade/list',
    method: 'get',
    params
  })
}

/**
 * 获取所有启用的年级列表
 */
export function getAllActiveGrades() {
  return request({
    url: '/basic/grade/all',
    method: 'get'
  })
}

/**
 * 根据ID获取年级详情
 * @param {Number} id 年级ID
 */
export function getGradeById(id) {
  return request({
    url: `/basic/grade/${id}`,
    method: 'get'
  })
}

/**
 * 新增年级
 * @param {Object} data 年级信息
 */
export function createGrade(data) {
  return request({
    url: '/basic/grade',
    method: 'post',
    data
  })
}

/**
 * 更新年级
 * @param {Number} id 年级ID
 * @param {Object} data 年级信息
 */
export function updateGrade(id, data) {
  return request({
    url: `/basic/grade/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除年级
 * @param {Number} id 年级ID
 */
export function deleteGrade(id) {
  return request({
    url: `/basic/grade/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除年级
 * @param {Array} ids 年级ID列表
 */
export function batchDeleteGrades(ids) {
  return request({
    url: '/basic/grade/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新年级状态
 * @param {Number} id 年级ID
 * @param {Number} status 状态
 */
export function updateGradeStatus(id, status) {
  return request({
    url: `/basic/grade/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 根据学年获取年级列表
 * @param {String} schoolYear 学年
 */
export function getGradesBySchoolYear(schoolYear) {
  return request({
    url: `/basic/grade/school-year/${schoolYear}`,
    method: 'get'
  })
}