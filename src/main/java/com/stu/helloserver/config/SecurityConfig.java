package com.stu.helloserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启 CORS（使用下面定义的 CorsConfigurationSource）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. 关闭 CSRF（前后端分离项目一般关闭）
                .csrf(csrf -> csrf.disable())

                // 3. 无状态会话（不创建 HttpSession）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. 接口权限规则
                .authorizeHttpRequests(auth -> auth
                        // 放行注册和登录
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        // 其余所有接口都必须认证
                        .anyRequest().authenticated()
                )

                // 5. 关闭表单登录和 HTTP Basic（纯前后端分离，不弹窗）
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * CORS 配置源，允许跨域请求
     * 开发阶段可暂时允许所有来源，上线后请限制为实际前端地址
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));  // 允许所有来源
        configuration.setAllowedMethods(List.of("*"));         // 允许所有 HTTP 方法
        configuration.setAllowedHeaders(List.of("*"));         // 允许所有请求头
        configuration.setAllowCredentials(true);               // 允许携带凭证（如 Cookie）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}