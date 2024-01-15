package com.example.backend.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception{

        http
                .httpBasic((httpBasic) ->
                        httpBasic.disable()
                )
                .csrf((auth) -> auth.disable())
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/register")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/login")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/checkUsername/**")).permitAll()
                        .anyRequest().hasAuthority("USER")
        )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session 말고 jwt 사용
                .formLogin((login) -> login.disable())
                ;

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
