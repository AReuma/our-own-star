package com.example.backend.config.encode;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class EncoderConfig {

    @Bean
    public BCryptPasswordEncoder encoder(){
        // Spring Security 에 포함된 클래스
        return new BCryptPasswordEncoder();
    }
}
