package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 当前套餐ID
     */
    private Long packageId;

    /**
     * 套餐到期时间
     */
    private LocalDateTime packageExpireTime;

    /**
     * 证书总数限制
     */
    private Integer totalCertCount;

    /**
     * 已使用证书数
     */
    private Integer usedCertCount;

    /**
     * 服务器总数限制
     */
    private Integer totalServerCount;

    /**
     * 已使用服务器数
     */
    private Integer usedServerCount;

    /**
     * 部署次数限制
     */
    private Integer totalDeployCount;

    /**
     * 已使用部署次数
     */
    private Integer usedDeployCount;

    /**
     * 监控数量限制
     */
    private Integer totalMonitorCount;

    /**
     * 已使用监控数
     */
    private Integer usedMonitorCount;

    /**
     * 微信OpenID
     */
    private String wechatOpenid;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

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
