package com.stu.helloserver.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu.helloserver.Entity.User;
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 读取请求头中的 Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. 如果没有或不以 Bearer 开头，直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 截取 JWT 字符串
        String jwt = authHeader.substring(7);
        String username;

        try {
            // 4. 从 JWT 中解析用户名
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            // 解析失败（过期、签名错误等），直接放行（后续会被 Spring Security 拦截返回 403）
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 如果解析到用户名且当前 SecurityContext 中无认证信息，则验证用户并设置认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从数据库查询用户（可根据实际业务决定是否需要每次查库）
            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, username)
            );

            if (user != null) {
                // 构造认证令牌（这里未设置权限，实际项目可添加角色）
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,           // principal
                                null,           // credentials
                                Collections.emptyList()  // authorities
                        );
                // 设置为已认证
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
}