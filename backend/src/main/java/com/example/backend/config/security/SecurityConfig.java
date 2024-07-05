package com.example.backend.config.security;

import com.example.backend.common.exception.handler.OAuth2SuccessHandler;
import com.example.backend.common.exception.jwt.CustomAuthenticationEntryPoint;
import com.example.backend.config.authentication.AuthenticationManagerConfig;
import com.example.backend.member.service.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
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

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

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
                        //.requestMatchers(new MvcRequestMatcher(introspector, "/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/email-certification")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/email-certification/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/chat-websocket/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/login")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector,"/api/v1/auth/oauth2/naver")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/auth/oauth2/google")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/auth/oauth2/kakao")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/oauth2/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/register")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/checkUsername/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/idol/page/*")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/idol/getTotalPage")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/idol/find/beforeLogin/*")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/v3/api-docs/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/v3/api-docs")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/swagger-ui/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/image/**")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/v1/users/refreshToken")).permitAll()
                        //.anyRequest().hasAuthority("USER")
                        //.anyRequest().authenticated()
                        .anyRequest().hasRole("USER")
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/api/v1/auth/oauth2"))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session 말고 jwt 사용
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint))
                .apply(authenticationManagerConfig);
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedHeader(HttpHeaders.AUTHORIZATION); // 클라이언트에게 Authorization 헤더 허용
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
