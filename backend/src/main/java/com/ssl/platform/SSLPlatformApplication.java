package com.ssl.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SSL证书自动续期平台启动类
 */
@SpringBootApplication
@MapperScan("com.ssl.platform.mapper")
@EnableScheduling
@EnableAsync
public class SSLPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SSLPlatformApplication.class, args);
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     SSL证书自动续期平台启动成功                                  ║");
        System.out.println("║     访问地址: http://localhost:8080/api                         ║");
        System.out.println("║     API文档: http://localhost:8080/api/doc.html                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}
