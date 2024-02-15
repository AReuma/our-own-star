package com.example.backend.artistBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostVoteResponseDTO {

    private List<String> choice;

    private int choiceCount;

    private LocalDateTime expireTime;
}
