import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

// 设置中文
dayjs.locale('zh-cn')

/**
 * 格式化日期
 * @param {Date|string} date 日期
 * @param {string} format 格式化字符串
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return dayjs(date).format(format)
}

/**
 * 格式化日期时间
 * @param {Date|string} date 日期
 * @returns {string} 格式化后的日期时间字符串
 */
export function formatDateTime(date) {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
}

/**
 * 获取相对时间
 * @param {Date|string} date 日期
 * @returns {string} 相对时间字符串
 */
export function getRelativeTime(date) {
  if (!date) return ''
  return dayjs(date).fromNow()
}

/**
 * 获取当前日期
 * @param {string} format 格式化字符串
 * @returns {string} 当前日期字符串
 */
export function getCurrentDate(format = 'YYYY-MM-DD') {
  return dayjs().format(format)
}

/**
 * 获取当前日期时间
 * @returns {string} 当前日期时间字符串
 */
export function getCurrentDateTime() {
  return dayjs().format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 计算年龄
 * @param {Date|string} birthDate 出生日期
 * @returns {number} 年龄
 */
export function calculateAge(birthDate) {
  if (!birthDate) return 0
  return dayjs().diff(dayjs(birthDate), 'year')
}

/**
 * 判断是否是今天
 * @param {Date|string} date 日期
 * @returns {boolean} 是否是今天
 */
export function isToday(date) {
  if (!date) return false
  return dayjs(date).isSame(dayjs(), 'day')
}

/**
 * 获取学年字符串
 * @param {Date|string} date 日期
 * @returns {string} 学年字符串 如：2024-2025
 */
export function getSchoolYear(date = new Date()) {
  const year = dayjs(date).year()
  const month = dayjs(date).month() + 1 // dayjs的月份从0开始
  
  if (month >= 9) {
    // 9月及以后，当前学年是当年到下一年
    return `${year}-${year + 1}`
  } else {
    // 8月及以前，当前学年是上一年到当年
    return `${year - 1}-${year}`
  }
}

/**
 * 获取学期
 * @param {Date|string} date 日期
 * @returns {number} 学期 1:第一学期 2:第二学期
 */
export function getSemester(date = new Date()) {
  const month = dayjs(date).month() + 1 // dayjs的月份从0开始
  
  if (month >= 9 || month <= 1) {
    return 1 // 第一学期：9月-1月
  } else {
    return 2 // 第二学期：2月-8月
  }
}