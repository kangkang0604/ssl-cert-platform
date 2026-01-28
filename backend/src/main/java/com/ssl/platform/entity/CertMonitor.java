package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 监控记录实体
 */
@Data
@TableName("cert_monitor")
public class CertMonitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long certId;

    private String domain;

    private Integer checkInterval;

    private Integer warningDays;

    private String alertChannels;

    private String alertEmail;

    private String alertWechat;

    private LocalDateTime lastCheckTime;

    private Integer lastStatus;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
