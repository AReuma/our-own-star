package com.example.backend.idolCategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IdolCategoryJoinRequestDTO {

    @NotBlank(message = "artistId not blank")
    @Schema(name = "아티스트 번호", example = "1")
    private Long id;
}
