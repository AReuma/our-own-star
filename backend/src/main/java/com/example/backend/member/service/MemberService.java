package com.example.backend.member.service;

import com.example.backend.member.dto.EmailCertificationRequestDTO;
import com.example.backend.member.dto.LoginResponseDTO;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<LoginResponseDTO> login(String username, String password);

    ResponseEntity<String> register(String username, String password, String nickname, String phoneNum);

    ResponseEntity<Boolean> checkUsername(String username);

    ResponseEntity<String> emailCertification(EmailCertificationRequestDTO dto);

    ResponseEntity<Boolean> emailCertificationCode(String username, String code);
}
