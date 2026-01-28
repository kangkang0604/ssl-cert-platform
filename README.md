# SSL证书自动续期平台

## 项目简介
本项目是一个 SSL 证书自动续期管理平台，支持证书申请、自动续期、自动部署到多种服务器和云服务。

## 技术栈

### 后端技术
- **语言**: Java 17
- **框架**: Spring Boot 3.2.x
- **数据库**: MySQL 8.0+ / PostgreSQL
- **ORM**: MyBatis-Plus
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **任务调度**: XXL-JOB

### 前端技术
- **框架**: Vue 3
- **UI库**: Element Plus
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **构建工具**: Vite

## 项目结构

```
ssl-cert-platform/
├── backend/                    # 后端项目
│   ├── src/main/java/com/ssl/
│   │   ├── SSLPlatformApplication.java
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── enums/              # 枚举类
│   │   ├── exception/          # 异常处理
│   │   ├── mapper/             # MyBatis Mapper
│   │   ├── security/           # 安全相关
│   │   ├── service/            # 服务层
│   │   └── util/               # 工具类
│   └── resources/
│       ├── application.yml
│       └── mapper/
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 组件
│   │   ├── layouts/           # 布局
│   │   ├── router/            # 路由
│   │   ├── stores/            # Pinia状态管理
│   │   ├── views/             # 页面
│   │   ├── App.vue
│   │   └── main.js
│   └── package.json
├── database/                   # 数据库脚本
│   └── init.sql
└── docs/                       # 文档
    └── 设计文档.md
```

## 快速开始

### 1. 数据库初始化
```bash
# 执行数据库脚本
mysql -u root -p < database/init.sql
```

### 2. 后端启动
```bash
cd backend
# 方式一：直接运行
mvn spring-boot:run

# 方式二：打包运行
mvn package
java -jar target/ssl-platform.jar
```

### 3. 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 4. 访问项目
- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080/api

## 核心功能

### 证书管理
- 证书申请（单域名、多域名、通配符）
- 证书列表管理
- 证书自动续期
- 证书详情查看

### 自动部署
- Nginx 部署
- Apache 部署
- IIS 部署
- 云服务部署（阿里云、腾讯云等）

### 监控告警
- 证书有效期监控
- 临期告警通知
- 部署状态监控

### 用户管理
- 用户注册登录
- 套餐管理
- 权限控制

## License
MIT License
