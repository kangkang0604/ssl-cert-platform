package com.ssl.platform.constant;

/**
 * 常量类
 */
public class Constants {

    /**
     * JWT Token前缀
     */
    public static final String JWT_TOKEN_PREFIX = "jwt:token:";

    /**
     * 用户ID
     */
    public static final String JWT_USER_ID = "userId";

    /**
     * 用户名
     */
    public static final String JWT_USERNAME = "username";

    /**
     * 成功标识
     */
    public static final int SUCCESS = 1;

    /**
     * 失败标识
     */
    public static final int FAIL = 0;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    public static final int CAPTCHA_EXPIRATION = 5;

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 权限前缀
     */
    public static final String PERMISSION_PREFIX = "ROLE_";

    /**
     * 正常状态
     */
    public static final Integer STATUS_NORMAL = 1;

    /**
     * 禁用状态
     */
    public static final Integer STATUS_DISABLE = 0;

    /**
     * 证书状态：申请中
     */
    public static final Integer CERT_STATUS_APPLYING = 0;

    /**
     * 证书状态：已签发
     */
    public static final Integer CERT_STATUS_ISSUED = 1;

    /**
     * 证书状态：即将过期
     */
    public static final Integer CERT_STATUS_EXPIRING = 2;

    /**
     * 证书状态：已过期
     */
    public static final Integer CERT_STATUS_EXPIRED = 3;

    /**
     * 证书状态：已吊销
     */
    public static final Integer CERT_STATUS_REVOKED = 4;

    /**
     * 证书状态：申请失败
     */
    public static final Integer CERT_STATUS_FAILED = 5;

    /**
     * 证书类型：单域名
     */
    public static final Integer CERT_TYPE_SINGLE = 1;

    /**
     * 证书类型：多域名
     */
    public static final Integer CERT_TYPE_MULTI = 2;

    /**
     * 证书类型：通配符
     */
    public static final Integer CERT_TYPE_WILDCARD = 3;

    /**
     * 证书品牌：Let's Encrypt
     */
    public static final String CERT_BRAND_LETSENCRYPT = "letsencrypt";

    /**
     * 证书品牌：ZeroSSL
     */
    public static final String CERT_BRAND_ZEROSSL = "zerossl";

    /**
     * 证书品牌：Google
     */
    public static final String CERT_BRAND_GOOGLE = "google";

    /**
     * 部署状态：待部署
     */
    public static final Integer DEPLOY_STATUS_PENDING = 0;

    /**
     * 部署状态：部署中
     */
    public static final Integer DEPLOY_STATUS_DEPLOYING = 1;

    /**
     * 部署状态：部署成功
     */
    public static final Integer DEPLOY_STATUS_SUCCESS = 2;

    /**
     * 部署状态：部署失败
     */
    public static final Integer DEPLOY_STATUS_FAILED = 3;

    /**
     * 服务器类型：Nginx
     */
    public static final Integer SERVER_TYPE_NGINX = 1;

    /**
     * 服务器类型：Apache
     */
    public static final Integer SERVER_TYPE_APACHE = 2;

    /**
     * 服务器类型：IIS
     */
    public static final Integer SERVER_TYPE_IIS = 3;

    /**
     * 服务器类型：群晖
     */
    public static final Integer SERVER_TYPE_SYNOLOGY = 4;

    /**
     * 认证方式：密码
     */
    public static final Integer AUTH_TYPE_PASSWORD = 1;

    /**
     * 认证方式：SSH密钥
     */
    public static final Integer AUTH_TYPE_SSH_KEY = 2;
}
