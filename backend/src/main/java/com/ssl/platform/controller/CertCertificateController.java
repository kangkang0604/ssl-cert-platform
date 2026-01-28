package com.ssl.platform.controller;

import com.ssl.platform.common.Result;
import com.ssl.platform.dto.CertApplyRequest;
import com.ssl.platform.dto.CertCertificateDTO;
import com.ssl.platform.dto.CertPageDTO;
import com.ssl.platform.service.CertCertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 证书控制器
 */
@Slf4j
@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
@Tag(name = "证书管理", description = "证书申请、查询、续期等相关接口")
public class CertCertificateController {

    private final CertCertificateService certCertificateService;

    /**
     * 分页查询证书列表
     */
    @GetMapping
    @Operation(summary = "分页查询证书", description = "获取当前用户的证书列表")
    public Result<CertPageDTO> pageCertificates(@AuthenticationPrincipal Long userId,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer status) {
        CertPageDTO page = certCertificateService.pageCertificates(
                userId, pageNum, pageSize, keyword, status);
        return Result.success(page);
    }

    /**
     * 获取证书详情
     */
    @GetMapping("/{certId}")
    @Operation(summary = "获取证书详情", description = "获取指定证书的详细信息")
    public Result<CertCertificateDTO> getCertificateDetail(@AuthenticationPrincipal Long userId,
                                                           @PathVariable Long certId) {
        CertCertificateDTO cert = certCertificateService.getCertificateDetail(userId, certId);
        return Result.success(cert);
    }

    /**
     * 申请证书
     */
    @PostMapping
    @Operation(summary = "申请证书", description = "申请新的SSL证书")
    public Result<Long> applyCertificate(@AuthenticationPrincipal Long userId,
                                         @Valid @RequestBody CertApplyRequest request) {
        Long certId = certCertificateService.applyCertificate(userId, request);
        return Result.success("证书申请已提交", certId);
    }

    /**
     * 删除证书
     */
    @DeleteMapping("/{certId}")
    @Operation(summary = "删除证书", description = "删除指定的证书")
    public Result<String> deleteCertificate(@AuthenticationPrincipal Long userId,
                                            @PathVariable Long certId) {
        certCertificateService.deleteCertificate(userId, certId);
        return Result.success("删除成功");
    }

    /**
     * 手动续期证书
     */
    @PostMapping("/{certId}/renew")
    @Operation(summary = "手动续期", description = "手动触发证书续期")
    public Result<String> renewCertificate(@AuthenticationPrincipal Long userId,
                                           @PathVariable Long certId) {
        certCertificateService.renewCertificate(userId, certId);
        return Result.success("续期成功");
    }

    /**
     * 下载证书
     */
    @GetMapping("/{certId}/download")
    @Operation(summary = "下载证书", description = "下载证书文件")
    public Result<CertCertificateDTO> downloadCertificate(@AuthenticationPrincipal Long userId,
                                                          @PathVariable Long certId) {
        CertCertificateDTO cert = certCertificateService.getCertificateDetail(userId, certId);
        return Result.success(cert);
    }
}
