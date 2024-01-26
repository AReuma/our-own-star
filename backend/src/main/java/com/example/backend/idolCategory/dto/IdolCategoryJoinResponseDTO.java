package com.example.backend.idolCategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdolCategoryJoinResponseDTO {

    @NotBlank(message = "artist not blank")
    @Schema(name = "아티스트 이름", example = "EXO")
    private String artist;
}
