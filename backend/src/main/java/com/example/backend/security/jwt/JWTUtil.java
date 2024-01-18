package com.example.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTUtil {

    private SecretKey accessTokenSecretKey;
    private SecretKey refreshTokenSecretKey;
    private final Long accessExpireTimeMs = 1000 * 60l * 20; // 5분
    private final Long refreshExpireTimeMs = 1000 * 60l * 30; // 10분

    public JWTUtil(@Value("${jwt.accessToken.secretKey}")String accessSecret, @Value("${jwt.refreshToken.secretKey}")String refreshSecret){
        accessTokenSecretKey = new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        refreshTokenSecretKey = new SecretKeySpec(refreshSecret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(String username, String nickname, String role, SecretKey secretKey, Long expireTimeMs){
        // token에 포함될 내용
        // 닉네임, 권한
        return Jwts.builder()
                .claim("username", username)
                .claim("nickname", nickname)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * accessToken 생성 메서드
     * */
    public String createAccessToken(String username, String nickname, String role){
        return createToken(username, nickname, role, accessTokenSecretKey, accessExpireTimeMs);
    }

    /**
     * refreshToken 생성 메서드
     * */
    public String createRefreshToken(String username, String nickname, String role){
        return createToken(username, nickname, role, refreshTokenSecretKey, refreshExpireTimeMs);
    }

    public Claims parseToken(String token, SecretKey secretKey){
        return  Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public Claims parseAccessToken(String token){
        return parseToken(token, accessTokenSecretKey);
    }

    public Claims parseRefreshToken(String token){
        return parseToken(token, refreshTokenSecretKey);
    }

    /**
     * 토큰 만료일 체크 메서드
     * */
    public boolean isExpired(String token, SecretKey secretKey){
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        }catch (ExpiredJwtException exception) {
            log.error("token 만료일이 지났습니다");
            return true;
        }
    }

    public boolean accessTokenExpired(String token){
        return isExpired(token, accessTokenSecretKey);
    }

    public boolean refreshTokenExpired(String token){
        return isExpired(token, refreshTokenSecretKey);
    }

    /**
     * 토큰에 포함된 username 가져오기
     */
    public String getUsername(String token){
        return Jwts.parser().verifyWith(accessTokenSecretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    /**
     * 토큰에 포함된 nickname 가져오기
     */
    public String getNickname(String token){
        return Jwts.parser().verifyWith(accessTokenSecretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
    }

    /**
     * 토큰에 포함된 role 가져오기
     */
    public String getRole(String token){
        return Jwts.parser().verifyWith(accessTokenSecretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
}
