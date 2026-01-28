import request from './request'

/**
 * 用户相关API
 */

/**
 * 用户登录
 * @param {Object} data 登录参数
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data 注册参数
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export function getUserInfo() {
  return request({
    url: '/users/profile',
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {Object} data 用户信息
 * @returns {Promise}
 */
export function updateUserInfo(data) {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 * @param {Object} data 密码参数
 * @returns {Promise}
 */
export function changePassword(data) {
  return request({
    url: '/users/password',
    method: 'put',
    data
  })
}

/**
 * 获取用户套餐信息
 * @returns {Promise}
 */
export function getUserPackage() {
  return request({
    url: '/users/package',
    method: 'get'
  })
}
