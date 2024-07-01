package com.example.backend.artistBoard.dto;

import com.example.backend.artistBoard.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDTO {

    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createDate;
    private BoardType boardType;
    private String writerProfile;
    private boolean isLike;
    private Integer likeCount;
    private boolean isBookmark;
    private Integer bookmarkCount;
    private Integer commentCount;

    //vote
    private Optional<PostListVoteResponseDTO> postVoteResponseDTO = Optional.empty();

    //image
    private Optional<List<String>> image = Optional.empty();

}
