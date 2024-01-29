package com.example.backend.common.exception.handler;

import com.example.backend.member.entity.CustomOAuth2User;
import com.example.backend.security.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        clearAuthenticationAttributes(request);
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String role = customOAuth2User.getAuthorities().stream().findFirst().get().toString();

        String accessToken = jwtUtil.createAccessToken(customOAuth2User.getUsername(), customOAuth2User.getName(), role);
        String refreshToken = jwtUtil.createRefreshToken(customOAuth2User.getUsername(), customOAuth2User.getName(), role);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                        .maxAge(1000 * 60L * 60 * 3)
                                .path("/")
                                        .secure(false)
                                                        .build();


        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(1000 * 60L * 60 * 5)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .build();



        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        response.sendRedirect("http://localhost:8080/auth/oauth-response");
        response.getWriter().flush();
    }

}
