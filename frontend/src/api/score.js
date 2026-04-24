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
    url: '/scores/list',
    method: 'get',
    params: {
      ...params,
      page: 1,
      size: 1000
    }
  })
}

/**
 * 批量录入成绩
 * @param {Array} data 成绩列表
 */
export function batchCreateScores(data) {
  return request({
    url: '/scores/batch',
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
    url: `/scores/${scoreId}`,
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
    url: `/scores/${scoreId}`,
    method: 'delete'
  })
}

/**
 * 计算成绩排名
 * @param {Object} params 参数
 */
export function calculateRankings(params) {
  return request({
    url: `/scores/ranking/exam/${params.examId}`,
    method: 'get',
    params
  })
}

/**
 * 生成成绩统计
 * @param {Object} params 查询参数
 */
export function generateStatistics(params) {
  return request({
    url: '/scores/statistics/grade',
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
    url: `/scores/report/student/${params.studentId}`,
    method: 'get',
    params: {
      examId: params.examId
    }
  })
}

/**
 * 获取考试列表
 */
export function getExamList(params) {
  return request({
    url: '/exams/list',
    method: 'get',
    params: {
      page: 1,
      size: 100,
      ...params
    }
  })
}

/**
 * 获取班级列表
 */
export function getClassList() {
  return request({
    url: '/basic/class/all',
    method: 'get'
  })
}

/**
 * 获取课程列表
 */
export function getCourseList() {
  return request({
    url: '/courses/active',
    method: 'get'
  })
}

/**
 * 获取班级学生列表
 */
export function getStudentsByClass(classId) {
  return request({
    url: `/students/class/${classId}`,
    method: 'get'
  })
}

/**
 * 获取考试课程下的成绩列表
 */
export function getScoresByExamAndCourse(examId, courseId) {
  return request({
    url: `/scores/exam/${examId}/course/${courseId}`,
    method: 'get'
  })
}