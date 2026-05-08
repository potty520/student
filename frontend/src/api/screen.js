import request from '@/utils/request'

/**
 * 获取大屏展示数据
 * @param {Object} params - { examId, courseId }
 */
export function getScreenData(params) {
  return request({
    url: '/screen/data',
    method: 'get',
    params
  })
}
