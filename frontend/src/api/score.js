import request from '@/utils/request'

/**
 * 成绩管理API接口
 * 
 * @author Qoder
 * @since 2025-08-25
 */

/**
 * 获取成绩录入模板
 * @param {Object} params 查询参数
 */
export function getScoreEntryTemplate(params) {
  return request({
    url: '/grade/score/template',
    method: 'get',
    params
  })
}

/**
 * 批量录入成绩
 * @param {Array} data 成绩列表
 */
export function batchCreateScores(data) {
  return request({
    url: '/grade/score/batch',
    method: 'post',
    data
  })
}

/**
 * 更新单个成绩
 * @param {Number} scoreId 成绩ID
 * @param {Object} data 成绩信息
 */
export function updateScore(scoreId, data) {
  return request({
    url: `/grade/score/${scoreId}`,
    method: 'put',
    data
  })
}

/**
 * 删除成绩
 * @param {Number} scoreId 成绩ID
 */
export function deleteScore(scoreId) {
  return request({
    url: `/grade/score/${scoreId}`,
    method: 'delete'
  })
}

/**
 * 计算成绩排名
 * @param {Object} params 参数
 */
export function calculateRankings(params) {
  return request({
    url: '/grade/score/calculate-rankings',
    method: 'post',
    params
  })
}

/**
 * 生成成绩统计
 * @param {Object} params 查询参数
 */
export function generateStatistics(params) {
  return request({
    url: '/grade/score/statistics',
    method: 'get',
    params
  })
}

/**
 * 获取学生成绩详情
 * @param {Object} params 查询参数
 */
export function getStudentScoreDetail(params) {
  return request({
    url: '/grade/score/student-detail',
    method: 'get',
    params
  })
}