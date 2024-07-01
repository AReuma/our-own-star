package com.example.backend.artistBoard.service;

import com.example.backend.artistBoard.dto.CommentResponseDTO;
import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.PostResponseDTO;
import com.example.backend.artistBoard.dto.SearchUserNicknameResponseDTO;
import com.example.backend.artistBoard.dto.market.*;
import com.example.backend.artistBoard.repository.ArtistPlaceResponse;
import com.example.backend.member.dto.LoginInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ArtistBoardService {
    ResponseEntity<String> saveImgPost(String artist, String content, List<MultipartFile> multipartFile, String username);

    ResponseEntity<String> savePost(String artist, String content, String username);

    ResponseEntity<String> saveVotePost(String artist, String content, List<Map<String, String>> voteChoice, int voteDay, int voteHour, String username);

    ResponseEntity<List<PostListResponseDTO>> getUserFollowPost(String username, String artist, Integer page);

    ResponseEntity<Integer> getUserFollowPostTotalPage(String username, String artist);

    ResponseEntity<PostResponseDTO> readArtistBoard(String artist, Long boardId, String username);

    ResponseEntity<String> saveComment(String artist, Long boardId, String username, String comment, Long boardCommentId);

    ResponseEntity<List<CommentResponseDTO>> readArtistBoardComment(String artist, Long boardId, String username);

    ResponseEntity<String> deleteComment(String artist, Long boardId, String username, Long boardCommentId);

    ResponseEntity<String> boardLike(String artist, Long boardId, String username);

    ResponseEntity<String> boardBookmark(String artist, Long boardId, String username);

    ResponseEntity<String> saveMarketBoard(String title, String artist, String content, String price, List<MultipartFile> multipartFile, String username);

    ResponseEntity<MarketPostResponseDTO> readMarketBoard(String artist, Long boardId, String username);

    ResponseEntity<Integer> getMarketPostTotalPage(String artist, String username);

    ResponseEntity<List<MarketPostListResponseDTO>> readMarketBoardList(String artist, String nickname, Integer page, String username);

    ResponseEntity<String> marketBoardLike(String artist, Long boardId, String username);

    ResponseEntity<String> marketBoardBookmark(String artist, Long boardId, String username);

    ResponseEntity<String> modifyMarketBoardStatus(String artist, Long boardId, String boardStatus, String username);

    ResponseEntity<String> deleteMarketBoard(String artist, Long boardId, String username);

    ResponseEntity<MarketBoardModifyResponseDTO> getModifyMarketBoard(String artist, Long boardId, String username);

    //ResponseEntity<String> modifyMarketBoard(String artist, Long sellNum, String username, String title, String price, String content, List<Integer> deleteImgList);
    ResponseEntity<String> modifyMarketBoard(String artist, Long sellNum, String username, String title, String price, String content, List<Long> deleteImgList, List<MultipartFile> addImgList);

    ResponseEntity<List<PostListResponseDTO>> getPopularBoard(String artist, String username);

    ResponseEntity<String> saveCommentLike(String artist, Long boardId, Long commentId, String username);

    ResponseEntity<String> saveCommentBookmark(String artist, Long boardNum, Long commentId, String username);

    ResponseEntity<String> saveMarketComment(String artist, Long boardId, String username, String comment, Long boardCommentId);

    ResponseEntity<List<MarketCommentResponseDTO>> readMarketBoardComment(String artist, Long sellNum, String username);

    ResponseEntity<String> saveMarketCommentLike(String artist, Long sellNum, Long commentId, String username);

    ResponseEntity<String> saveMarketCommentBookmark(String artist, Long sellNum, Long commentId, String username);

    ResponseEntity<String> deleteMarketComment(String artist, Long boardId, String username, Long boardCommentId);

    ResponseEntity<String> savePlace(String artist, String lat, String lng, String placeName, String addressName, String roadAddressName, String placeUrl, String memo, MultipartFile multipartFile, String username);

    ResponseEntity<List<ArtistPlaceResponse>> getArtistPlace(String artist, String username);

    ResponseEntity<List<PostListResponseDTO>> getSearchBoard(String artist, String keyword, String username);

    ResponseEntity<List<MarketPostListResponseDTO>> getSearchMarketBoard(String artist, String keyword, String username);

    ResponseEntity<List<SearchUserNicknameResponseDTO>> getSearchKeyword(String artist, String keyword, String username);
}
