package com.example.backend.artistBoard.service;

import com.example.backend.artistBoard.dto.PostListResponseDTO;
import com.example.backend.artistBoard.dto.PostResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
}
