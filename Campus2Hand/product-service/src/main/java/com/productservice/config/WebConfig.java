package com.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     * 让前端可以通过 /avatars/xxx.png 访问上传的头像文件
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问路径：/uploads/products/8.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/Idea/4685/uploads/");
    }

    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
