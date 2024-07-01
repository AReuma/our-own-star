package com.example.backend.member.service.myPage;

import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.market.MarketPostListResponseDTO;
import com.example.backend.member.dto.mypage.UserInfoCommentResponse;
import com.example.backend.member.dto.mypage.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyPageService {
    ResponseEntity<UserInfoResponse> getUserInfo(String artist, String username);

    ResponseEntity<List<PostListResponseDTO>> getUserInfoMyBoard(String artist, String username);

    ResponseEntity<List<UserInfoCommentResponse>> getUserInfoMyComment(String artist, String username);

    ResponseEntity<List<PostListResponseDTO>> getUserInfoMyLike(String artist, String username);

    ResponseEntity<List<MarketPostListResponseDTO>> getUserInfoMyMarket(String artist, String username);

    ResponseEntity<Boolean> checkNicknameDub(String artist, String nickname, String username);

    ResponseEntity<String> modifyUserInfo(String artist, String nickname, String userInfo, MultipartFile userProfileImg, String username);
}
