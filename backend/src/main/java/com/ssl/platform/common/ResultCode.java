package com.ssl.platform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(1, "操作成功"),

    /**
     * 失败
     */
    FAIL(0, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未登录
     */
    NOT_LOGIN(401, "未登录或登录已过期"),

    /**
     * 无权限
     */
    NO_PERMISSION(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务器错误
     */
    SERVER_ERROR(500, "服务器错误"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1002, "用户已被禁用"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1003, "用户名已存在"),

    /**
     * 邮箱已被注册
     */
    EMAIL_EXISTS(1004, "邮箱已被注册"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(1005, "验证码错误"),

    /**
     * 验证码已过期
     */
    CAPTCHA_EXPIRED(1006, "验证码已过期"),

    /**
     * 套餐不存在
     */
    PACKAGE_NOT_FOUND(2001, "套餐不存在"),

    /**
     * 套餐已过期
     */
    PACKAGE_EXPIRED(2002, "套餐已过期"),

    /**
     * 超出套餐限制
     */
    EXCEED_PACKAGE_LIMIT(2003, "超出套餐限制"),

    /**
     * 证书不存在
     */
    CERT_NOT_FOUND(3001, "证书不存在"),

    /**
     * 证书申请失败
     */
    CERT_APPLY_FAILED(3002, "证书申请失败"),

    /**
     * 证书续期失败
     */
    CERT_RENEW_FAILED(3003, "证书续期失败"),

    /**
     * 域名不存在
     */
    DOMAIN_NOT_FOUND(4001, "域名不存在"),

    /**
     * 域名验证失败
     */
    DOMAIN_VERIFY_FAILED(4002, "域名验证失败"),

    /**
     * 服务器不存在
     */
    SERVER_NOT_FOUND(5001, "服务器不存在"),

    /**
     * 服务器连接失败
     */
    SERVER_CONNECT_FAILED(5002, "服务器连接失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}
