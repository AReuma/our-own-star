package com.example.backend.member.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoCommentResponse {

    private Long boardId;
    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createDate;
    private String writerProfile;
    private boolean isLike;
    private Integer likeCount;
    private boolean isBookmark;
    private Integer bookmarkCount;
    private Integer commentCount;
}
