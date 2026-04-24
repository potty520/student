import request from '@/utils/request'

export async function getOverviewStats() {
  const [students, teachers, classes, exams] = await Promise.all([
    request({ url: '/students/list', method: 'get', params: { page: 1, size: 1 } }),
    request({ url: '/teachers/list', method: 'get', params: { page: 1, size: 1 } }),
    request({ url: '/basic/class/list', method: 'get', params: { page: 1, size: 1 } }),
    request({ url: '/exams/list', method: 'get', params: { page: 1, size: 1 } })
  ])

  return {
    studentTotal: students.data?.total || 0,
    teacherTotal: teachers.data?.total || 0,
    classTotal: classes.data?.totalElements || classes.data?.total || 0,
    examTotal: exams.data?.total || 0
  }
}

export function getRecentExams() {
  return request({
    url: '/exams/upcoming',
    method: 'get',
    params: { page: 1, size: 5 }
  })
}

export function getSystemNotices() {
  return request({
    url: '/messages/system-notices',
    method: 'get'
  })
}
