package com.ssl.platform.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssl.platform.dto.LoginRequest;
import com.ssl.platform.dto.LoginResponse;
import com.ssl.platform.dto.RegisterRequest;
import com.ssl.platform.entity.SysUser;
import com.ssl.platform.exception.BusinessException;
import com.ssl.platform.mapper.SysUserMapper;
import com.ssl.platform.security.JwtTokenUtil;
import com.ssl.platform.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration:86400}")
    private int jwtExpiration;

    @Value("${jwt.secret:ssl-cert-platform-jwt-secret-key-2024}")
    private String jwtSecret;

    @Override
    public SysUser register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus(1); // 正常状态
        user.setPackageId(1L); // 默认免费版
        user.setPackageExpireTime(LocalDateTime.now().plusYears(1)); // 一年有效期
        user.setTotalCertCount(1);
        user.setUsedCertCount(0);
        user.setTotalServerCount(1);
        user.setUsedServerCount(0);
        user.setTotalDeployCount(10);
        user.setUsedDeployCount(0);
        user.setTotalMonitorCount(5);
        user.setUsedMonitorCount(0);

        sysUserMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());

        return user;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 根据用户名获取用户
        SysUser user = getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成JWT Token
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());

        // 保存Token到Redis
        String redisKey = "jwt:token:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, jwtExpiration, TimeUnit.SECONDS);

        // 更新登录信息
        updateLoginInfo(user.getId(), request.getIp());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());

        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        wrapper.eq(SysUser::getDeleted, 0);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public void updateLoginInfo(Long userId, String ip) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        sysUserMapper.updateById(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        wrapper.eq(SysUser::getDeleted, 0);
        return sysUserMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        wrapper.eq(SysUser::getDeleted, 0);
        return sysUserMapper.selectCount(wrapper) > 0;
    }
}
