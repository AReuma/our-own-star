package com.example.backend.member.service;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.member.dto.LoginResponseDTO;
import com.example.backend.member.entity.Member;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public ResponseEntity<LoginResponseDTO> login(String username, String password) {
        Member findMember = memberRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER_ID, "회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, findMember.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "잘못된 비밀번호입니다.");
        }

        String accessToken = jwtUtil.createAccessToken(findMember.getUsername(), findMember.getNickname(), findMember.getRole().toString());
        String refreshToken = jwtUtil.createRefreshToken(findMember.getUsername(), findMember.getNickname(), findMember.getRole().toString());

        return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken));
    }
}
