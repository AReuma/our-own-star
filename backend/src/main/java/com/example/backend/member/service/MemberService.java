package com.example.backend.member.service;

import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.market.MarketPostListResponseDTO;
import com.example.backend.member.dto.EmailCertificationRequestDTO;
import com.example.backend.member.dto.LoginResponseDTO;
import com.example.backend.member.dto.OtherUserInfoResponse;
import com.example.backend.member.dto.mypage.UserInfoCommentResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MemberService {
    ResponseEntity<LoginResponseDTO> login(String username, String password);

    ResponseEntity<String> register(String username, String password, String nickname, String phoneNum);

    ResponseEntity<Boolean> checkUsername(String username);

    ResponseEntity<String> emailCertification(EmailCertificationRequestDTO dto);

    ResponseEntity<Boolean> emailCertificationCode(String username, String code);

    ResponseEntity<OtherUserInfoResponse> getOtherUserInfo(String artist, String nickname, String username);

    ResponseEntity<List<PostListResponseDTO>> getOtherUserInfoBoard(String artist, String nickname, String username);

    ResponseEntity<List<UserInfoCommentResponse>> getUserInfoMyComment(String artist, String nickname, String username);

    ResponseEntity<List<PostListResponseDTO>> getUserInfoMyLike(String artist, String nickname, String username);

    ResponseEntity<List<MarketPostListResponseDTO>> getUserInfoMyMarket(String artist, String nickname, String username);
}
