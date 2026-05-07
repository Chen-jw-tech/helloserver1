package com.stu.helloserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 原 addInterceptors 方法已删除
    // 如果将来需要保留 CORS、资源映射等其他配置，可在这里补充
}