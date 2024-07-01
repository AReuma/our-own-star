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
public class PostVoteResponseDTO {

    private List<String> choice;

    private int choiceCount;

    private boolean hasVoted;

    private boolean expireVote;

    private String userVoteChoice;

    private Map<String, String> voteResults; // 투표 결과

    private LocalDateTime expireTime;
}
