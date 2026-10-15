package com.shankong.admin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.shankong.admin.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil implements ApplicationContextAware {
    // 应用上下文
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // 初始化上下文
        JwtTokenUtil.applicationContext = applicationContext;
    }
    // 获取 JWT 配置
    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }

    // 生成token
    public static String generateToken(Long userId, String username, Integer userType) {
        try {
            // 获取jwt配置
            JwtConfig jwtConfig = getJwtConfig();
            // 生成签名算法
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            // 生成过期时间
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());
            return JWT.create()
                    .withExpiresAt(expiration) // 过期时间
                    .withIssuedAt(new Date()) // 签发时间
                    .withIssuer(jwtConfig.getIssuer()) // 签发者
                    .withClaim("id", userId) // 自定义声明，用户ID
                    .withClaim("username", username) // 自定义声明，用户名
                    .withClaim("userType", userType) // 自定义声明，用户类型
                    .sign(algorithm); // 签名
                   }
        catch (Exception e) {
            throw new RuntimeException("生成token失败: " + e.getMessage(), e);
        }
    }
}
