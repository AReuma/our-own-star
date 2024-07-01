package com.example.backend.artistBoard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveVotePostRequestDTO {

    private String content;
    private List<Map<String, String>> voteChoice;
    private int voteDay;
    private int voteHour;
}
