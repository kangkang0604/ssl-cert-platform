package com.ssl.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证书数据传输对象
 */
@Data
@Schema(description = "证书信息")
public class CertCertificateDTO {

    /**
     * 证书ID
     */
    @Schema(description = "证书ID")
    private Long id;

    /**
     * 证书类型：1-单域名，2-多域名，3-通配符
     */
    @Schema(description = "证书类型：1-单域名，2-多域名，3-通配符")
    private Integer certType;

    /**
     * 证书品牌
     */
    @Schema(description = "证书品牌")
    private String brand;

    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domainName;

    /**
     * 证书主题
     */
    @Schema(description = "证书主题")
    private String subject;

    /**
     * 证书颁发者
     */
    @Schema(description = "证书颁发者")
    private String issuer;

    /**
     * 证书序列号
     */
    @Schema(description = "证书序列号")
    private String serialNumber;

    /**
     * 生效时间
     */
    @Schema(description = "生效时间")
    private LocalDateTime notBefore;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime notAfter;

    /**
     * 剩余天数
     */
    @Schema(description = "剩余天数")
    private Integer daysRemaining;

    /**
     * 证书状态
     */
    @Schema(description = "证书状态")
    private Integer status;

    /**
     * 是否自动续期
     */
    @Schema(description = "是否自动续期")
    private Integer autoRenew;

    /**
     * 续期次数
     */
    @Schema(description = "续期次数")
    private Integer renewCount;

    /**
     * 证书内容（PEM格式）
     */
    @Schema(description = "证书内容")
    private String certPem;

    /**
     * 私钥内容
     */
    @Schema(description = "私钥内容")
    private String privateKey;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 证书状态文本
     */
    @Schema(description = "证书状态文本")
    private String statusText;

    /**
     * 证书类型文本
     */
    @Schema(description = "证书类型文本")
    private String certTypeText;
}
