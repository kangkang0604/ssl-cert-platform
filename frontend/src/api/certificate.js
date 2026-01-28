import request from './request'

/**
 * 证书相关API
 */

/**
 * 分页查询证书列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getCertificateList(params) {
  return request({
    url: '/certificates',
    method: 'get',
    params
  })
}

/**
 * 获取证书详情
 * @param {Number} certId 证书ID
 * @returns {Promise}
 */
export function getCertificateDetail(certId) {
  return request({
    url: `/certificates/${certId}`,
    method: 'get'
  })
}

/**
 * 申请证书
 * @param {Object} data 申请参数
 * @returns {Promise}
 */
export function applyCertificate(data) {
  return request({
    url: '/certificates',
    method: 'post',
    data
  })
}

/**
 * 删除证书
 * @param {Number} certId 证书ID
 * @returns {Promise}
 */
export function deleteCertificate(certId) {
  return request({
    url: `/certificates/${certId}`,
    method: 'delete'
  })
}

/**
 * 手动续期证书
 * @param {Number} certId 证书ID
 * @returns {Promise}
 */
export function renewCertificate(certId) {
  return request({
    url: `/certificates/${certId}/renew`,
    method: 'post'
  })
}

/**
 * 下载证书
 * @param {Number} certId 证书ID
 * @returns {Promise}
 */
export function downloadCertificate(certId) {
  return request({
    url: `/certificates/${certId}/download`,
    method: 'get'
  })
}

/**
 * 获取证书品牌列表
 * @returns {Promise}
 */
export function getCertificateBrands() {
  return request({
    url: '/certificates/brands',
    method: 'get'
  })
}

/**
 * 获取DNS服务商列表
 * @returns {Promise}
 */
export function getDnsProviders() {
  return request({
    url: '/certificates/dns-providers',
    method: 'get'
  })
}
