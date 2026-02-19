package com.dongne.reservation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // CORS를 허용할 API 경로
                .allowedOrigins("http://localhost:8080", "https://localhost:8080")  // Swagger UI가 실행 중인 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")  // 허용할 HTTP 메소드
                .allowedHeaders("*")  // 허용할 HTTP 헤더
                .allowCredentials(true);  // 자격 증명 허용 (옵션)
    }
}