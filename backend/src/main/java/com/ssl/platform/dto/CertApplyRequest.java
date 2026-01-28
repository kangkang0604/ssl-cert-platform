package com.ssl.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 证书申请请求DTO
 */
@Data
@Schema(description = "证书申请请求")
public class CertApplyRequest {

    /**
     * 证书类型：1-单域名，2-多域名，3-通配符
     */
    @Schema(description = "证书类型：1-单域名，2-多域名，3-通配符", required = true)
    @NotEmpty(message = "证书类型不能为空")
    private Integer certType;

    /**
     * 证书品牌：letsencrypt、zerossl、google
     */
    @Schema(description = "证书品牌：letsencrypt、zerossl、google", required = true)
    @NotBlank(message = "证书品牌不能为空")
    private String brand;

    /**
     * 域名列表
     */
    @Schema(description = "域名列表", required = true)
    @NotEmpty(message = "域名不能为空")
    private List<String> domains;

    /**
     * DNS服务商：aliyun、tencent、dnspod、cloudflare
     */
    @Schema(description = "DNS服务商")
    private String dnsProvider;

    /**
     * DNS AccessKey ID
     */
    @Schema(description = "DNS AccessKey ID")
    private String dnsAccessKeyId;

    /**
     * DNS AccessKey Secret
     */
    @Schema(description = "DNS AccessKey Secret")
    private String dnsAccessKeySecret;

    /**
     * 是否自动续期：0-否，1-是
     */
    @Schema(description = "是否自动续期")
    private Integer autoRenew = 1;
}
