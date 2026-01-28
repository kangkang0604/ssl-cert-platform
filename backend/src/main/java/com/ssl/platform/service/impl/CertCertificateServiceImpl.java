package com.ssl.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssl.platform.constant.Constants;
import com.ssl.platform.dto.CertApplyRequest;
import com.ssl.platform.dto.CertCertificateDTO;
import com.ssl.platform.dto.CertPageDTO;
import com.ssl.platform.entity.CertCertificate;
import com.ssl.platform.entity.CertDomain;
import com.ssl.platform.entity.SysUser;
import com.ssl.platform.exception.BusinessException;
import com.ssl.platform.mapper.CertCertificateMapper;
import com.ssl.platform.mapper.CertDomainMapper;
import com.ssl.platform.service.CertCertificateService;
import com.ssl.platform.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 证书服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertCertificateServiceImpl extends ServiceImpl<CertCertificateMapper, CertCertificate>
        implements CertCertificateService {

    private final CertCertificateMapper certCertificateMapper;
    private final CertDomainMapper certDomainMapper;
    private final SysUserService sysUserService;

    @Override
    public CertPageDTO pageCertificates(Long userId, Integer pageNum, Integer pageSize,
                                        String keyword, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<CertCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CertCertificate::getUserId, userId);
        wrapper.eq(CertCertificate::getDeleted, 0);

        // 关键词搜索
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(CertCertificate::getDomainName, keyword)
                    .or()
                    .like(CertCertificate::getSubject, keyword));
        }

        // 状态筛选
        if (status != null) {
            wrapper.eq(CertCertificate::getStatus, status);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(CertCertificate::getCreateTime);

        // 分页查询
        Page<CertCertificate> page = new Page<>(pageNum, pageSize);
        Page<CertCertificate> certPage = certCertificateMapper.selectPage(page, wrapper);

        // 转换为DTO
        List<CertCertificateDTO> list = certPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建统计信息
        CertPageDTO.CertStatistics statistics = buildStatistics(userId);

        // 构建分页结果
        CertPageDTO result = new CertPageDTO();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(certPage.getTotal());
        result.setPages((int) certPage.getPages());
        result.setList(list);
        result.setStatistics(statistics);

        return result;
    }

    @Override
    public CertCertificateDTO getCertificateDetail(Long userId, Long certId) {
        CertCertificate cert = certCertificateMapper.selectById(certId);
        if (cert == null || !cert.getUserId().equals(userId) || cert.getDeleted() == 1) {
            throw new BusinessException("证书不存在");
        }
        return convertToDTO(cert);
    }

    @Override
    public Long applyCertificate(Long userId, CertApplyRequest request) {
        // 获取用户信息，检查套餐限制
        SysUser user = sysUserService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查证书数量限制
        int certCount = Math.toIntExact(
                certCertificateMapper.selectCount(
                        new LambdaQueryWrapper<CertCertificate>()
                                .eq(CertCertificate::getUserId, userId)
                                .eq(CertCertificate::getDeleted, 0)
                )
        );

        if (user.getTotalCertCount() > 0 && certCount >= user.getTotalCertCount()) {
            throw new BusinessException("已超出套餐证书数量限制，请升级套餐");
        }

        // 验证域名
        validateDomains(request.getDomains());

        // 创建证书记录
        CertCertificate cert = new CertCertificate();
        cert.setUserId(userId);
        cert.setCertType(request.getCertType());
        cert.setBrand(request.getBrand());
        cert.setDomainName(String.join(",", request.getDomains()));
        cert.setStatus(Constants.CERT_STATUS_APPLYING); // 申请中
        cert.setAutoRenew(request.getAutoRenew());
        cert.setRenewCount(0);
        cert.setNotBefore(LocalDateTime.now());

        // 设置到期时间（Let's Encrypt证书有效期90天）
        cert.setNotAfter(LocalDateTime.now().plusDays(90));
        cert.setDaysRemaining(90);
        cert.setNextRenewTime(cert.getNotAfter().minusDays(30));

        certCertificateMapper.insert(cert);

        log.info("证书申请成功: certId={}, userId={}, domains={}",
                cert.getId(), userId, request.getDomains());

        return cert.getId();
    }

    @Override
    public void deleteCertificate(Long userId, Long certId) {
        CertCertificate cert = certCertificateMapper.selectById(certId);
        if (cert == null || !cert.getUserId().equals(userId)) {
            throw new BusinessException("证书不存在");
        }

        cert.setDeleted(1);
        certCertificateMapper.updateById(cert);

        log.info("证书删除成功: certId={}, userId={}", certId, userId);
    }

    @Override
    public void renewCertificate(Long userId, Long certId) {
        CertCertificate cert = certCertificateMapper.selectById(certId);
        if (cert == null || !cert.getUserId().equals(userId)) {
            throw new BusinessException("证书不存在");
        }

        if (cert.getStatus() != Constants.CERT_STATUS_ISSUED) {
            throw new BusinessException("只有已签发的证书才能续期");
        }

        // TODO: 调用Let's Encrypt API进行证书续期
        // 这里模拟续期过程
        cert.setRenewCount(cert.getRenewCount() + 1);
        cert.setLastRenewTime(LocalDateTime.now());
        cert.setNotAfter(LocalDateTime.now().plusDays(90));
        cert.setDaysRemaining(90);
        cert.setNextRenewTime(cert.getNotAfter().minusDays(30));

        certCertificateMapper.updateById(cert);

        log.info("证书续期成功: certId={}, userId={}, renewCount={}",
                certId, userId, cert.getRenewCount());
    }

    @Override
    public void checkCertificatesForRenewal(Long userId) {
        // 查找需要续期的证书
        LambdaQueryWrapper<CertCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CertCertificate::getUserId, userId);
        wrapper.eq(CertCertificate::getDeleted, 0);
        wrapper.eq(CertCertificate::getAutoRenew, 1);
        wrapper.eq(CertCertificate::getStatus, Constants.CERT_STATUS_ISSUED);
        wrapper.le(CertCertificate::getNextRenewTime, LocalDateTime.now());

        List<CertCertificate> certificates = certCertificateMapper.selectList(wrapper);

        for (CertCertificate cert : certificates) {
            try {
                renewCertificate(userId, cert.getId());
            } catch (Exception e) {
                log.error("证书自动续期失败: certId={}", cert.getId(), e);
                cert.setErrorMessage(e.getMessage());
                cert.setStatus(Constants.CERT_STATUS_FAILED);
                certCertificateMapper.updateById(cert);
            }
        }
    }

    /**
     * 转换为DTO
     */
    private CertCertificateDTO convertToDTO(CertCertificate cert) {
        CertCertificateDTO dto = new CertCertificateDTO();
        dto.setId(cert.getId());
        dto.setCertType(cert.getCertType());
        dto.setBrand(cert.getBrand());
        dto.setDomainName(cert.getDomainName());
        dto.setSubject(cert.getSubject());
        dto.setIssuer(cert.getIssuer());
        dto.setSerialNumber(cert.getSerialNumber());
        dto.setNotBefore(cert.getNotBefore());
        dto.setNotAfter(cert.getNotAfter());
        dto.setDaysRemaining(cert.getDaysRemaining());
        dto.setStatus(cert.getStatus());
        dto.setAutoRenew(cert.getAutoRenew());
        dto.setRenewCount(cert.getRenewCount());
        dto.setCertPem(cert.getCertPem());
        dto.setPrivateKey(cert.getPrivateKey());
        dto.setCreateTime(cert.getCreateTime());

        // 设置状态文本
        dto.setStatusText(getStatusText(cert.getStatus()));
        dto.setCertTypeText(getCertTypeText(cert.getCertType()));

        return dto;
    }

    /**
     * 构建统计信息
     */
    private CertPageDTO.CertStatistics buildStatistics(Long userId) {
        CertPageDTO.CertStatistics statistics = new CertPageDTO.CertStatistics();

        LambdaQueryWrapper<CertCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CertCertificate::getUserId, userId);
        wrapper.eq(CertCertificate::getDeleted, 0);

        // 总数
        statistics.setTotal(Math.toIntExact(certCertificateMapper.selectCount(wrapper)));

        // 已签发
        statistics.setIssued(Math.toIntExact(certCertificateMapper.selectCount(
                new LambdaQueryWrapper<CertCertificate>()
                        .eq(CertCertificate::getUserId, userId)
                        .eq(CertCertificate::getDeleted, 0)
                        .eq(CertCertificate::getStatus, Constants.CERT_STATUS_ISSUED))));

        // 即将过期
        statistics.setExpiring(Math.toIntExact(certCertificateMapper.selectCount(
                new LambdaQueryWrapper<CertCertificate>()
                        .eq(CertCertificate::getUserId, userId)
                        .eq(CertCertificate::getDeleted, 0)
                        .eq(CertCertificate::getStatus, Constants.CERT_STATUS_EXPIRING))));

        // 已过期
        statistics.setExpired(Math.toIntExact(certCertificateMapper.selectCount(
                new LambdaQueryWrapper<CertCertificate>()
                        .eq(CertCertificate::getUserId, userId)
                        .eq(CertCertificate::getDeleted, 0)
                        .eq(CertCertificate::getStatus, Constants.CERT_STATUS_EXPIRED))));

        // 申请中
        statistics.setApplying(Math.toIntExact(certCertificateMapper.selectCount(
                new LambdaQueryWrapper<CertCertificate>()
                        .eq(CertCertificate::getUserId, userId)
                        .eq(CertCertificate::getDeleted, 0)
                        .eq(CertCertificate::getStatus, Constants.CERT_STATUS_APPLYING))));

        return statistics;
    }

    /**
     * 验证域名
     */
    private void validateDomains(List<String> domains) {
        for (String domain : domains) {
            // 验证域名格式
            if (!isValidDomain(domain)) {
                throw new BusinessException("域名格式不正确: " + domain);
            }

            // 检查域名是否已存在
            LambdaQueryWrapper<CertDomain> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CertDomain::getDomain, domain);
            wrapper.eq(CertDomain::getDeleted, 0);
            if (certDomainMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("域名已存在: " + domain);
            }
        }
    }

    /**
     * 验证域名格式
     */
    private boolean isValidDomain(String domain) {
        // 简单验证：域名只能包含字母、数字、中划线和点
        return domain != null && domain.matches("^[a-zA-Z0-9][a-zA-Z0-9-]*(\\.[a-zA-Z0-9][a-zA-Z0-9-]*)*\\.[a-zA-Z]{2,}$");
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(int status) {
        if (status == Constants.CERT_STATUS_APPLYING) {
            return "申请中";
        } else if (status == Constants.CERT_STATUS_ISSUED) {
            return "已签发";
        } else if (status == Constants.CERT_STATUS_EXPIRING) {
            return "即将过期";
        } else if (status == Constants.CERT_STATUS_EXPIRED) {
            return "已过期";
        } else if (status == Constants.CERT_STATUS_REVOKED) {
            return "已吊销";
        } else if (status == Constants.CERT_STATUS_FAILED) {
            return "申请失败";
        }
        return "未知";
    }

    /**
     * 获取证书类型文本
     */
    private String getCertTypeText(int certType) {
        if (certType == Constants.CERT_TYPE_SINGLE) {
            return "单域名";
        } else if (certType == Constants.CERT_TYPE_MULTI) {
            return "多域名";
        } else if (certType == Constants.CERT_TYPE_WILDCARD) {
            return "通配符";
        }
        return "未知";
    }
}
