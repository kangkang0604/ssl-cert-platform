package com.ssl.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ssl.platform.dto.LoginRequest;
import com.ssl.platform.dto.LoginResponse;
import com.ssl.platform.dto.RegisterRequest;
import com.ssl.platform.entity.SysUser;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册用户
     */
    SysUser register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    SysUser getByUsername(String username);

    /**
     * 更新用户最后登录信息
     *
     * @param userId 用户ID
     * @param ip 登录IP
     */
    void updateLoginInfo(Long userId, String ip);

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}
