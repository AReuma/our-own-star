package com.example.backend.member.controller;

import com.example.backend.common.exception.dto.ErrorDTO;
import com.example.backend.member.dto.LoginRequestDTO;
import com.example.backend.member.dto.LoginResponseDTO;
import com.example.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "memberController", description = "회원 정보 및 인증을 관리하는 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "로그인", description = "로그인 API, 로그인 완료시 jwt 발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "비밀번호 틀림", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "회원이 없음", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("login");

        return memberService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
    }
}
