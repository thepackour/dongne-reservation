package com.dongne.reservation.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class RequestMetaFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute("timestamp", Instant.now().toString());
        request.setAttribute("request-id", UUID.randomUUID().toString());

        chain.doFilter(request, response);
    }
}
