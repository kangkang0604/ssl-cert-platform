package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 证书实体
 */
@Data
@TableName("cert_certificate")
public class CertCertificate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证书ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联域名ID
     */
    private Long domainId;

    /**
     * 证书类型：1-单域名，2-多域名，3-通配符
     */
    private Integer certType;

    /**
     * 证书品牌：letsencrypt、zerossl、google
     */
    private String brand;

    /**
     * 域名（多个用逗号分隔）
     */
    private String domainName;

    /**
     * 证书主题
     */
    private String subject;

    /**
     * 证书颁发者
     */
    private String issuer;

    /**
     * 证书序列号
     */
    private String serialNumber;

    /**
     * 证书DN
     */
    private String certDn;

    /**
     * 证书内容（PEM格式）
     */
    private String certPem;

    /**
     * 私钥内容
     */
    private String privateKey;

    /**
     * 生效时间
     */
    private LocalDateTime notBefore;

    /**
     * 过期时间
     */
    private LocalDateTime notAfter;

    /**
     * 剩余天数
     */
    private Integer daysRemaining;

    /**
     * 证书状态：0-申请中，1-已签发，2-即将过期，3-已过期，4-已吊销，5-申请失败
     */
    private Integer status;

    /**
     * 是否自动续期：0-否，1-是
     */
    private Integer autoRenew;

    /**
     * 续期次数
     */
    private Integer renewCount;

    /**
     * 最后续期时间
     */
    private LocalDateTime lastRenewTime;

    /**
     * 下次续期时间
     */
    private LocalDateTime nextRenewTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
