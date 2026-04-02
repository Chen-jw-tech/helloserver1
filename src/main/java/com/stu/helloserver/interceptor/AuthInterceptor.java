package com.stu.helloserver.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        // 获取 HTTP 动词和路径
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 规则 A: POST /api/users - 注册放行
        boolean isCreateUser = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);

        // 规则 B: GET /api/users/{id} - 查看用户信息放行
        boolean isGetUser = "GET".equalsIgnoreCase(method) && uri.matches("^/api/users/\\d+$");

        // 满足放行条件，直接放行
        if (isCreateUser || isGetUser) {
            return true;
        }

        // 敏感操作：需要验证 Token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            String errorJson = "{\"code\": 401, \"msg\": \"登录凭证已缺失或过期，请重新登录\"}";
            response.getWriter().write(errorJson);
            return false;  // 拦截
        }

        return true;  // Token 有效，放行
    }
}