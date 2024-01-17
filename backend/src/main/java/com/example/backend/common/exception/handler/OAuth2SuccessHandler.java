package com.example.backend.common.exception.handler;

import com.example.backend.member.entity.CustomOAuth2User;
import com.example.backend.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String role = customOAuth2User.getAuthorities().stream().findFirst().get().toString();

        String accessToken = jwtUtil.createAccessToken(customOAuth2User.getUsername(), customOAuth2User.getName(), role);
        String refreshToken = jwtUtil.createRefreshToken(customOAuth2User.getUsername(), customOAuth2User.getName(), role);

        response.sendRedirect("http://localhost:8080/auth/oauth-response?accessToken=" + accessToken+"&refreshToken="+refreshToken);
        response.getWriter().flush();
    }

}
