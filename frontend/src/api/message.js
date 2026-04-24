import request from '@/utils/request'

export function getSystemNotices(limit = 10) {
  return request({
    url: '/messages/system-notices',
    method: 'get',
    params: { limit }
  })
}

export function getMessageList(params) {
  return request({
    url: '/messages/list',
    method: 'get',
    params
  })
}

export function createMessage(data) {
  return request({
    url: '/messages',
    method: 'post',
    data
  })
}

export function updateMessage(id, data) {
  return request({
    url: `/messages/${id}`,
    method: 'put',
    data
  })
}

export function sendMessage(id) {
  return request({
    url: `/messages/${id}/send`,
    method: 'post'
  })
}

export function deleteMessage(id) {
  return request({
    url: `/messages/${id}`,
    method: 'delete'
  })
}

export function sendSystemNotice(params) {
  return request({
    url: '/messages/system-notice',
    method: 'post',
    params
  })
}
