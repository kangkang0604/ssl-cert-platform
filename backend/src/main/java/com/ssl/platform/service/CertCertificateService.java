package com.ssl.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ssl.platform.dto.CertApplyRequest;
import com.ssl.platform.dto.CertCertificateDTO;
import com.ssl.platform.dto.CertPageDTO;
import com.ssl.platform.entity.CertCertificate;

/**
 * 证书服务接口
 */
public interface CertCertificateService extends IService<CertCertificate> {

    /**
     * 分页查询证书列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @param status 状态
     * @return 证书分页列表
     */
    CertPageDTO pageCertificates(Long userId, Integer pageNum, Integer pageSize, String keyword, Integer status);

    /**
     * 获取证书详情
     *
     * @param userId 用户ID
     * @param certId 证书ID
     * @return 证书详情
     */
    CertCertificateDTO getCertificateDetail(Long userId, Long certId);

    /**
     * 申请证书
     *
     * @param userId 用户ID
     * @param request 申请请求
     * @return 证书ID
     */
    Long applyCertificate(Long userId, CertApplyRequest request);

    /**
     * 删除证书
     *
     * @param userId 用户ID
     * @param certId 证书ID
     */
    void deleteCertificate(Long userId, Long certId);

    /**
     * 手动续期证书
     *
     * @param userId 用户ID
     * @param certId 证书ID
     */
    void renewCertificate(Long userId, Long certId);

    /**
     * 检查证书是否需要续期
     *
     * @param userId 用户ID
     */
    void checkCertificatesForRenewal(Long userId);
}
