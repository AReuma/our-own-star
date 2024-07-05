package com.example.backend.artistBoard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequestDTO {

    @Min(value = 0, message = "Value must be between 0 and 3")
    @Max(value = 3, message = "Value must be between 0 and 3")
    private int choice;
}
