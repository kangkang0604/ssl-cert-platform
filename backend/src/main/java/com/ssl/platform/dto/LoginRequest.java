package com.ssl.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", required = true, example = "admin")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "admin123")
    private String password;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    private String captcha;

    /**
     * 验证码UUID
     */
    @Schema(description = "验证码UUID")
    private String captchaUuid;

    /**
     * 登录IP
     */
    @Schema(description = "登录IP")
    private String ip;
}
