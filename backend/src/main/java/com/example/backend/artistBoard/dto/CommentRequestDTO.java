package com.example.backend.artistBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    private String comment;
    private Long boardCommentId;
}
