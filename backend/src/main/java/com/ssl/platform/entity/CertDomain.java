package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 域名实体
 */
@Data
@TableName("cert_domain")
public class CertDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String domain;

    private Integer domainType;

    private String dnsProvider;

    private String dnsAccountId;

    private String dnsTxtVerification;

    private Integer verificationStatus;

    private LocalDateTime verificationTime;

    private Integer resolveStatus;

    private String resolveIp;

    private Integer caaStatus;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
