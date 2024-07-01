package com.example.backend.common.chat;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.member.dto.LoginInfoDto;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.security.jwt.JWTUtil;
import com.example.backend.security.jwt.token.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class JwtTokenValidationInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("ChatPreHandler");

        String token = "";
        try {
            StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            String authorizationHeader = String.valueOf(headerAccessor.getNativeHeader("Authorization"));

            //System.out.println("authrization: " + authorizationHeader);

            StompCommand command = headerAccessor.getCommand();
            if (command.equals(StompCommand.ERROR)) {
                throw new MessageDeliveryException("요청 처리 중 오류가 발생했습니다.");
            } else if(command.equals(StompCommand.DISCONNECT)){
                log.info("DISCONNECT command received. Closing the connection.");
                return message;
            }else {
                String authorizationHeaderStr = authorizationHeader.replace("[","").replace("]","");
                if (authorizationHeaderStr.startsWith("Bearer ")) {
                    token = authorizationHeaderStr.replace("Bearer ", "");
                } else {
                    log.error("토큰을 찾을 수 없습니다. : {}", authorizationHeader);
                    throw new MalformedJwtException("not found token exception");
                }

                JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(token);

                Claims claims = jwtUtil.parseAccessToken(jwtAuthenticationToken.getToken());

                String username = claims.get("username", String.class);

                Member member = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다."));

                List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

                SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(authorities, new LoginInfoDto(member.getUsername(), member.getNickname()), null));

                return message;
            }

        }catch (NullPointerException | IllegalStateException e) {
            log.error("Not found Token // token : {}", token);
            throw new BadCredentialsException("throw new not found token exception");
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid Token // token : {}", token);
            throw new BadCredentialsException("throw new invalid token exception");
        } catch (ExpiredJwtException e) {
            log.error("EXPIRED Token // token : {}", token);
            throw new BadCredentialsException("throw new expired token exception");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported Token // token : {}", token);
            throw new BadCredentialsException("throw new unsupported token exception");
        } catch (Exception e) {
            log.error("====================================================");
            log.error("ChatPreHandler - JWT 인증 오류 발생");
            log.error("token : {}", token);
            throw new BadCredentialsException("throw new exception");
        }

    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims){
        String rolesObject = claims.get("role", String.class);

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(() -> rolesObject);

        return authorities;
    }
}