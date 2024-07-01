package com.example.backend.member.controller;

import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.market.MarketPostListResponseDTO;
import com.example.backend.member.dto.LoginInfoDto;
import com.example.backend.member.dto.mypage.UserInfoCommentResponse;
import com.example.backend.member.dto.mypage.UserInfoResponse;
import com.example.backend.member.service.myPage.MyPageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/myPage")
@Tag(name = "MyPageController", description = "회원 개인 정보를 관리하는 API")
public class MyPageController {

    private final MyPageService myPageService;

    // 회원의 기본 정보 가져오기
    @GetMapping("/{artist}/getMyInfo")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable String artist, Authentication authentication){
        log.info("getUserInfo");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return myPageService.getUserInfo(artist, loginInfoDto.getUsername());
    }

    // 회원이 작성한 게시글 전부
    @GetMapping("/{artist}/getMyInfo/board")
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoBoard(@PathVariable String artist, Authentication authentication){
        log.info("getUserInfo");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return myPageService.getUserInfoMyBoard(artist, loginInfoDto.getUsername());
    }


    // 회원이 작성한 댓글
    @GetMapping("/{artist}/getMyInfo/comment")
    public ResponseEntity<List<UserInfoCommentResponse>> getUserInfoMyComment(@PathVariable String artist, Authentication authentication){
        log.info("getUserInfoMyComment");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return myPageService.getUserInfoMyComment(artist, loginInfoDto.getUsername());
    }


    // 회원이 좋아요 누른 게시글 전부
    @GetMapping("/{artist}/getMyInfo/like")
    public ResponseEntity<List<PostListResponseDTO>> getUserInfoMyLike(@PathVariable String artist, Authentication authentication){
        log.info("getUserInfoMyLike");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return myPageService.getUserInfoMyLike(artist, loginInfoDto.getUsername());
    }

    // 회원이 좋아요 누른 판매 상품
    @GetMapping("/{artist}/getMyInfo/market")
    public ResponseEntity<List<MarketPostListResponseDTO>> getUserInfoMyMarket(@PathVariable String artist, Authentication authentication){
        log.info("getUserInfoMyMarket");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return myPageService.getUserInfoMyMarket(artist, loginInfoDto.getUsername());
    }

    // 닉네임 중복 체크
    @GetMapping("/{artist}/getMyInfo/modify/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDub(@PathVariable String artist, @PathVariable String nickname, Authentication authentication){
        log.info("checkNicknameDub");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return myPageService.checkNicknameDub(artist, nickname, loginInfoDto.getUsername());
    }

    @PatchMapping("/{artist}/getMyInfo/modify")
    public ResponseEntity<String> modifyUserProfileInfo(@PathVariable String artist,
                                                        @RequestParam("nickname") String nickname,
                                                        @RequestParam("userInfo") String userInfo,
                                                        @RequestPart(value = "userProfileImg", required = false) MultipartFile userProfileImg,
                                                        Authentication authentication){
        log.info("modifyUserProfileInfo");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();

        /*log.info("userProfileImg: "+ userProfileImg);
        log.info("artist: "+ artist);
        log.info("nickname: "+ nickname);
        log.info("userInfo: "+ userInfo);*/

        return myPageService.modifyUserInfo(artist, nickname, userInfo, userProfileImg, loginInfoDto.getUsername());
    }
}
