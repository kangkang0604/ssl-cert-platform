package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务器实体
 */
@Data
@TableName("cert_server")
public class CertServer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private Integer type;

    private String host;

    private Integer port;

    private String username;

    private Integer authType;

    private String password;

    private String privateKey;

    private String certPath;

    private String keyPath;

    private Integer status;

    private LocalDateTime lastHeartbeat;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
