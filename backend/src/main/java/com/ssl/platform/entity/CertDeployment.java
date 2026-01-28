package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部署记录实体
 */
@Data
@TableName("cert_deployment")
public class CertDeployment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long certId;

    private Long serverId;

    private Integer deployType;

    private String targetId;

    private String targetName;

    private String config;

    private Integer status;

    private String errorMessage;

    private LocalDateTime deployTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
