package com.example.backend.member.controller;

import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.market.MarketPostListResponseDTO;
import com.example.backend.common.exception.dto.ErrorDTO;
import com.example.backend.config.swagger.DisableSwaggerSecurity;
import com.example.backend.member.dto.*;
import com.example.backend.member.dto.mypage.UserInfoCommentResponse;
import com.example.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @DisableSwaggerSecurity
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        log.info("login");

        return memberService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
    }

    @DisableSwaggerSecurity
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO  registerRequestDTO){
        log.info("register");
        return memberService.register(registerRequestDTO.getUsername(), registerRequestDTO.getPassword(),
                registerRequestDTO.getNickname(), registerRequestDTO.getPhoneNum());
    }

    @DisableSwaggerSecurity
    @GetMapping("/checkUsername/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable @NotBlank String username){
        log.info("checkUsername");
        return memberService.checkUsername(username);
    }

    @DisableSwaggerSecurity
    @PostMapping("/email-certification")
    public ResponseEntity<String> emailCertification(@RequestBody @Valid EmailCertificationRequestDTO emailCertificationRequestDTO){
        log.info("emailCertification");
        return memberService.emailCertification(emailCertificationRequestDTO);
    }

    @DisableSwaggerSecurity
    @PostMapping("/email-certification/code")
    public ResponseEntity<Boolean> emailCertificationCode(@RequestBody @Valid EmailCertificationCodeRequestDTO emailCertificationCodeRequestDTO){
        log.info("emailCertificationCode");
        return memberService.emailCertificationCode(emailCertificationCodeRequestDTO.getUsername(), emailCertificationCodeRequestDTO.getCode());
    }

    @GetMapping("/getUserInfo/{artist}/{nickname}")
    public ResponseEntity<OtherUserInfoResponse> getOtherUserInfo(@PathVariable String artist, @PathVariable String nickname, Authentication authentication){
        log.info("getOtherUserInfo");
        LoginInfoDto loginInfo = (LoginInfoDto) authentication.getPrincipal();
        return memberService.getOtherUserInfo(artist, nickname, loginInfo.getUsername());
    }

    // 회원이 작성한 게시글 전부
    @GetMapping("/{artist}/{nickname}/board")
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoBoard(@PathVariable String artist, @PathVariable String nickname, Authentication authentication){
        log.info("getUserInfo");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return memberService.getOtherUserInfoBoard(artist, nickname, loginInfoDto.getUsername());
    }


    // 회원이 작성한 댓글
    @GetMapping("/{artist}/{nickname}/comment")
    public ResponseEntity<List<UserInfoCommentResponse>> getUserInfoMyComment(@PathVariable String artist, @PathVariable String nickname, Authentication authentication){
        log.info("getUserInfoMyComment");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return memberService.getUserInfoMyComment(artist, nickname, loginInfoDto.getUsername());
    }


    // 회원이 좋아요 누른 게시글 전부
    @GetMapping("/{artist}/{nickname}/like")
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoMyLike(@PathVariable String artist, @PathVariable String nickname, Authentication authentication){
        log.info("getUserInfoMyLike");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return memberService.getUserInfoMyLike(artist, nickname, loginInfoDto.getUsername());
    }

    // 회원이 좋아요 누른 판매 상품
    @GetMapping("/{artist}/{nickname}/market")
    public ResponseEntity<List<MarketPostListResponseDTO>> getUserInfoMyMarket(@PathVariable String artist, @PathVariable String nickname, Authentication authentication){
        log.info("getUserInfoMyMarket");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return memberService.getUserInfoMyMarket(artist, nickname, loginInfoDto.getUsername());
    }

}
