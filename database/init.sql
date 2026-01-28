-- =====================================================
-- SSL证书自动续期平台 - 数据库初始化脚本
-- Database: MySQL 8.0+
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ssl_cert_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ssl_cert_platform;

-- =====================================================
-- 1. 系统管理模块
-- =====================================================

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码（加密存储）',
  `email` varchar(128) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `package_id` bigint DEFAULT NULL COMMENT '当前套餐ID',
  `package_expire_time` datetime DEFAULT NULL COMMENT '套餐到期时间',
  `total_cert_count` int DEFAULT 0 COMMENT '证书总数限制',
  `used_cert_count` int DEFAULT 0 COMMENT '已使用证书数',
  `total_server_count` int DEFAULT 1 COMMENT '服务器总数限制',
  `used_server_count` int DEFAULT 0 COMMENT '已使用服务器数',
  `total_deploy_count` int DEFAULT 10 COMMENT '部署次数限制',
  `used_deploy_count` int DEFAULT 0 COMMENT '已使用部署次数',
  `total_monitor_count` int DEFAULT 5 COMMENT '监控数量限制',
  `used_monitor_count` int DEFAULT 0 COMMENT '已使用监控数',
  `wechat_openid` varchar(64) DEFAULT NULL COMMENT '微信OpenID',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(128) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_package_id` (`package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(64) NOT NULL COMMENT '角色名称',
  `code` varchar(64) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(64) NOT NULL COMMENT '权限名称',
  `code` varchar(128) NOT NULL COMMENT '权限编码',
  `type` tinyint NOT NULL COMMENT '类型：1-菜单，2-按钮',
  `parent_id` bigint DEFAULT 0 COMMENT '父级ID',
  `path` varchar(128) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(128) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(64) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_type` tinyint DEFAULT 1 COMMENT '配置类型：1-系统配置，2-业务配置',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(32) DEFAULT NULL COMMENT '操作类型',
  `method` varchar(128) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `ip` varchar(128) DEFAULT NULL COMMENT '请求IP',
  `user_agent` varchar(255) DEFAULT NULL COMMENT 'User-Agent',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_message` text COMMENT '错误信息',
  `cost_time` int DEFAULT 0 COMMENT '耗时（毫秒）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- =====================================================
-- 2. 证书管理模块
-- =====================================================

-- 套餐表
CREATE TABLE IF NOT EXISTS `cert_package` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `name` varchar(64) NOT NULL COMMENT '套餐名称',
  `code` varchar(32) NOT NULL COMMENT '套餐编码',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `price_year` decimal(10,2) DEFAULT NULL COMMENT '年费价格',
  `description` varchar(500) DEFAULT NULL COMMENT '套餐描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标URL',
  `cert_limit` int NOT NULL COMMENT '证书数量限制（0表示不限）',
  `deploy_limit` int NOT NULL COMMENT '部署次数限制（0表示不限）',
  `server_limit` int NOT NULL COMMENT '服务器授权数量限制（0表示不限）',
  `monitor_limit` int NOT NULL COMMENT '监控数量限制（0表示不限）',
  `wildcard_cert_limit` int DEFAULT 0 COMMENT '通配符证书限制',
  `ov_cert_enable` tinyint DEFAULT 0 COMMENT '是否支持OV证书：0-不支持，1-支持',
  `ev_cert_enable` tinyint DEFAULT 0 COMMENT '是否支持EV证书：0-不支持，1-支持',
  `priority` int DEFAULT 0 COMMENT '排序优先级',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- 订单表
