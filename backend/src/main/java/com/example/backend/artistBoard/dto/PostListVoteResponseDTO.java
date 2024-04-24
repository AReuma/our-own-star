package com.example.backend.artistBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListVoteResponseDTO {

    private List<String> choice;

    private int choiceCount;

    private LocalDateTime expireTime;
}
