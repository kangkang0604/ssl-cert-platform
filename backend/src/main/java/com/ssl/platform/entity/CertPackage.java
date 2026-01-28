package com.ssl.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 套餐实体
 */
@Data
@TableName("cert_package")
public class CertPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 套餐编码
     */
    private String code;

    /**
     * 价格
     */
    private java.math.BigDecimal price;

    /**
     * 年费价格
     */
    private java.math.BigDecimal priceYear;

    /**
     * 套餐描述
     */
    private String description;

    /**
     * 图标URL
     */
    private String icon;

    /**
     * 证书数量限制（0表示不限）
     */
    private Integer certLimit;

    /**
     * 部署次数限制（0表示不限）
     */
    private Integer deployLimit;

    /**
     * 服务器授权数量限制（0表示不限）
     */
    private Integer serverLimit;

    /**
     * 监控数量限制（0表示不限）
     */
    private Integer monitorLimit;

    /**
     * 通配符证书限制
     */
    private Integer wildcardCertLimit;

    /**
     * 是否支持OV证书：0-不支持，1-支持
     */
    private Integer ovCertEnable;

    /**
     * 是否支持EV证书：0-不支持，1-支持
     */
    private Integer evCertEnable;

    /**
     * 排序优先级
     */
    private Integer priority;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

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
}
