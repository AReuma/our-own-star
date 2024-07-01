package com.example.backend.security.jwt.provider;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.member.dto.LoginInfoDto;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.security.jwt.JWTUtil;
import com.example.backend.security.jwt.token.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("JwtAuthenticationProvider - authenticate()");

        // token 검사
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;

        Claims claims = jwtUtil.parseAccessToken(authenticationToken.getToken());

        String username = claims.get("username", String.class);
        String nickname = claims.get("nickname", String.class);

        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        return new JwtAuthenticationToken(authorities, new LoginInfoDto(member.getUsername(), member.getNickname()), null);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims){
        String rolesObject = claims.get("role", String.class);

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(() -> rolesObject);
        /*if (rolesObject instanceof String) {
            // "role" 클레임이 단일 값인 경우
            authorities.add(() -> (String) rolesObject);
        } else if (rolesObject instanceof List) {
            // "role" 클레임이 리스트인 경우
            List<String> roles = (List<String>) rolesObject;
            authorities.add(() -> roles.get(0));
        }*/

        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //AuthenticationProvider가 어떤 종류의 인증 토큰을 처리할지 결정
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