CREATE TABLE IF NOT EXISTS `cert_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `package_id` bigint NOT NULL COMMENT '套餐ID',
  `pay_type` tinyint NOT NULL COMMENT '支付方式：1-支付宝，2-微信',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消，3-已退款',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime DEFAULT NULL COMMENT '套餐到期时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8=utf8mb4mb4 COLLATE_unicode_ci COMMENT='订单表';

-- 域名表
CREATE TABLE IF NOT EXISTS `cert_domain` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '域名ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `domain` varchar(128) NOT NULL COMMENT '域名',
  `domain_type` tinyint DEFAULT 1 COMMENT '域名类型：1-主域名，2-二级域名',
  `dns_provider` varchar(32) DEFAULT NULL COMMENT 'DNS服务商：aliyun、tencent、dnspod、cloudflare',
  `dns_account_id` varchar(128) DEFAULT NULL COMMENT 'DNS账户ID',
  `dns_txt_verification` varchar(255) DEFAULT NULL COMMENT 'DNS TXT验证记录',
  `verification_status` tinyint DEFAULT 0 COMMENT '验证状态：0-未验证，1-验证中，2-验证成功，3-验证失败',
  `verification_time` datetime DEFAULT NULL COMMENT '验证完成时间',
  `resolve_status` tinyint DEFAULT 1 COMMENT '解析状态：0-解析失败，1-解析正常',
  `resolve_ip` varchar(64) DEFAULT NULL COMMENT '解析IP',
  `caa_status` tinyint DEFAULT 1 COMMENT 'CAA记录状态：0-有冲突，1-正常',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_domain` (`domain`),
  KEY `idx_verification_status` (`verification_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='域名表';

-- 证书表
CREATE TABLE IF NOT EXISTS `cert_certificate` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '证书ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `domain_id` bigint DEFAULT NULL COMMENT '关联域名ID',
  `cert_type` tinyint NOT NULL COMMENT '证书类型：1-单域名，2-多域名，3-通配符',
  `brand` varchar(32) NOT NULL COMMENT '证书品牌：letsencrypt、zerossl、google',
  `domain_name` varchar(500) NOT NULL COMMENT '域名（多个用逗号分隔）',
  `subject` varchar(255) DEFAULT NULL COMMENT '证书主题',
  `issuer` varchar(255) DEFAULT NULL COMMENT '证书颁发者',
  `serial_number` varchar(128) DEFAULT NULL COMMENT '证书序列号',
  `cert_dn` text COMMENT '证书DN',
  `cert_pem` text COMMENT '证书内容（PEM格式）',
  `private_key` text COMMENT '私钥内容',
  `not_before` datetime DEFAULT NULL COMMENT '生效时间',
  `not_after` datetime DEFAULT NULL COMMENT '过期时间',
  `days_remaining` int DEFAULT NULL COMMENT '剩余天数',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '证书状态：0-申请中，1-已签发，2-即将过期，3-已过期，4-已吊销，5-申请失败',
  `auto_renew` tinyint NOT NULL DEFAULT 1 COMMENT '是否自动续期：0-否，1-是',
  `renew_count` int DEFAULT 0 COMMENT '续期次数',
  `last_renew_time` datetime DEFAULT NULL COMMENT '最后续期时间',
  `next_renew_time` datetime DEFAULT NULL COMMENT '下次续期时间',
  `error_message` text COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_domain_id` (`domain_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_time` (`not_after`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书表';

-- 服务器表
CREATE TABLE IF NOT EXISTS `cert_server` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务器ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(64) NOT NULL COMMENT '服务器名称',
  `type` tinyint NOT NULL COMMENT '服务器类型：1-Nginx，2-Apache，3-IIS，4-群晖，5-其他',
  `host` varchar(128) NOT NULL COMMENT '主机地址',
  `port` int DEFAULT 22 COMMENT 'SSH端口',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `auth_type` tinyint DEFAULT 1 COMMENT '认证方式：1-密码，2-SSH密钥',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密存储）',
  `private_key` text COMMENT 'SSH私钥',
  `cert_path` varchar(255) DEFAULT NULL COMMENT '证书存放路径',
  `key_path` varchar(255) DEFAULT NULL COMMENT '私钥存放路径',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-离线，1-在线',
  `last_heartbeat` datetime DEFAULT NULL COMMENT '最后心跳时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务器表';

-- 部署记录表
CREATE TABLE IF NOT EXISTS `cert_deployment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部署ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `cert_id` bigint NOT NULL COMMENT '证书ID',
  `server_id` bigint DEFAULT NULL COMMENT '服务器ID',
  `deploy_type` tinyint NOT NULL COMMENT '部署类型：1-服务器，2-CDN，3-COS，4-LB，5-WebHook',
  `target_id` varchar(128) DEFAULT NULL COMMENT '部署目标ID（如CDN域名、COS Bucket等）',
  `target_name` varchar(128) DEFAULT NULL COMMENT '部署目标名称',
  `config` text COMMENT '部署配置',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '部署状态：0-待部署，1-部署中，2-部署成功，3-部署失败',
  `error_message` text COMMENT '错误信息',
  `deploy_time` datetime DEFAULT NULL COMMENT '部署时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cert_id` (`cert_id`),
  KEY `idx_server_id` (`server_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部署记录表';

-- 监控记录表
CREATE TABLE IF NOT EXISTS `cert_monitor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '监控ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `cert_id` bigint DEFAULT NULL COMMENT '证书ID',
  `domain` varchar(128) NOT NULL COMMENT '监控域名',
  `check_interval` int DEFAULT 3600 COMMENT '检查间隔（秒），默认1小时',
  `warning_days` int DEFAULT 30 COMMENT '预警天数，默认30天',
  `alert_channels` varchar(255) DEFAULT NULL COMMENT '告警渠道：email,wechat,sms（逗号分隔）',
  `alert_email` varchar(128) DEFAULT NULL COMMENT '告警邮箱',
  `alert_wechat` varchar(64) DEFAULT NULL COMMENT '告警微信',
  `last_check_time` datetime DEFAULT NULL COMMENT '最后检查时间',
  `last_status` tinyint DEFAULT NULL COMMENT '最后状态：1-正常，2-警告，3-危险',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '监控状态：0-停用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cert_id` (`cert_id`),
  KEY `idx_domain` (`domain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb COMMENT='监控记录4_unicode_ci表';

-- 通知记录表
CREATE TABLE IF NOT EXISTS `cert_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `monitor_id` bigint DEFAULT NULL COMMENT '监控ID',
  `cert_id` bigint DEFAULT NULL COMMENT '证书ID',
  `type` tinyint NOT NULL COMMENT '通知类型：1-证书即将过期，2-证书已过期，3-部署成功，4-部署失败，5-续期成功，6-续期失败',
  `channel` varchar(32) NOT NULL COMMENT '通知渠道：email,wechat,sms',
  `title` varchar(128) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `error_message` text COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_monitor_id` (`monitor_id`),
  KEY `idx_cert_id` (`cert_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

-- =====================================================
-- 3. 初始化数据
-- =====================================================

-- 初始化套餐数据
INSERT INTO `cert_package` (`id`, `name`, `code`, `price`, `price_year`, `description`, `cert_limit`, `deploy_limit`, `server_limit`, `monitor_limit`, `wildcard_cert_limit`, `ov_cert_enable`, `ev_cert_enable`, `priority`, `status`) VALUES
(1, '免费版', 'free', 0.00, 0.00, '想体验SSL自动化，测试环境使用', 1, 10, 1, 5, 1, 0, 0, 100, 1),
(2, '个人版', 'personal', 69.00, 69.00, '独立开发者、个人站长、小项目运营者', 5, 200, 3, 30, 5, 0, 0, 90, 1),
(3, '企业基础版', 'enterprise', 199.00, 199.00, '中小企业、正式项目运营者', 30, 2000, 10, 200, 30, 1, 0, 80, 1),
(4, '高级版', 'professional', 599.00, 599.00, '企业、站长、大型项目负责人', 200, 8000, 50, 500, 200, 1, 1, 70, 1);

-- 初始化管理员密码（密码：admin123）
-- BCrypt加密后的密码
INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `status`, `package_id`, `package_expire_time`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@ssl-platform.com', 1, 4, '2030-12-31 23:59:59');

-- 初始化角色
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`) VALUES
(1, '超级管理员', 'super_admin', '拥有所有权限'),
(2, '普通用户', 'user', '普通用户权限'),
(3, '游客', 'guest', '只读权限');

-- 初始化管理员角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 初始化权限
INSERT INTO `sys_permission` (`id`, `name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`, `status`) VALUES
(1, '系统管理', 'system', 1, 0, '/system', 'Layout', 'setting', 100, 1),
(2, '用户管理', 'system:user', 1, 1, '/system/user', 'system/user/index', 'user', 1, 1),
(3, '用户列表', 'system:user:list', 2, 2, NULL, NULL, NULL, 0, 1),
(4, '用户新增', 'system:user:add', 2, 2, NULL, NULL, NULL, 0, 1),
(5, '用户编辑', 'system:user:edit', 2, 2, NULL, NULL, NULL, 0, 1),
(6, '用户删除', 'system:user:delete', 2, 2, NULL, NULL, NULL, 0, 1),
(7, '角色管理', 'system:role', 1, 1, '/system/role', 'system/role/index', 'peoples', 2, 1),
(8, '角色列表', 'system:role:list', 2, 7, NULL, NULL, NULL, 0, 1),
(9, '角色新增', 'system:role:add', 2, 7, NULL, NULL, NULL, 0, 1),
(10, '角色编辑', 'system:role:edit', 2, 7, NULL, NULL, NULL, 0, 1),
(11, '角色删除', 'system:role:delete', 2, 7, NULL, NULL, NULL, 0, 1),
(12, '权限管理', 'system:permission', 1, 1, '/system/permission', 'system/permission/index', 'tree-table', 3, 1),
(13, '权限列表', 'system:permission:list', 2, 12, NULL, NULL, NULL, 0, 1),
(14, '权限新增', 'system:permission:add', 2, 12, NULL, NULL, NULL, 0, 1),
(15, '权限编辑', 'system:permission:edit', 2, 12, NULL, NULL, NULL, 0, 1),
(16, '权限删除', 'system:permission:delete', 2, 12, NULL, NULL, NULL, 0, 1),
(17, '证书管理', 'certificate', 1, 0, '/certificate', 'Layout', 'certificate', 10, 1),
(18, '证书列表', 'certificate:list', 1, 17, '/certificate/list', 'certificate/list/index', 'list', 1, 1),
(19, '证书申请', 'certificate:apply', 1, 17, '/certificate/apply', 'certificate/apply/index', 'add', 2, 1),
(20, '证书详情', 'certificate:detail', 2, 17, NULL, NULL, NULL, 0, 1),
(21, '证书删除', 'certificate:delete', 2, 17, NULL, NULL, NULL, 0, 1),
(22, '证书部署', 'certificate:deploy', 2, 17, NULL, NULL, NULL, 0, 1),
(23, '域名管理', 'domain', 1, 0, '/domain', 'Layout', 'domain', 20, 1),
(24, '域名列表', 'domain:list', 1, 23, '/domain/list', 'domain/list/index', 'list', 1, 1),
(25, '域名新增', 'domain:add', 2, 23, NULL, NULL, NULL, 0, 1),
(26, '域名删除', 'domain:delete', 2, 23, NULL, NULL, NULL, 0, 1),
(27, '服务器管理', 'server', 1, 0, '/server', 'Layout', 'server', 30, 1),
(28, '服务器列表', 'server:list', 1, 27, '/server/list', 'server/list/index', 'list', 1, 1),
(29, '服务器新增', 'server:add', 2, 27, NULL, NULL, NULL, 0, 1),
(30, '服务器编辑', 'server:edit', 2, 27, NULL, NULL, NULL, 0, 1),
(31, '服务器删除', 'server:delete', 2, 27, NULL, NULL, NULL, 0, 1),
(32, '部署管理', 'deployment', 1, 0, '/deployment', 'Layout', 'deploy', 40, 1),
(33, '部署列表', 'deployment:list', 1, 32, '/deployment/list', 'deployment/list/index', 'list', 1, 1),
(34, '监控管理', 'monitor', 1, 0, '/monitor', 'Layout', 'monitor', 50, 1),
(35, '监控列表', 'monitor:list', 1, 34, '/monitor/list', 'monitor/list/index', 'list', 1, 1),
(36, '监控新增', 'monitor:add', 2, 34, NULL, NULL, NULL, 0, 1),
(37, '监控编辑', 'monitor:edit', 2, 34, NULL, NULL, NULL, 0, 1),
(38, '监控删除', 'monitor:delete', 2, 34, NULL, NULL, NULL, 0, 1);

-- 给超级管理员分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `sys_permission`;

-- 给普通用户分配基础权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 17), (2, 18), (2, 19), (2, 20), (2, 22),
(2, 23), (2, 24), (2, 25),
(2, 27), (2, 28), (2, 29), (2, 30),
(2, 32), (2, 33),
(2, 34), (2, 35), (2, 36), (2, 37);

-- 初始化系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('sys.site.name', 'SSL证书管理平台', 1, '网站名称'),
('sys.site.logo', '/logo.png', 1, '网站Logo'),
('sys.site.copyright', 'Copyright © 2026 SSL证书管理平台', 1, '版权信息'),
('sys.cert.letsencrypt.api.url', 'https://acme-v02.api.letsencrypt.org/directory', 2, 'Let's Encrypt API地址'),
('sys.cert.zerossl.api.url', 'https://api.zerossl.com/acme', 2, 'ZeroSSL API地址'),
('sys.cert.renew.days.before', '30', 2, '证书续期提前天数'),
('sys.cert.check.interval', '3600', 2, '证书检查间隔（秒）'),
('sys.scheduler.enabled', 'true', 2, '是否启用定时任务'),
('sys.jwt.secret', 'ssl-cert-platform-jwt-secret-key-2024', 1, 'JWT密钥'),
('sys.jwt.expiration', '86400', 1, 'JWT过期时间（秒）');

-- =====================================================
-- 4. 创建视图
-- =====================================================

-- 用户套餐信息视图
CREATE VIEW `v_user_package` AS
SELECT 
    u.`id` AS user_id,
    u.`username`,
    u.`email`,
    u.`package_id`,
    p.`name` AS package_name,
    p.`code` AS package_code,
    p.`cert_limit`,
    p.`deploy_limit`,
    p.`server_limit`,
    p.`monitor_limit`,
    p.`wildcard_cert_limit`,
    u.`package_expire_time`,
    u.`total_cert_count`,
    u.`used_cert_count`,
    u.`total_server_count`,
    u.`used_server_count`,
    u.`total_deploy_count`,
    u.`used_deploy_count`,
    u.`total_monitor_count`,
    u.`used_monitor_count`
FROM `sys_user` u
LEFT JOIN `cert_package` p ON u.`package_id` = p.`id`;

-- 证书统计视图
CREATE VIEW `v_cert_statistics` AS
SELECT 
    `user_id`,
    `status`,
    COUNT(*) AS count
FROM `cert_certificate`
WHERE `deleted` = 0
GROUP BY `user_id`, `status`;

-- =====================================================
-- 5. 创建存储过程
-- =====================================================

-- 自动更新证书剩余天数
DELIMITER $
CREATE PROCEDURE `proc_update_cert_days_remaining`()
BEGIN
    UPDATE `cert_certificate` 
    SET `days_remaining` = DATEDIFF(`not_after`, NOW()),
        `next_renew_time` = DATE_SUB(`not_after`, INTERVAL 30 DAY)
    WHERE `deleted` = 0 
      AND `status` = 1
      AND `not_after` IS NOT NULL;
END $
DELIMITER ;

-- =====================================================
-- 6. 创建定时任务
-- =====================================================

-- 创建事件：每小时更新证书剩余天数
CREATE EVENT `event_update_cert_days`
ON SCHEDULE EVERY 1 HOUR
DO CALL proc_update_cert_days_remaining();

-- 开启事件调度器
SET GLOBAL event_scheduler = ON;
