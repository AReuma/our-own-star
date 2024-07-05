package com.example.backend.artistBoard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SaveVotePostRequestDTO {

    @NotBlank
    private String content;

    private List<Map<String, String>> voteChoice;

    @Min(value = 1, message = "Value must be between 1 and 10")
    @Max(value = 10, message = "Value must be between 1 and 10")
    private int voteDay;

    @Min(value = 1, message = "Value must be between 1 and 10")
    @Max(value = 10, message = "Value must be between 1 and 10")
    private int voteHour;
}
