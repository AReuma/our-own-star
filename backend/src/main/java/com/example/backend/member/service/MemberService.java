package com.example.backend.member.service;

import com.example.backend.member.dto.LoginResponseDTO;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<LoginResponseDTO> login(String username, String password);

    ResponseEntity<String> register(String username, String password, String nickname, String phoneNum);

    ResponseEntity<Boolean> checkUsername(String username);
}
