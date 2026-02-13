package com.dongne.reservation.exception;

import com.dongne.reservation.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    public AuthFailureHandler(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ApiResponse<?> apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "아이디 또는 비밀번호가 올바르지 않습니다.");

        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }
}
