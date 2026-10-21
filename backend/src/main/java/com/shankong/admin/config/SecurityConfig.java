package com.shankong.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置类, 配置 Spring Security 的安全规则
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    public static final String[] PUBLIC_PATHS = {
            "/",
            "/user/login",
            "/user/adminRegister",
            "/user/adminRegister/emailCode",
    };

    // 配置过滤器链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)  throws Exception {
        http
                // 关闭 CSRF 保护 (API服务通常不需要)
                .csrf(AbstractHttpConfigurer::disable)
                // 关闭会话管理(JWT不需要)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //配置请求规则
                .authorizeHttpRequests(auth ->
                        auth
                                // 公共路径放行
                                .requestMatchers(PUBLIC_PATHS).permitAll()
                                // 其他路径需要认证
                                .anyRequest().authenticated()
                );
        return http.build();
    }
}
