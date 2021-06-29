package com.lulobank.credits.starter.config;

import com.lulobank.core.security.spring.WebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTGenerationSecurityConfig {

    @Bean
    public WebSecurity webSecurity() {
        return new WebSecurity();
    }
}