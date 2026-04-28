package com.example.demo.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // This grabs the JWT from the current user's request
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null) {
                // This injects it into the Feign call to the Payment Service
                template.header("Authorization", authHeader);
            }
        }
    }
}

