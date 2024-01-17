package com.example.backend.member.service;

import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.member.dto.EmailCertificationRequestDTO;
import com.example.backend.member.dto.EmailCertificationResponseDTO;
import com.example.backend.member.dto.LoginResponseDTO;
import com.example.backend.member.entity.Auth;
import com.example.backend.member.entity.Member;
import com.example.backend.member.entity.SocialType;
import com.example.backend.member.repository.MemberRedisRepository;
import com.example.backend.member.repository.MemberRepository;
import com.example.backend.security.jwt.JWTUtil;
import com.example.backend.security.jwt.email.provider.EmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final EmailProvider emailProvider;
    private final MemberRedisRepository memberRedisRepository;

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

    @Transactional
    @Override
    public ResponseEntity<String> register(String username, String password, String nickname, String phoneNum) {

        memberRepository.findByUsername(username)
                .ifPresent(member -> { throw new AppException(ErrorCode.USER_DUPLICATED, "중복된 아이디입니다.");});
        memberRepository.findByPhoneNum(phoneNum)
                .ifPresent(member -> { throw new AppException(ErrorCode.USER_DUPLICATED, "이미 사용한 전화번호입니다.");});

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .phoneNum(phoneNum)
                .socialType(SocialType.DEFAULT)
                .role(Auth.ROLE_USER)
                .build();

        memberRepository.save(member);
        return ResponseEntity.ok().body("회원가입 성공");
    }

    @Override
    public ResponseEntity<Boolean> checkUsername(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(member -> {
                    throw new AppException(ErrorCode.USER_DUPLICATED, "사용 불가능한 아이디입니다.");
                });

        return ResponseEntity.ok().body(Boolean.TRUE);
    }

    @Override
    public ResponseEntity<String> emailCertification(EmailCertificationRequestDTO dto) {

        String username = dto.getUsername();

        boolean isExistId = memberRepository.existsByUsername(username);
        if (isExistId) throw new AppException(ErrorCode.USER_DUPLICATED, "사용 불가능한 아이디입니다.");

        // 인증코드
        String certificationNumber = getCertificationNumber();

        boolean isSuccessed = emailProvider.sendCertificationMail(username, certificationNumber);
        if(!isSuccessed) throw new AppException(ErrorCode.EMAIL_SEND_FAIL, "email send fail");

        memberRedisRepository.save(new EmailCertificationResponseDTO(username, certificationNumber));
        return ResponseEntity.ok().body("성공");
    }

    @Override
    public ResponseEntity<Boolean> emailCertificationCode(String username, String code) {

        EmailCertificationResponseDTO findEmailCertificationCode = memberRedisRepository.findById(username).orElseThrow(() -> new AppException(ErrorCode.CACHE_DATA_NOT_EXISTED, "Cache Data is not existed"));

        log.info("findEmailCertificationCode: {}", findEmailCertificationCode.getCode());

        if (findEmailCertificationCode.getCode().equals(code)){
            return ResponseEntity.ok().body(Boolean.TRUE);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Boolean.FALSE);
    }

    public String getCertificationNumber(){
        StringBuilder certificationNumber = new StringBuilder();

        for (int count = 0; count < 4; count++){
            certificationNumber.append((int) (Math.random() * 10));
        }

        return certificationNumber.toString();
    }
}
