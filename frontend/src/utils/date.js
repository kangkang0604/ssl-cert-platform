import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.locale('zh-cn')
dayjs.extend(relativeTime)

/**
 * 格式化日期
 * @param {string|Date} date - 日期
 * @param {string} format - 格式
 * @returns {string}
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return '-'
  return dayjs(date).format(format)
}

/**
 * 格式化为相对时间
 * @param {string|Date} date - 日期
 * @returns {string}
 */
export function fromNow(date) {
  if (!date) return '-'
  return dayjs(date).fromNow()
}

/**
 * 获取剩余天数
 * @param {string|Date} date - 过期日期
 * @returns {number}
 */
export function getDaysRemaining(date) {
  if (!date) return 0
  return dayjs(date).diff(dayjs(), 'day')
}

/**
 * 检查是否即将过期
 * @param {string|Date} date - 过期日期
 * @param {number} days - 提前天数
 * @returns {boolean}
 */
export function isExpiringSoon(date, days = 30) {
  if (!date) return false
  return dayjs(date).diff(dayjs(), 'day') <= days
}

/**
 * 检查是否已过期
 * @param {string|Date} date - 过期日期
 * @returns {boolean}
 */
export function isExpired(date) {
  if (!date) return false
  return dayjs(date).isBefore(dayjs())
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string}
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 格式化数字
 * @param {number} num - 数字
 * @returns {string}
 */
export function formatNumber(num) {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}
