package com.example.hellotalk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TrailingSlashInterceptor());
    }
}

class TrailingSlashInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        if (!requestUri.endsWith("/") && !hasExtension(requestUri)) {
            response.sendRedirect(requestUri + "/");
            return false;
        }
        return true;
    }

    private boolean hasExtension(String uri) {
        int lastDotIndex = uri.lastIndexOf(".");
        int lastSlashIndex = uri.lastIndexOf("/");
        return lastDotIndex > lastSlashIndex;
    }
}

